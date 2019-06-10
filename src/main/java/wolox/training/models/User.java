package wolox.training.models;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;
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
    }

    /**
     * Constructor.
     *
     * @param username The username.
     * @param name The user's name.
     * @param birthDate The user's birthDate.
     */
    public User(final String username, final String name, final LocalDate birthDate) {
        assertUsername(username);
        assertName(name);
        assertBirthData(birthDate);
        this.id = 0; // Will be set when saving by JPA provider
        this.username = username;
        this.name = name;
        this.birthDate = birthDate;
        this.books = new HashSet<>();
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
     * Asserts the given {@code username}.
     *
     * @param username The username value to be asserted.
     * @throws IllegalArgumentException If the given {@code username} value is not valid.
     */
    private static void assertUsername(final String username) {
        Assert.notNull(username, "The username must not be null");
    }

    /**
     * Asserts the given {@code name}.
     *
     * @param name The name value to be asserted.
     * @throws IllegalArgumentException If the given {@code name} value is not valid.
     */
    private static void assertName(final String name) {
        Assert.notNull(name, "The name must not be null");
    }

    /**
     * Asserts the given {@code birthDate}.
     *
     * @param birthDate The birthDate value to be asserted.
     * @throws IllegalArgumentException If the given {@code birthDate} value is not valid.
     */
    private static void assertBirthData(final LocalDate birthDate) {
        Assert.notNull(birthDate, "The birth date must not be null");
        Assert.isTrue(birthDate.isBefore(LocalDate.now()), "The birth date must be in the past");
    }

    /**
     * Asserts the given {@code book}.
     *
     * @param book The {@link Book} value to be asserted.
     * @throws IllegalArgumentException If the given {@code book} value is not valid.
     */
    private static void assertBook(final Book book) {
        Assert.notNull(book, "The book must not be null");
    }
}
