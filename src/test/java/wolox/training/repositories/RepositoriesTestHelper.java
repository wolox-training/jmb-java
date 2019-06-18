package wolox.training.repositories;

import org.junit.jupiter.api.Assertions;
import wolox.training.models.Book;

/**
 * Helper class for persistence testing.
 */
public class RepositoriesTestHelper {

    /**
     * Private constructor to avoid instantiation.
     */
    private RepositoriesTestHelper() {
    }

    /**
     * Asserts that the {@code expected} and the {@code actual} {@link Book}s are the same. This
     * method is different that {@link Assertions#assertEquals(Object, Object)} as equality is not
     * checked using the {@link Object#equals(Object)} method, but by checking all the {@link Book}
     * fields.
     *
     * @param expected The expected {@link Book}.
     * @param actual The actual {@link Book}.
     * @param message A message to be displayed in case the assertion fails.
     */
    /* package */
    static void assertSameBook(final Book expected, final Book actual, final String message) {
        Assertions.assertAll(
            message,
            () -> Assertions.assertEquals(
                expected.getId(),
                actual.getId(),
                "The book has not been stored"
            ),
            () -> Assertions.assertEquals(
                expected.getGenre(),
                actual.getGenre(),
                "There is a mismatch in the genre"
            ),
            () -> Assertions.assertEquals(
                expected.getAuthor(),
                actual.getAuthor(),
                "There is a mismatch in the author"
            ),
            () -> Assertions.assertEquals(
                expected.getImage(),
                actual.getImage(),
                "There is a mismatch in the image"
            ),
            () -> Assertions.assertEquals(
                expected.getTitle(),
                actual.getTitle(),
                "There is a mismatch in the title"
            ),
            () -> Assertions.assertEquals(
                expected.getSubtitle(),
                actual.getSubtitle(),
                "There is a mismatch in the subtitle"
            ),
            () -> Assertions.assertEquals(
                expected.getPublisher(),
                actual.getPublisher(),
                "There is a mismatch in the publisher"
            ),
            () -> Assertions.assertEquals(
                expected.getYear(),
                actual.getYear(),
                "There is a mismatch in the year"
            ),
            () -> Assertions.assertEquals(
                expected.getPages(),
                actual.getPages(),
                "There is a mismatch in the pages"
            ),
            () -> Assertions.assertEquals(
                expected.getIsbn(),
                actual.getIsbn(),
                "There is a mismatch in the isbn"
            )
        );
    }

}
