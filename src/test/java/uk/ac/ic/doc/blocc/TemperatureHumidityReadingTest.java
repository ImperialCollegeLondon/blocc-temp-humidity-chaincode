package uk.ac.ic.doc.blocc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TemperatureHumidityReadingTest {
  TemperatureHumidityReading reading1 = new TemperatureHumidityReading(1.0f, 0.82f, 0L);

  @Nested
  class Equality {
    TemperatureHumidityReading reading2 = new TemperatureHumidityReading(1.0f, 0.82f, 0L);
    TemperatureHumidityReading reading3 = new TemperatureHumidityReading(1.0f, 0.82f, 0L);

    @Test
    public void isReflexive() {
      assertThat(reading1).isEqualTo(reading1);
    }

    @Test
    public void isSymmetric() {
      assertThat(reading1).isEqualTo(reading2);
      assertThat(reading2).isEqualTo(reading1);
    }

    @Test
    public void isTransitive() {
      assertThat(reading1).isEqualTo(reading2);
      assertThat(reading2).isEqualTo(reading3);
      assertThat(reading1).isEqualTo(reading3);
    }

    @Test
    public void handlesInequality() {
      TemperatureHumidityReading reading4 = new TemperatureHumidityReading(2.0f, 0.82f, 0L);

      assertThat(reading1).isNotEqualTo(reading4);
    }

    @Test
    public void handlesNull() {
      assertThat(reading1).isNotEqualTo(null);
    }
  }

  @Test
  public void toStringIdentifiesReading() {
    assertThat(reading1.toString())
        .isEqualTo(
            "TemperatureHumidityReading@efd25fe4 [time=1970-01-01T00:00:00Z, temperature=1.000000, relativeHumidity=0.820000]");
  }
}
