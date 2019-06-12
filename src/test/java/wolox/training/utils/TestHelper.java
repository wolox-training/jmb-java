package wolox.training.utils;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.util.Assert;
import wolox.training.models.Book;

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
     * @return A mocked {@link List} od {@link Book}s.
     */
    public static List<Book> mockBookList(final int maxSize) {
        Assert.isTrue(maxSize > 1, "The max size must be greater than 1");
        final var size = Faker.instance().number().numberBetween(1, maxSize);
        return IntStream.range(0, size)
            .mapToObj(ignored -> mockBook())
            .collect(Collectors.toList());
    }
}
