package wolox.training.utils;

import org.junit.jupiter.api.Assertions;
import wolox.training.models.BlacklistedJwtToken;


/**
 * Class containing several assertions to be performed over a {@link BlacklistedJwtToken} instance.
 */
public class BlacklistedJwtTokenAssertions {

    /**
     * Private constructor to avoid instantiation.
     */
    private BlacklistedJwtTokenAssertions() {
    }


    /**
     * Asserts that the {@code expected} and the {@code actual} {@link BlacklistedJwtToken}s are the
     * same. This method is different that {@link Assertions#assertEquals(Object, Object)} as
     * equality is not checked using the {@link BlacklistedJwtToken#equals(Object)} method, but by
     * checking all the {@link BlacklistedJwtToken} fields.
     *
     * @param expected The expected {@link BlacklistedJwtToken}.
     * @param actual The actual {@link BlacklistedJwtToken}.
     * @param message A message to be displayed in case the assertion fails.
     */
    public static void assertSame(
        final BlacklistedJwtToken expected,
        BlacklistedJwtToken actual,
        final String message) {
        Assertions.assertAll(
            message,
            () -> Assertions.assertEquals(
                expected.getId(),
                actual.getId(),
                "There is a mismatch in the id"
            )
        );
    }
}
