package uk.ac.ic.doc.blocc;

import com.owlike.genson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Objects;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType()
public class Measurement {

  @Property() private final float temperature;

  @Property() private final float relativeHumidity;

  @Property() private final Instant time;

  public Measurement(
      @JsonProperty("temperature") final float temperature,
      @JsonProperty("relativeHumidity") final float relativeHumidity,
      @JsonProperty("measureTime") final long timeEpochSeconds) {
    this.temperature = temperature;
    this.relativeHumidity = relativeHumidity;
    this.time = Instant.ofEpochSecond(timeEpochSeconds);
  }

  public float getTemperature() {
    return temperature;
  }

  public float getRelativeHumidity() {
    return relativeHumidity;
  }

  public Instant getTime() {
    return time;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }

    if (getClass() != obj.getClass()) {
      return false;
    }

    Measurement that = (Measurement) obj;

    return this.getTime() == that.getTime()
        && this.getTemperature() == that.getTemperature()
        && this.getRelativeHumidity() == that.getRelativeHumidity();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTime(), getTemperature(), getRelativeHumidity());
  }

  @Override
  public String toString() {
    return String.format(
        "%s@%s [time=%s, temperature=%f, relativeHumidity=%f]",
        getClass().getSimpleName(),
        Integer.toHexString(hashCode()),
        time,
        temperature,
        relativeHumidity);
  }
}
