package wolox.training.web.dtos.deserializers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * A {@link com.fasterxml.jackson.databind.JsonDeserializer} to create {@link LocalDate} instances
 * from ISO 8601 format {@link String}s.
 */
public class IsoLocalDateDeserializer extends StdDeserializer<LocalDate> {

    protected IsoLocalDateDeserializer() {
        super(LocalDate.class);
    }

    @Override
    public LocalDate deserialize(final JsonParser parser, final DeserializationContext context)
        throws IOException {
        final var dateString = parser.getText();
        try {
            return LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(dateString));
        } catch (final DateTimeParseException e) {
            throw new JsonParseException(parser, "Unable to deserialize the date", e);
        }
    }
}
