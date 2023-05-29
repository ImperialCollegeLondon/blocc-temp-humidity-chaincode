package uk.ac.ic.doc.blocc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TemperatureHumidityReadingContractTest {

  private final TemperatureHumidityReadingContract contract =
      new TemperatureHumidityReadingContract();
  private final Context ctx = mock(Context.class);
  private final ChaincodeStub stub = mock(ChaincodeStub.class);

  @BeforeEach
  public void setUp() {
    when(ctx.getStub()).thenReturn(stub);
  }

  @Nested
  class InvokeAddReadingTransaction {

    @Test
    public void addsNewReadingToLedger() {
      // No reading at epoch 0L exists in the ledger
      when(stub.getStringState(String.valueOf(0L))).thenReturn("");

      TemperatureHumidityReading reading = contract.addReading(ctx, 1f, 0.9f, 0L);

      assertThat(reading).isEqualTo(new TemperatureHumidityReading(1f, 0.9f, 0L));
    }

    @Test
    public void throwsWhenAddingDuplicatedReadingToLedger() {
      // A reading at epoch 0L exists in the ledger
      String existedReading =
          String.format(
              "{ \"temperature\": %f, \"time\": \"%s\", \"relativeHumidity\": %f }",
              0.1f, Instant.ofEpochSecond(0L), 0.9f);
      when(stub.getStringState(String.valueOf(0L))).thenReturn(existedReading);

      Throwable thrown = catchThrowable(() -> contract.addReading(ctx, 1f, 0.9f, 0L));

      String msg = String.format("Reading at %s already exist", Instant.ofEpochSecond(0L));
      assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause().hasMessage(msg);
      assertThat(((ChaincodeException) thrown).getPayload())
          .isEqualTo("READING_ALREADY_EXISTS".getBytes());
    }
  }
}
