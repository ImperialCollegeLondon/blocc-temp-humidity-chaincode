package uk.ac.ic.doc.blocc;

import static org.assertj.core.api.Assertions.assertThat;

import com.owlike.genson.Converter;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import java.time.Instant;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TemperatureHumidityReadingTest {
  TemperatureHumidityReading reading1 = new TemperatureHumidityReading(1.0f, 0.82f, 1L);

  @Nested
  class Equality {
    TemperatureHumidityReading reading2 = new TemperatureHumidityReading(1.0f, 0.82f, 1L);
    TemperatureHumidityReading reading3 = new TemperatureHumidityReading(1.0f, 0.82f, 1L);

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
      TemperatureHumidityReading reading4 = new TemperatureHumidityReading(2.0f, 0.82f, 1L);

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
            "TemperatureHumidityReading [time=1970-01-01T00:00:01Z, temperature=1.000000, relativeHumidity=0.820000]");
  }

  @Test
  public void validJsonStringDeserializesToReading() {
    String readingJson =
        String.format(
            "{ \"temperature\": %f, \"time\": \"%s\", \"relativeHumidity\": %f }", 1f, 1L, 0.82f);
    Genson genson =
        new GensonBuilder()
            .withConverter(
                new Converter<>() {
                  @Override
                  public void serialize(
                      Instant object, ObjectWriter writer, com.owlike.genson.Context ctx) {
                    writer.writeValue(object.getEpochSecond());
                  }

                  @Override
                  public Instant deserialize(ObjectReader reader, com.owlike.genson.Context ctx) {
                    return Instant.ofEpochSecond(reader.valueAsLong());
                  }
                },
                Instant.class)
            .create();

    assertThat(reading1)
        .isEqualTo(genson.deserialize(readingJson, TemperatureHumidityReading.class));
  }
}
