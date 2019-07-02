package wolox.training.models;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.StringUtils;

/**
 * Class representing a blacklisted JWT token.
 */
@Getter
@EqualsAndHashCode(of = "id")
@ToString(doNotUseGetters = true)
public class BlacklistedJwtToken {

    /**
     * The id of the JWT token.
     */
    private final String id;


    /**
     * Default constructor for JPA Provider.
     */
    /* package */ BlacklistedJwtToken() {
        // Default constructor that sets final fields with default values
        // Real values will be set by JPA Provider
        this.id = null;
    }

    /**
     * Constructor.
     *
     * @param id The id of the JWT token.
     */
    public BlacklistedJwtToken(final String id) {
        assertId(id);
        this.id = id;
    }

    /**
     * Asserts the given {@code id}.
     *
     * @param id The id value to be asserted.
     * @throws NullPointerException If the given {@code id} is {@code null}
     * @throws IllegalArgumentException If the given {@code id} does not have text.
     */
    private static void assertId(final String id) {
        Preconditions.checkNotNull(id, "The id must not be null");
        Preconditions.checkArgument(StringUtils.hasText(id), "The id must have text");
    }
}
