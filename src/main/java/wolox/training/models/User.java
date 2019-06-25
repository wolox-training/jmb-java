package wolox.training.models;

import com.google.common.base.Preconditions;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import wolox.training.exceptions.BookAlreadyOwnedException;

/**
 * Represents a user.
 */
@ToString(
    doNotUseGetters = true
)
@EqualsAndHashCode(
    of = "id"
)
public class User {

    /**
     * The {@link PasswordEncoder} used to hash and match passwords.
     */
    private static final PasswordEncoder PASSWORD_HASHER = new BCryptPasswordEncoder();


    /**
     * The min. a password must have.
     */
    public static final int PASSWORD_MIN_LENGTH = 8;
    /**
     * A regex to check whether the password contains at least a lowercase letter.
     */
    public static final String CONTAIN_LOWERCASE_REGEX = "^.*[a-z].*$";
    /**
     * A regex to check whether the password contains at least an uppercase letter.
     */
    public static final String CONTAIN_UPPERCASE_REGEX = "^.*[A-Z].*$";
    /**
     * A regex to check whether the password contains at least an number.
     */
    public static final String CONTAIN_NUMBER_REGEX = "^.*\\d.*$";
    /**
     * A regex to check whether the password contains at least a special character.
     */
    public static final String CONTAIN_SPECIAL_CHARACTER_REGEX = "^.*[^a-zA-Z0-9].*$";


    /**
     * The user's id.
     */
    @Getter
    private final long id;
    /**
     * The username.
     */
    @Getter
    private final String username;
    /**
     * The user's password (in hashed format).
     */
    private String hashedPassword;
    /**
     * The user's name.
     */
    @Getter
    private final String name;
    /**
     * The user's birthDate.
     */
    @Getter
    private final LocalDate birthDate;
    /**
     * The {@link Book}s owned by this user.
     */
    private final Set<Book> books;
    /**
     * The roles this user were granted.
     */
    private final Set<String> roles;


    /**
     * Default constructor for JPA Provider.
     */
    /* package */ User() {
        // Default constructor that sets final fields with default values
        // Real values will be set by JPA Provider
        this.id = 0;
        this.username = null;
        this.name = null;
        this.birthDate = null;
        this.books = new HashSet<>();
        this.roles = new HashSet<>();
    }

    /**
     * Constructor.
     *
     * @param username The username.
     * @param name The user's name.
     * @param birthDate The user's birthDate.
     * @implNote This method applies a hashing function to the given password.
     */
    public User(final String username, final String name, final LocalDate birthDate) {
        assertUsername(username);
        assertName(name);
        assertBirthDate(birthDate);

        this.id = 0; // Will be set when saving by JPA provider
        this.username = username;
        this.hashedPassword = null;
        this.name = name;
        this.birthDate = birthDate;
        this.books = new HashSet<>();
        this.roles = Set.of("USER");
    }


    /**
     * Gets an unmodifiable {@link Set} version of the {@link Book}s owned by this user.
     *
     * @return The {@link Book}s owned by this user.
     */
    public Set<Book> getBooks() {
        return Collections.unmodifiableSet(books);
    }

    /**
     * Gets an unmodifiable {@link Set} version of the roles granted to this user.
     *
     * @return The roles this user has.
     */
    public Set<String> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    /**
     * Adds a {@link Book} to this user.
     *
     * @param book The {@link Book} to be added.
     * @throws BookAlreadyOwnedException If the given {@code book} is already owned by this user.
     */
    public void addBook(final Book book) {
        assertBook(book);
        if (books.contains(book)) {
            throw new BookAlreadyOwnedException();
        }
        books.add(book);
    }

    /**
     * Removes a {@link Book} from this user.
     *
     * @param book The {@link Book} to be removed.
     */
    public void removeBook(final Book book) {
        assertBook(book);
        books.remove(book);
    }

    /**
     * Changes the password for this user.
     *
     * @param newPassword The new password for the user (in plain format).
     * @implNote This method applies a hashing function to the given password.
     */
    public void changePassword(final String newPassword) {
        assertPassword(newPassword);
        this.hashedPassword = PASSWORD_HASHER.encode(newPassword);
    }

    /**
     * Changes the password for this user. Verifies whether the given {@code password} matches the
     * {@link User}'s password.
     *
     * @param password The password to be validated.
     * @implNote This method applies a hashing function to the given password, and the checks using
     * that.
     */
    public boolean passwordMatches(final String password) {
        Preconditions.checkState(
            this.hashedPassword != null,
            "A password must be set in order to be matched"
        );
        return PASSWORD_HASHER.matches(password, this.hashedPassword);
    }


    /**
     * Asserts the given {@code username}.
     *
     * @param username The username value to be asserted.
     * @throws IllegalArgumentException If the given {@code username} is {@code null}.
     */
    private static void assertUsername(final String username) {
        Preconditions.checkNotNull(username, "The username must not be null");
    }

    /**
     * Asserts the given {@code plainPassword}.
     *
     * @param plainPassword The plainPassword value to be asserted.
     * @throws NullPointerException If the given {@code plainPassword} is {@code null}
     * @throws IllegalArgumentException If the given {@code plainPassword} is not does not comply
     * with the platform's security requirements.
     */
    private static void assertPassword(final String plainPassword) {
        Preconditions.checkNotNull(plainPassword, "The password must not be null");
        Preconditions.checkArgument(
            plainPassword.length() >= PASSWORD_MIN_LENGTH,
            "The password must contain at least 8 characters"
        );
        Preconditions.checkArgument(
            plainPassword.matches(CONTAIN_LOWERCASE_REGEX),
            "The password must contain lower case letters"
        );
        Preconditions.checkArgument(
            plainPassword.matches(CONTAIN_UPPERCASE_REGEX),
            "The password must contain upper case letters"
        );
        Preconditions.checkArgument(
            plainPassword.matches(CONTAIN_NUMBER_REGEX),
            "The password must contain numbers"
        );
        Preconditions.checkArgument(
            plainPassword.matches(CONTAIN_SPECIAL_CHARACTER_REGEX),
            "The password must special characters"
        );
    }

    /**
     * Asserts the given {@code name}.
     *
     * @param name The name value to be asserted.
     * @throws NullPointerException If the given {@code name} is {@code null}.
     */
    private static void assertName(final String name) {
        Preconditions.checkNotNull(name, "The name must not be null");
    }

    /**
     * Asserts the given {@code birthDate}.
     *
     * @param birthDate The birthDate value to be asserted.
     * @throws NullPointerException If the given {@code birthDate} is {@code null}
     * @throws IllegalArgumentException If the given {@code birthDate} is not in the past (i.e is
     * not before {@link LocalDate#now()}).
     */
    private static void assertBirthDate(final LocalDate birthDate) {
        Preconditions.checkNotNull(birthDate, "The birth date must not be null");
        Preconditions.checkArgument(
            birthDate.isBefore(LocalDate.now()),
            "The birth date must be in the past"
        );
    }

    /**
     * Asserts the given {@code book}.
     *
     * @param book The {@link Book} value to be asserted.
     * @throws NullPointerException If the given {@code book} is {@code null}.
     */
    private static void assertBook(final Book book) {
        Preconditions.checkNotNull(book, "The book must not be null");
    }
}
