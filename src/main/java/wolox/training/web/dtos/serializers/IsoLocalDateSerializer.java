package wolox.training.web.dtos.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A {@link com.fasterxml.jackson.databind.JsonSerializer} to transform a {@link LocalDate}
 * instances into ISO 8601 format {@link String}s.
 */
public class IsoLocalDateSerializer extends StdSerializer<LocalDate> {

    protected IsoLocalDateSerializer() {
        super(LocalDate.class);
    }

    @Override
    public void serialize(
        final LocalDate value,
        final JsonGenerator generator,
        final SerializerProvider provider) throws IOException {
        generator.writeString(DateTimeFormatter.ISO_LOCAL_DATE.format(value));
    }
}
