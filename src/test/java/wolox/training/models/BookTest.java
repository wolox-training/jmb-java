package wolox.training.models;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wolox.training.utils.ValuesGenerator;

/**
 * Testing for the {@link Book} class.
 */
class BookTest {


    @Test
    @DisplayName("Create Book - Valid arguments")
    void testCreateBookWithValidArguments() {
        Assertions.assertDoesNotThrow(
            () -> new Book(
                ValuesGenerator.validBookGenre(),
                ValuesGenerator.validBookAuthor(),
                ValuesGenerator.validBookImage(),
                ValuesGenerator.validBookTitle(),
                ValuesGenerator.validBookSubtitle(),
                ValuesGenerator.validBookPublisher(),
                ValuesGenerator.validBookYear(),
                ValuesGenerator.validBookPages(),
                ValuesGenerator.validBookIsbn()
            ),
            "Creating a Book with valid values is throwing an unexpected Exception"
        );
    }

    @Test
    @DisplayName("Create Book - Null author")
    void testCreateBookWithNullAuthor() {
        Assertions.assertThrows(
            NullPointerException.class,
            () -> new Book(
                ValuesGenerator.validBookGenre(),
                null,
                ValuesGenerator.validBookImage(),
                ValuesGenerator.validBookTitle(),
                ValuesGenerator.validBookSubtitle(),
                ValuesGenerator.validBookPublisher(),
                ValuesGenerator.validBookYear(),
                ValuesGenerator.validBookPages(),
                ValuesGenerator.validBookIsbn()
            ),
            "Creating a Book with a null author is not throwing a NullPointerException"
        );
    }

    @Test
    @DisplayName("Create Book - Null image")
    void testCreateBookWithNullImage() {
        Assertions.assertThrows(
            NullPointerException.class,
            () -> new Book(
                ValuesGenerator.validBookGenre(),
                ValuesGenerator.validBookAuthor(),
                null,
                ValuesGenerator.validBookTitle(),
                ValuesGenerator.validBookSubtitle(),
                ValuesGenerator.validBookPublisher(),
                ValuesGenerator.validBookYear(),
                ValuesGenerator.validBookPages(),
                ValuesGenerator.validBookIsbn()
            ),
            "Creating a Book with a null image is not throwing a NullPointerException"
        );
    }

    @Test
    @DisplayName("Create Book - Null title")
    void testCreateBookWithNullTitle() {
        Assertions.assertThrows(
            NullPointerException.class,
            () -> new Book(
                ValuesGenerator.validBookGenre(),
                ValuesGenerator.validBookAuthor(),
                ValuesGenerator.validBookImage(),
                null,
                ValuesGenerator.validBookSubtitle(),
                ValuesGenerator.validBookPublisher(),
                ValuesGenerator.validBookYear(),
                ValuesGenerator.validBookPages(),
                ValuesGenerator.validBookIsbn()
            ),
            "Creating a Book with a null title is not throwing a NullPointerException"
        );
    }

    @Test
    @DisplayName("Create Book - Null subtitle")
    void testCreateBookWithNullSubtitle() {
        Assertions.assertThrows(
            NullPointerException.class,
            () -> new Book(
                ValuesGenerator.validBookGenre(),
                ValuesGenerator.validBookAuthor(),
                ValuesGenerator.validBookImage(),
                ValuesGenerator.validBookTitle(),
                null,
                ValuesGenerator.validBookPublisher(),
                ValuesGenerator.validBookYear(),
                ValuesGenerator.validBookPages(),
                ValuesGenerator.validBookIsbn()
            ),
            "Creating a Book with a null subtitle is not throwing a NullPointerException"
        );
    }

    @Test
    @DisplayName("Create Book - Null publisher")
    void testCreateBookWithNullPublisher() {
        Assertions.assertThrows(
            NullPointerException.class,
            () -> new Book(
                ValuesGenerator.validBookGenre(),
                ValuesGenerator.validBookAuthor(),
                ValuesGenerator.validBookImage(),
                ValuesGenerator.validBookTitle(),
                ValuesGenerator.validBookSubtitle(),
                null,
                ValuesGenerator.validBookYear(),
                ValuesGenerator.validBookPages(),
                ValuesGenerator.validBookIsbn()
            ),
            "Creating a Book with a null publisher is not throwing a NullPointerException"
        );
    }

    @Test
    @DisplayName("Create Book - Null year")
    void testCreateBookWithNullYear() {
        Assertions.assertThrows(
            NullPointerException.class,
            () -> new Book(
                ValuesGenerator.validBookGenre(),
                ValuesGenerator.validBookAuthor(),
                ValuesGenerator.validBookImage(),
                ValuesGenerator.validBookTitle(),
                ValuesGenerator.validBookSubtitle(),
                ValuesGenerator.validBookPublisher(),
                null,
                ValuesGenerator.validBookPages(),
                ValuesGenerator.validBookIsbn()
            ),
            "Creating a Book with a null year is not throwing a NullPointerException"
        );
    }


    @Test
    @DisplayName("Create Book - Non positive pages")
    void testCreateBookWithANonPositivePagesValue() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new Book(
                ValuesGenerator.validBookGenre(),
                ValuesGenerator.validBookAuthor(),
                ValuesGenerator.validBookImage(),
                ValuesGenerator.validBookTitle(),
                ValuesGenerator.validBookSubtitle(),
                ValuesGenerator.validBookPublisher(),
                ValuesGenerator.validBookYear(),
                Faker.instance().number().numberBetween(Integer.MIN_VALUE, 1),
                ValuesGenerator.validBookIsbn()
            ),
            "Creating a Book with a non positive pages value"
                + " is not throwing an IllegalArgumentException"
        );
    }

    @Test
    @DisplayName("Create Book - Null isbn")
    void testCreateBookWithNullIsbn() {
        Assertions.assertThrows(
            NullPointerException.class,
            () -> new Book(
                ValuesGenerator.validBookGenre(),
                ValuesGenerator.validBookAuthor(),
                ValuesGenerator.validBookImage(),
                ValuesGenerator.validBookTitle(),
                ValuesGenerator.validBookSubtitle(),
                ValuesGenerator.validBookPublisher(),
                ValuesGenerator.validBookYear(),
                ValuesGenerator.validBookPages(),
                null
            ),
            "Creating a Book with a null isbn is not throwing a NullPointerException"
        );
    }

}
