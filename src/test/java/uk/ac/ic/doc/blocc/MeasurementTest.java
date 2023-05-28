package uk.ac.ic.doc.blocc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MeasurementTest {
  Measurement measurement1 = new Measurement(1.0f, 0.82f, 0L);

  @Nested
  class Equality {
    Measurement measurement2 = new Measurement(1.0f, 0.82f, 0L);
    Measurement measurement3 = new Measurement(1.0f, 0.82f, 0L);

    @Test
    public void isReflexive() {
      assertThat(measurement1).isEqualTo(measurement1);
    }

    @Test
    public void isSymmetric() {
      assertThat(measurement1).isEqualTo(measurement2);
      assertThat(measurement2).isEqualTo(measurement1);
    }

    @Test
    public void isTransitive() {
      assertThat(measurement1).isEqualTo(measurement2);
      assertThat(measurement2).isEqualTo(measurement3);
      assertThat(measurement1).isEqualTo(measurement3);
    }

    @Test
    public void handlesInequality() {
      Measurement measurement4 = new Measurement(2.0f, 0.82f, 0L);

      assertThat(measurement1).isNotEqualTo(measurement4);
    }

    @Test
    public void handlesNull() {
      assertThat(measurement1).isNotEqualTo(null);
    }
  }

  @Test
  public void toStringIdentifiesAsset() {
    assertThat(measurement1.toString())
        .isEqualTo(
            "Measurement@efd25fe4 [time=1970-01-01T00:00:00Z, temperature=1.000000, relativeHumidity=0.820000]");
  }
}
