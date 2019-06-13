package wolox.training.utils;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
        return new Book(
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
    }

    /**
     * Mocks a {@link List} of {@link Book}s of random size.
     *
     * @param maxSize The max size the {@link List} will have
     * @return A mocked {@link List} of {@link Book}s.
     */
    public static List<Book> mockBookList(final int maxSize) {
        return mocksList(maxSize, ignored -> mockBook());
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
        return new User(
            Faker.instance().name().username(),
            Faker.instance().name().fullName(),
            LocalDate.ofInstant(
                Faker.instance().date().birthday().toInstant(),
                ZoneId.systemDefault()
            )
        );
    }

    /**
     * Mocks a {@link List} of {@link User}s of random size.
     *
     * @param maxSize The max size the {@link List} will have
     * @return A mocked {@link List} of {@link User}s.
     */
    public static List<User> mockUserList(final int maxSize) {
        return mocksList(maxSize, ignored -> mockUser());
    }

    /**
     * Abstract method to create a {@link List} of mocks of a random size.
     *
     * @param maxSize The max size the {@link List} will have.
     * @param mockGenerator An {@link IntFunction} that receives a position and, based on it,
     * creates a mock of type {@code T}.
     * @param <T> The concrete type of the mock.
     * @return The created {@link List} of mocks.
     */
    private static <T> List<T> mocksList(final int maxSize, IntFunction<T> mockGenerator) {
        Assert.isTrue(maxSize > 1, "The max size must be greater than 1");
        final var size = Faker.instance().number().numberBetween(1, maxSize);
        return IntStream.range(0, size)
            .mapToObj(mockGenerator)
            .collect(Collectors.toList());
    }
}
