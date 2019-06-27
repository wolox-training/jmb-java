package wolox.training.utils;

import org.junit.jupiter.api.Assertions;
import wolox.training.models.Book;

/**
 * Class containing several assertions to be performed over a {@link Book} instance.
 */
public class BookAssertions {

    /**
     * Private constructor to avoid instantiation.
     */
    private BookAssertions() {
    }


    /**
     * Asserts that the {@code expected} and the {@code actual} {@link Book}s are the same. This
     * method is different that {@link Assertions#assertEquals(Object, Object)} as equality is not
     * checked using the {@link Book#equals(Object)} method, but by checking all the {@link Book}
     * fields.
     *
     * @param expected The expected {@link Book}.
     * @param actual The actual {@link Book}.
     * @param message A message to be displayed in case the assertion fails.
     */
    public static void assertSame(final Book expected, final Book actual, final String message) {
        Assertions.assertAll(
            message,
            () -> Assertions.assertEquals(
                expected.getId(),
                actual.getId(),
                "There is a mismatch in the id"
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
