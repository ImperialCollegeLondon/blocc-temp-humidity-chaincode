package uk.ac.ic.doc.blocc;

import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;
import java.time.Instant;

public class InstantConverter implements Converter<Instant> {

  @Override
  public void serialize(Instant object, ObjectWriter writer, com.owlike.genson.Context ctx) {
    writer.writeValue(object.getEpochSecond());
  }

  @Override
  public Instant deserialize(ObjectReader reader, com.owlike.genson.Context ctx) {
    return Instant.ofEpochSecond(reader.valueAsLong());
  }
}
