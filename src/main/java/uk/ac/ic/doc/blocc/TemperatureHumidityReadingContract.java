package uk.ac.ic.doc.blocc;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.contract.annotation.Transaction.TYPE;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

@Contract(
    name = "blocc-temp-humidity-reading",
    info =
        @Info(
            title = "BLOCC Temperature and Humidity Sensor Reading Contract",
            description =
                "A smart contract to record the temperature and humidity sensor reading taken at nodes",
            contact = @Contact(email = "tony.wu122@imperial.ac.uk", name = "Tony Wu")))
@Default
public class TemperatureHumidityReadingContract {

  // Build a Genson that can serialize/deserialize Instant to/from long (epoch)
  private final Genson genson =
      new GensonBuilder().withConverter(new InstantConverter(), Instant.class).create();

  private enum Errors {
    READING_NOT_FOUND,
    READING_ALREADY_EXISTS
  }

  @Transaction(intent = Transaction.TYPE.EVALUATE)
  public boolean readingExists(final Context ctx, final long timeEpochSeconds) {
    ChaincodeStub stub = ctx.getStub();
    String readingJson = stub.getStringState(String.valueOf(timeEpochSeconds));

    return (readingJson != null && !readingJson.isEmpty());
  }

  /**
   * Record a temperature and humidity reading to the ledger.
   *
   * @param ctx the transaction ledger
   * @param temperature the temperature reading in degree celsius
   * @param relativeHumidity the relative humidity reading in percentage
   * @param timeEpochSeconds timestamp of the reading
   * @return the {@code TemperatureHumidityReading} added to ledger
   * @throws ChaincodeException when a reading at the same {@code timeEpochSeconds} exists already
   */
  @Transaction(intent = TYPE.SUBMIT)
  public TemperatureHumidityReading addReading(
      final Context ctx,
      final float temperature,
      final float relativeHumidity,
      final long timeEpochSeconds)
      throws ChaincodeException {
    ChaincodeStub stub = ctx.getStub();

    if (readingExists(ctx, timeEpochSeconds)) {
      String errorMessage =
          String.format("Reading at %s already exist", Instant.ofEpochSecond(timeEpochSeconds));
      System.out.println(errorMessage);
      throw new ChaincodeException(errorMessage, Errors.READING_ALREADY_EXISTS.toString());
    }

    TemperatureHumidityReading reading =
        new TemperatureHumidityReading(temperature, relativeHumidity, timeEpochSeconds);

    // Use Genson to convert the Measurement into string, sort it alphabetically and serialise it
    // into a JSON string
    String sortedJson = genson.serialize(reading);
    // Use timestamp as the unique identifier since there should only be one measurement at a second
    stub.putStringState(String.valueOf(timeEpochSeconds), sortedJson);

    return reading;
  }

  /**
   * Retrieve the reading at a given time epoch second
   *
   * @param ctx the transaction context
   * @param timeEpochSeconds a given time epoch second
   * @return the retrieved {@code TemperatureHumidityReading}
   * @throws ChaincodeException when reading at the given time is not found
   */
  public TemperatureHumidityReading getReading(Context ctx, long timeEpochSeconds)
      throws ChaincodeException {
    ChaincodeStub stub = ctx.getStub();
    String readingJson = stub.getStringState(String.valueOf(timeEpochSeconds));

    if (readingJson == null || readingJson.isEmpty()) {
      String errorMessage =
          String.format("Reading at %s is not found", Instant.ofEpochSecond(timeEpochSeconds));
      System.out.println(errorMessage);
      throw new ChaincodeException(errorMessage, Errors.READING_NOT_FOUND.toString());
    }

    return genson.deserialize(readingJson, TemperatureHumidityReading.class);
  }

  public Iterable<TemperatureHumidityReading> getAllReadings(Context ctx) {
    ChaincodeStub stub = ctx.getStub();

    List<TemperatureHumidityReading> allReadings = new ArrayList<>();

    // Get all readings
    QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

    for (KeyValue result : results) {
      TemperatureHumidityReading reading =
          genson.deserialize(result.getStringValue(), TemperatureHumidityReading.class);
      System.out.println(reading);
      allReadings.add(reading);
    }

    return allReadings;
  }
}
