package uk.ac.ic.doc.blocc;

import com.owlike.genson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Objects;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType()
public class TemperatureHumidityReading {

  @Property() private final float temperature;

  @Property() private final float relativeHumidity;

  @Property() private final long timestamp;

  /**
   * A reading taken from a temperature and humidity sensor.
   *
   * @param temperature temperature in degree celsius
   * @param relativeHumidity relative humidity in percentage
   * @param timestamp timestamp in epoch seconds
   */
  public TemperatureHumidityReading(
      @JsonProperty("temperature") final float temperature,
      @JsonProperty("relativeHumidity") final float relativeHumidity,
      @JsonProperty("timestamp") final long timestamp) {
    this.temperature = temperature;
    this.relativeHumidity = relativeHumidity;
    this.timestamp = timestamp;
  }

  public float getTemperature() {
    return temperature;
  }

  public float getRelativeHumidity() {
    return relativeHumidity;
  }

  public long getTimestamp() {
    return timestamp;
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

    TemperatureHumidityReading that = (TemperatureHumidityReading) obj;

    return this.getTimestamp() == that.getTimestamp()
        && this.getTemperature() == that.getTemperature()
        && this.getRelativeHumidity() == that.getRelativeHumidity();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTimestamp(), getTemperature(), getRelativeHumidity());
  }

  @Override
  public String toString() {
    return String.format(
        "%s [timestamp=%s, temperature=%f, relativeHumidity=%f]",
        getClass().getSimpleName(),
        Instant.ofEpochSecond(timestamp),
        temperature,
        relativeHumidity);
  }
}
