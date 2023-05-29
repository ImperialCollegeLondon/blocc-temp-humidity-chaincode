package uk.ac.ic.doc.blocc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TemperatureHumidityReadingContractTest {

  private final TemperatureHumidityReadingContract contract =
      new TemperatureHumidityReadingContract();
  private final Context ctx = mock(Context.class);
  private final ChaincodeStub stub = mock(ChaincodeStub.class);
  private final String existedReading =
      String.format(
          "{ \"temperature\": %f, \"time\": \"%s\", \"relativeHumidity\": %f }", 0.1f, 1L, 0.9f);

  @BeforeEach
  public void setUp() {
    when(ctx.getStub()).thenReturn(stub);
  }

  @Nested
  class InvokeAddReadingTransaction {

    @Test
    public void addsNewReadingToLedger() {
      when(stub.getStringState(String.valueOf(1L))).thenReturn("");

      TemperatureHumidityReading reading = contract.addReading(ctx, 1f, 0.9f, 1L);

      assertThat(reading).isEqualTo(new TemperatureHumidityReading(1f, 0.9f, 1L));
    }

    @Test
    public void throwsWhenAddingDuplicatedReadingToLedger() {
      when(stub.getStringState(String.valueOf(1L))).thenReturn(existedReading);

      Throwable thrown = catchThrowable(() -> contract.addReading(ctx, 1f, 0.9f, 1L));

      String msg = String.format("Reading at %s already exist", Instant.ofEpochSecond(1L));
      assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause().hasMessage(msg);
      assertThat(((ChaincodeException) thrown).getPayload())
          .isEqualTo("READING_ALREADY_EXISTS".getBytes());
    }
  }

  @Nested
  class InvokeGetReadingTransaction {

    @Test
    public void throwsWhenReadingNotFound() {
      when(stub.getStringState(String.valueOf(1L))).thenReturn("");

      Throwable thrown = catchThrowable(() -> contract.getReading(ctx, 1L));

      String msg = String.format("Reading at %s is not found", Instant.ofEpochSecond(1L));
      assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause().hasMessage(msg);
      assertThat(((ChaincodeException) thrown).getPayload())
          .isEqualTo("READING_NOT_FOUND".getBytes());
    }

    @Test
    public void retrievesExistingReading() {
      when(stub.getStringState(String.valueOf(1L))).thenReturn(existedReading);

      TemperatureHumidityReading reading = contract.getReading(ctx, 1L);

      assertThat(reading).isEqualTo(new TemperatureHumidityReading(0.1f, 0.9f, 1L));
    }
  }

  @Nested
  class InvokeGetAllReadingsTransaction {

    private final MockAssetResultsIterator mockedResults = new MockAssetResultsIterator();

    private final class MockKeyValue implements KeyValue {

      private final String key;
      private final String value;

      MockKeyValue(final String key, final String value) {
        super();
        this.key = key;
        this.value = value;
      }

      @Override
      public String getKey() {
        return this.key;
      }

      @Override
      public String getStringValue() {
        return this.value;
      }

      @Override
      public byte[] getValue() {
        return this.value.getBytes();
      }
    }

    private final class MockAssetResultsIterator implements QueryResultsIterator<KeyValue> {

      private final List<KeyValue> results;

      MockAssetResultsIterator() {
        super();

        results = new ArrayList<>();
      }

      public void add(String key, String value) {
        results.add(new MockKeyValue(key, value));
      }

      @Override
      public @NotNull Iterator<KeyValue> iterator() {
        return results.iterator();
      }

      @Override
      public void close() {
        // do nothing
      }
    }

    @Test
    public void returnsEmptyIterableWhenNoReadingAvailable() {
      when(stub.getStateByRange("", "")).thenReturn(mockedResults);

      Iterable<TemperatureHumidityReading> reading = contract.getAllReadings(ctx);

      assertThat(reading).isEmpty();
    }

    @Test
    public void retrievesAllExistingReadings() {
      String anotherReading =
          String.format(
              "{ \"temperature\": %f, \"time\": \"%s\", \"relativeHumidity\": %f }", 20f, 100L, 4f);

      mockedResults.add(String.valueOf(1L), existedReading);
      mockedResults.add(String.valueOf(100L), anotherReading);

      when(stub.getStateByRange("", "")).thenReturn(mockedResults);

      Iterable<TemperatureHumidityReading> actualReadings = contract.getAllReadings(ctx);

      List<TemperatureHumidityReading> expectedReadings = new ArrayList<>();

      expectedReadings.add(new TemperatureHumidityReading(0.1f, 0.9f, 1L));
      expectedReadings.add(new TemperatureHumidityReading(20f, 4f, 100L));

      assertIterableEquals(actualReadings, expectedReadings);
    }
  }
}
