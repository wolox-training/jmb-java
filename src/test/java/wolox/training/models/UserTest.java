package wolox.training.models;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wolox.training.exceptions.BookAlreadyOwnedException;
import wolox.training.models.ModelsTestHelper.UserField;
import wolox.training.utils.TestHelper;

/**
 * Testing for the {@link User} class.
 */
class UserTest {


    @Test
    @DisplayName("Create User - Valid arguments")
    void testCreateUserWithValidArguments() {
        final var userMap = ModelsTestHelper.buildUserMap();
        Assertions.assertDoesNotThrow(
            () -> ModelsTestHelper.buildUserFromMap(userMap),
            "Creating a User with valid values is throwing an unexpected Exception"
        );
    }

    @Test
    @DisplayName("Create User - Null username")
    void testCreateUserWithNullUsername() {
        final var userMap = ModelsTestHelper.buildUserMap();
        userMap.put(UserField.USERNAME, null);
        Assertions.assertThrows(
            NullPointerException.class,
            () -> ModelsTestHelper.buildUserFromMap(userMap),
            "Creating a User with a null username is not throwing a NullPointerException"
        );
    }

    @Test
    @DisplayName("Create User - Null name")
    void testCreateUserWithNullName() {
        final var userMap = ModelsTestHelper.buildUserMap();
        userMap.put(UserField.NAME, null);
        Assertions.assertThrows(
            NullPointerException.class,
            () -> ModelsTestHelper.buildUserFromMap(userMap),
            "Creating a User with a null name is not throwing a NullPointerException"
        );
    }

    @Test
    @DisplayName("Create User - Null birth date")
    void testCreateUserWithNullBirthDate() {
        final var userMap = ModelsTestHelper.buildUserMap();
        userMap.put(UserField.BIRTH_DATE, null);
        Assertions.assertThrows(
            NullPointerException.class,
            () -> ModelsTestHelper.buildUserFromMap(userMap),
            "Creating a User with a null birth date is not throwing a NullPointerException"
        );
    }

    @Test
    @DisplayName("Create User - Today birth date")
    void testCreateUserWithTodayAsBirthDate() {
        final var userMap = ModelsTestHelper.buildUserMap();
        userMap.put(UserField.BIRTH_DATE, LocalDate.now());
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> ModelsTestHelper.buildUserFromMap(userMap),
            "Creating a User with a birth date that is today"
                + " is not throwing an IllegalArgumentException"
        );
    }

    @Test
    @DisplayName("Create User - Future birth date")
    void testCreateUserWithFutureAsBirthDate() {
        final var userMap = ModelsTestHelper.buildUserMap();
        userMap.put(UserField.BIRTH_DATE, LocalDate.now().plusDays(1));
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> ModelsTestHelper.buildUserFromMap(userMap),
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
