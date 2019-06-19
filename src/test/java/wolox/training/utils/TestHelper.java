package wolox.training.utils;

import com.github.javafaker.Faker;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Assert;
import wolox.training.models.Book;
import wolox.training.models.User;

/**
 * Helper class for tests.
 */
public class TestHelper {

    /**
     * Private constructor to avoid instantiation.
     */
    private TestHelper() {
    }


    /**
     * Mocks a {@link Book} using {@link Faker} utilities.
     *
     * @return A mocked {@link Book}.
     */
    public static Book mockBook() {
        return new Book(
            ValuesGenerator.validBookGenre(),
            ValuesGenerator.validBookAuthor(),
            ValuesGenerator.validBookImage(),
            ValuesGenerator.validBookTitle(),
            ValuesGenerator.validBookSubtitle(),
            ValuesGenerator.validBookPublisher(),
            ValuesGenerator.validBookYear(),
            ValuesGenerator.validBookPages(),
            ValuesGenerator.validBookIsbn()
        );
    }


    /**
     * Mocks a {@link List} of {@link Book}s of random size.
     *
     * @param maxSize The max size the {@link List} will have
     * @return A mocked {@link List} of {@link Book}s.
     */
    public static List<Book> mockBookList(final int maxSize) {
        return mockCollection(maxSize, TestHelper::mockBook, LinkedList::new);
    }

    /**
     * Mocks a {@link Set} of {@link Book}s of random size.
     *
     * @param maxSize The max size the {@link Set} will have
     * @return A mocked {@link Set} of {@link Book}s.
     */
    public static Set<Book> mockBookSet(final int maxSize) {
        return mockBookList(maxSize)
            .stream()
            .peek(TestHelper::addId)
            .collect(Collectors.toSet())
            ;
    }

    /**
     * Mocks a {@link User} using {@link Faker} utilities.
     *
     * @return A mocked {@link User}.
     */
    public static User mockUser() {
        return new User(
            ValuesGenerator.validUserUsername(),
            ValuesGenerator.validUserName(),
            ValuesGenerator.validUserBirthDate()
        );
    }


    /**
     * Mocks a {@link User} with {@link Book}s.
     *
     * @param maxAmountOfBooks The max. amount of {@link Book}s the {@link User} can have.
     * @return The mocked {@link User}.
     */
    public static User mockUserWithBooks(final int maxAmountOfBooks) {
        final var user = mockUser();
        mockBookSet(maxAmountOfBooks).forEach(user::addBook);
        return user;
    }

    /**
     * Mocks a {@link List} of {@link User}s of random size.
     *
     * @param maxSize The max size the {@link List} will have
     * @return A mocked {@link List} of {@link User}s.
     */
    public static List<User> mockUserList(final int maxSize) {
        return mockCollection(maxSize, TestHelper::mockUser, LinkedList::new);
    }

    /**
     * Abstract method that creates a {@link Collection} of subtype {@code I} of mocks of type
     * {@code T}, of a random size.
     *
     * @param maxSize The max size the {@link List} will have.
     * @param mockGenerator A {@link Supplier} that creates a mock of type {@code T}.
     * @param collectionGenerator A {@link Supplier} that creates the {@link Collection} to be
     * returned after being filled.
     * @param <T> The concrete type of the mock.
     * @param <I> The concrete type of the {@link Collection}.
     * @return The created {@link Collection} of mocks.
     */
    private static <T, I extends Collection<T>> I mockCollection(
        final int maxSize,
        final Supplier<T> mockGenerator,
        final Supplier<I> collectionGenerator) {
        Assert.isTrue(maxSize > 2, "The max size must be greater than 2");
        Assert.notNull(mockGenerator, "The mock generator must not be null");
        Assert.notNull(collectionGenerator, "The collection generator must not be null");
        final var size = Faker.instance().number().numberBetween(2, maxSize);
        return Stream.generate(mockGenerator)
            .limit(size)
            .collect(Collectors.toCollection(collectionGenerator));
    }

    /**
     * Adds an id to the given {@code book}.
     *
     * @param book The {@link Book} to which an id will be added.
     * @implNote This method uses reflection to access the {@link Book}'s id field.
     */
    public static void addId(final Book book) {
        addId(book, "id", ValuesGenerator::validBookId);
    }

    /**
     * Adds an id to the given {@code user}.
     *
     * @param user The {@link User} to which an id will be added.
     * @implNote This method uses reflection to access the {@link User}'s id field.
     */
    public static void addId(final User user) {
        addId(user, "id", ValuesGenerator::validUserId);
    }

    private static <T, ID> void addId(
        final T entity,
        final String idFieldName,
        final Supplier<ID> idSupplier) {
        ReflectionTestUtils.setField(entity, idFieldName, idSupplier.get());
    }
}
