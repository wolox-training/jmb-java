package wolox.training.utils;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashSet;
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
     * Mocks a {@link Book}'s id using {@link Faker} utilities.
     *
     * @return A mocked {@link Book} id.
     */
    public static long mockBookId() {
        return Faker.instance().number().numberBetween(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /**
     * Mocks a {@link Book} using {@link Faker} utilities.
     *
     * @return A mocked {@link Book}.
     */
    public static Book mockBook() {
        final var book = new Book(
            Faker.instance().book().genre(),
            Faker.instance().book().author(),
            Faker.instance().internet().image(),
            Faker.instance().book().title(),
            Faker.instance().book().title(), // Use this as subtitle
            Faker.instance().book().publisher(),
            Long.toString(Faker.instance().number().numberBetween(1950, LocalDate.now().getYear())),
            Faker.instance().number().numberBetween(1, 2000), // Book has at most 2000 pages
            Faker.instance().code().isbn13()
        );
        ReflectionTestUtils.setField(book, "id", mockBookId());
        return book;
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
        return mockCollection(maxSize, TestHelper::mockBook, HashSet::new);
    }


    /**
     * Mocks a {@link Book}'s id using {@link Faker} utilities.
     *
     * @return A mocked {@link Book} id.
     */
    public static long mockUserId() {
        return Faker.instance().number().numberBetween(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    /**
     * Mocks a {@link User} using {@link Faker} utilities.
     *
     * @return A mocked {@link User}.
     */
    public static User mockUser() {
        final var user = new User(
            Faker.instance().name().username(),
            Faker.instance().name().fullName(),
            LocalDate.ofInstant(
                Faker.instance().date().birthday().toInstant(),
                ZoneId.systemDefault()
            )
        );
        ReflectionTestUtils.setField(user, "id", mockUserId());
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
     * @param <T> The concrete type of the mock.
     * @param <I> The concrete type of the {@link Collection}.
     * @return The created {@link Collection} of mocks.
     */
    private static <T, I extends Collection<T>> I mockCollection(
        final int maxSize,
        final Supplier<T> mockGenerator,
        final Supplier<I> iterableGenerator) {
        Assert.isTrue(maxSize > 1, "The max size must be greater than 1");
        final var size = Faker.instance().number().numberBetween(8, maxSize);
        return Stream.generate(mockGenerator)
            .limit(size)
            .collect(Collectors.toCollection(iterableGenerator));
    }
}
