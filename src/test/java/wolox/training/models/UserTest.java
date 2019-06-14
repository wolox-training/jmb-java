package wolox.training.models;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wolox.training.exceptions.BookAlreadyOwnedException;
import wolox.training.utils.TestHelper;
import wolox.training.utils.ValuesGenerator;

/**
 * Testing for the {@link User} class.
 */
class UserTest {


    @Test
    @DisplayName("Create User - Valid arguments")
    void testCreateUserWithValidArguments() {
        Assertions.assertDoesNotThrow(
            () -> new User(
                ValuesGenerator.validUserUsername(),
                ValuesGenerator.validUserName(),
                ValuesGenerator.validUserBirthDate()
            ),
            "Creating a User with valid values is throwing an unexpected Exception"
        );
    }

    @Test
    @DisplayName("Create User - Null username")
    void testCreateUserWithNullUsername() {
        Assertions.assertThrows(
            NullPointerException.class,
            () -> new User(
                null,
                ValuesGenerator.validUserName(),
                ValuesGenerator.validUserBirthDate()
            ),
            "Creating a User with a null username is not throwing a NullPointerException"
        );
    }

    @Test
    @DisplayName("Create User - Null name")
    void testCreateUserWithNullName() {
        Assertions.assertThrows(
            NullPointerException.class,
            () -> new User(
                ValuesGenerator.validUserUsername(),
                null,
                ValuesGenerator.validUserBirthDate()
            ),
            "Creating a User with a null name is not throwing a NullPointerException"
        );
    }

    @Test
    @DisplayName("Create User - Null title")
    void testCreateUserWithNullBirthDate() {
        Assertions.assertThrows(
            NullPointerException.class,
            () -> new User(
                ValuesGenerator.validUserUsername(),
                ValuesGenerator.validUserName(),
                null
            ),
            "Creating a User with a null birth date is not throwing a NullPointerException"
        );
    }

    @Test
    @DisplayName("Create User - Today birth date")
    void testCreateUserWithTodayAsBirthDate() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new User(
                ValuesGenerator.validUserUsername(),
                ValuesGenerator.validUserName(),
                LocalDate.now()
            ),
            "Creating a User with a birth date that is today"
                + " is not throwing an IllegalArgumentException"
        );
    }

    @Test
    @DisplayName("Create User - Future birth date")
    void testCreateUserWithFutureAsBirthDate() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new User(
                ValuesGenerator.validUserUsername(),
                ValuesGenerator.validUserName(),
                LocalDate.now().plusDays(1)
            ),
            "Creating a User with a birth date that is in the future"
                + " is not throwing an IllegalArgumentException"
        );
    }


    @Test
    @DisplayName("Add Book - Book not owned")
    void testAddNotOwnedBookToUser() {
        final var maxListSize = 10;
        final var user = TestHelper.mockUserWithBooks(maxListSize);
        final var book = TestHelper.mockBook();
        Assertions.assertDoesNotThrow(
            () -> user.addBook(book),
            "Adding a book to a User that does not own it"
                + " is throwing an unexpected exception"
        );
    }

    @Test
    @DisplayName("Add Book - Book owned")
    void testAddOwnedBookToUser() {
        final var maxListSize = 10;
        final var user = TestHelper.mockUserWithBooks(maxListSize);
        final var book = user.getBooks().iterator().next();  // Will always have at least one
        Assertions.assertThrows(
            BookAlreadyOwnedException.class,
            () -> user.addBook(book),
            "Adding a book to a User that already owns it"
                + " is not throwing a BookAlreadyOwnedException"
        );
    }


    @Test
    @DisplayName("Remove Book - Book not owned")
    void testRemoveNotOwnedBookToUser() {
        final var maxListSize = 10;
        final var user = TestHelper.mockUserWithBooks(maxListSize);
        final var book = TestHelper.mockBook();
        Assertions.assertDoesNotThrow(
            () -> user.removeBook(book),
            "Removing a book to a User that does not own it"
                + " is throwing an unexpected exception"
        );
    }

    @Test
    @DisplayName("Remove Book - Book owned")
    void testAddExistingBookToUser() {
        final var maxListSize = 10;
        final var user = TestHelper.mockUserWithBooks(maxListSize);
        final var book = user.getBooks().iterator().next();  // Will always have at least one
        Assertions.assertDoesNotThrow(
            () -> user.removeBook(book),
            "Removing a book to a User that already owns it"
                + " is throwing an unexpected exception"
        );
    }
}
