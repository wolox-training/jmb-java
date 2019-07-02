package wolox.training.models;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import wolox.training.exceptions.BookAlreadyOwnedException;
import wolox.training.models.ModelsTestHelper.UserField;
import wolox.training.utils.TestHelper;
import wolox.training.utils.ValuesGenerator;

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
        TestHelper.addId(book);
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
        TestHelper.addId(book);
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


    @Test
    @DisplayName("Set password - Null password")
    void testSetNullPassword() {
        Assertions.assertThrows(
            NullPointerException.class,
            () -> TestHelper.mockUser().changePassword(null),
            "Changing a User's password passing a null value is being allowed"
        );
    }

    @Test
    @DisplayName("Set password - Short password")
    void testSetShortPassword() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> TestHelper.mockUser().changePassword(
                Faker.instance().internet().password(0, User.PASSWORD_MIN_LENGTH - 1)
            ),
            "Changing a User's password passing a short password is being allowed"
        );
    }

    @Test
    @DisplayName("Set password - No lowercase")
    void testSetPasswordWithoutLowercase() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> TestHelper.mockUser().changePassword(
                ValuesGenerator.validPassword().toUpperCase()
            ),
            "Changing a User's password passing a password without a lowercase letters is being allowed"
        );
    }

    @Test
    @DisplayName("Set password - No uppercase")
    void testSetPasswordWithoutUppercase() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> TestHelper.mockUser().changePassword(
                ValuesGenerator.validPassword().toLowerCase()
            ),
            "Changing a User's password passing a password without an uppercase letters is being allowed"
        );
    }

    @Test
    @DisplayName("Set password - No number")
    void testSetPasswordWithoutNumbers() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> TestHelper.mockUser().changePassword(
                ValuesGenerator.validPassword().replaceAll("[0-9]", "_")
            ),
            "Changing a User's password passing a password without an number is being allowed"
        );
    }

    @Test
    @DisplayName("Set password - No special character")
    void testSetPasswordWithoutSpecialCharacters() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> TestHelper.mockUser().changePassword(
                ValuesGenerator.validPassword().replaceAll("[^a-zA-Z0-9]", "a")
            ),
            "Changing a User's password passing a password without a special character is being allowed"
        );
    }


    @Test
    @DisplayName("Match password - Not set yet")
    void testMatchPasswordWhenNotSet() {
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> TestHelper.mockUser().passwordMatches(ValuesGenerator.validPassword()),
            "Matching a password for a User without a password is being allowed"
        );
    }

    @Test
    @DisplayName("Match password - Different passwords")
    void testMatchDifferentPasswords() {
        final var user = TestHelper.mockUser();
        final var password = ValuesGenerator.validPassword();
        user.changePassword(password);
        Assertions.assertFalse(
            user.passwordMatches(password + "another-stuff"),
            "A User is matching a password when they are different"
        );
    }

    @Test
    @DisplayName("Match password - Same passwords")
    void testMatchSamePasswords() {
        final var user = TestHelper.mockUser();
        final var password = ValuesGenerator.validPassword();
        user.changePassword(password);
        Assertions.assertTrue(
            user.passwordMatches(password),
            "A User is not matching a password when they are the same"
        );
    }
}
