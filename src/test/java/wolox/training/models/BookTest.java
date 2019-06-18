package wolox.training.models;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wolox.training.models.ModelsTestHelper.BookField;

/**
 * Testing for the {@link Book} class.
 */
class BookTest {


    @Test
    @DisplayName("Create Book - Valid arguments")
    void testCreateBookWithValidArguments() {
        final var bookMap = ModelsTestHelper.buildBookMap();
        Assertions.assertDoesNotThrow(
            () -> ModelsTestHelper.buildFromMap(bookMap),
            "Creating a Book with valid values is throwing an unexpected Exception"
        );
    }

    @Test
    @DisplayName("Create Book - Null author")
    void testCreateBookWithNullAuthor() {
        final var bookMap = ModelsTestHelper.buildBookMap();
        bookMap.put(BookField.AUTHOR, null);
        Assertions.assertThrows(
            NullPointerException.class,
            () -> ModelsTestHelper.buildFromMap(bookMap),
            "Creating a Book with a null author is not throwing a NullPointerException"
        );
    }

    @Test
    @DisplayName("Create Book - Null image")
    void testCreateBookWithNullImage() {
        final var bookMap = ModelsTestHelper.buildBookMap();
        bookMap.put(BookField.IMAGE, null);
        Assertions.assertThrows(
            NullPointerException.class,
            () -> ModelsTestHelper.buildFromMap(bookMap),
            "Creating a Book with a null image is not throwing a NullPointerException"
        );
    }

    @Test
    @DisplayName("Create Book - Null title")
    void testCreateBookWithNullTitle() {
        final var bookMap = ModelsTestHelper.buildBookMap();
        bookMap.put(BookField.TITLE, null);
        Assertions.assertThrows(
            NullPointerException.class,
            () -> ModelsTestHelper.buildFromMap(bookMap),
            "Creating a Book with a null title is not throwing a NullPointerException"
        );
    }

    @Test
    @DisplayName("Create Book - Null subtitle")
    void testCreateBookWithNullSubtitle() {
        final var bookMap = ModelsTestHelper.buildBookMap();
        bookMap.put(BookField.SUBTITLE, null);
        Assertions.assertThrows(
            NullPointerException.class,
            () -> ModelsTestHelper.buildFromMap(bookMap),
            "Creating a Book with a null subtitle is not throwing a NullPointerException"
        );
    }

    @Test
    @DisplayName("Create Book - Null publisher")
    void testCreateBookWithNullPublisher() {
        final var bookMap = ModelsTestHelper.buildBookMap();
        bookMap.put(BookField.PUBLISHER, null);
        Assertions.assertThrows(
            NullPointerException.class,
            () -> ModelsTestHelper.buildFromMap(bookMap),
            "Creating a Book with a null publisher is not throwing a NullPointerException"
        );
    }

    @Test
    @DisplayName("Create Book - Null year")
    void testCreateBookWithNullYear() {
        final var bookMap = ModelsTestHelper.buildBookMap();
        bookMap.put(BookField.YEAR, null);
        Assertions.assertThrows(
            NullPointerException.class,
            () -> ModelsTestHelper.buildFromMap(bookMap),
            "Creating a Book with a null year is not throwing a NullPointerException"
        );
    }


    @Test
    @DisplayName("Create Book - Non positive pages")
    void testCreateBookWithANonPositivePagesValue() {
        final var bookMap = ModelsTestHelper.buildBookMap();
        bookMap.put(
            BookField.PAGES,
            Faker.instance().number().numberBetween(Integer.MIN_VALUE, 1)
        );

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> ModelsTestHelper.buildFromMap(bookMap),
            "Creating a Book with a non positive pages value"
                + " is not throwing an IllegalArgumentException"
        );
    }

    @Test
    @DisplayName("Create Book - Null isbn")
    void testCreateBookWithNullIsbn() {
        final var bookMap = ModelsTestHelper.buildBookMap();
        bookMap.put(BookField.ISBN, null);
        Assertions.assertThrows(
            NullPointerException.class,
            () -> ModelsTestHelper.buildFromMap(bookMap),
            "Creating a Book with a null isbn is not throwing a NullPointerException"
        );
    }
}
