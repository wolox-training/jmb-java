package wolox.training.utils;

import org.junit.jupiter.api.Assertions;
import wolox.training.models.Book;
import wolox.training.models.User;

/**
 * Class containing several assertions to be performed over a {@link User} instance.
 */
public class UserAssertions {

    /**
     * Private constructor to avoid instantiation.
     */
    private UserAssertions() {
    }


    /**
     * Asserts that the {@code expected} and the {@code actual} {@link User}s are the same. This
     * method is different that {@link Assertions#assertEquals(Object, Object)} as equality is not
     * checked using the {@link User#equals(Object)} method, but by checking all the {@link User}
     * fields.
     *
     * @param expected The expected {@link Book}.
     * @param actual The actual {@link Book}.
     * @param message A message to be displayed in case the assertion fails.
     */
    public static void assertSame(final User expected, final User actual, final String message) {
        Assertions.assertAll(
            message,
            () -> Assertions.assertEquals(
                expected.getId(),
                actual.getId(),
                "There is a mismatch in the id"
            ),
            () -> Assertions.assertEquals(
                expected.getUsername(),
                actual.getUsername(),
                "There is a mismatch in the username"
            ),
            () -> Assertions.assertEquals(
                expected.getName(),
                actual.getName(),
                "There is a mismatch in the name"
            ),
            () -> Assertions.assertEquals(
                expected.getBirthDate(),
                actual.getBirthDate(),
                "There is a mismatch in the birth date"
            )
        );
    }
}
