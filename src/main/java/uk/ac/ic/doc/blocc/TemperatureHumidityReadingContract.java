package uk.ac.ic.doc.blocc;

import com.owlike.genson.Genson;
import java.time.Instant;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.contract.annotation.Transaction.TYPE;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

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

  private final Genson genson = new Genson();

  private enum Errors {
    READING_ALREADY_EXISTS
  }

  @Transaction(intent = Transaction.TYPE.EVALUATE)
  public boolean readingExists(final Context ctx, final long timeEpochSeconds) {
    ChaincodeStub stub = ctx.getStub();
    String readingJson = stub.getStringState(String.valueOf(timeEpochSeconds));

    return (readingJson != null && !readingJson.isEmpty());
  }

  @Transaction(intent = TYPE.SUBMIT)
  public TemperatureHumidityReading addReading(
      final Context ctx,
      final float temperature,
      final float relativeHumidity,
      final long timeEpochSeconds) {
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
}
