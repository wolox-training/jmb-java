package wolox.training.utils;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.time.ZoneId;
import wolox.training.models.Book;
import wolox.training.models.User;

/**
 * Helper class that contains methods to generate mock values to be used by tests.
 */
public class ValuesGenerator {

    /**
     * Private constructor to avoid instantiation.
     */
    private ValuesGenerator() {
    }


    /**
     * Mocks a {@link Book}'s id using {@link Faker} utilities.
     *
     * @return A mocked {@link Book} id.
     */
    public static long validBookId() {
        return Faker.instance().number().numberBetween(1, Long.MAX_VALUE);
    }

    /**
     * Mocks a {@link Book}'s genre using {@link Faker} utilities.
     *
     * @return A mocked {@link Book} genre.
     */
    public static String validBookGenre() {
        return Faker.instance().book().genre();
    }

    /**
     * Mocks a {@link Book}'s author using {@link Faker} utilities.
     *
     * @return A mocked {@link Book} author.
     */
    public static String validBookAuthor() {
        return Faker.instance().book().author();
    }

    /**
     * Mocks a {@link Book}'s image using {@link Faker} utilities.
     *
     * @return A mocked {@link Book} image.
     */
    public static String validBookImage() {
        return Faker.instance().internet().image();
    }

    /**
     * Mocks a {@link Book}'s title using {@link Faker} utilities.
     *
     * @return A mocked {@link Book} title.
     */
    public static String validBookTitle() {
        return Faker.instance().book().title();
    }

    /**
     * Mocks a {@link Book}'s subtitle using {@link Faker} utilities.
     *
     * @return A mocked {@link Book} subtitle.
     */
    public static String validBookSubtitle() {
        return Faker.instance().book().title(); // Use this as subtitle
    }

    /**
     * Mocks a {@link Book}'s publisher using {@link Faker} utilities.
     *
     * @return A mocked {@link Book} publisher.
     */
    public static String validBookPublisher() {
        return Faker.instance().book().publisher();
    }

    /**
     * Mocks a {@link Book}'s year using {@link Faker} utilities.
     *
     * @return A mocked {@link Book} year.
     */
    public static String validBookYear() {
        return Long
            .toString(Faker.instance().number().numberBetween(1950, LocalDate.now().getYear()));
    }

    /**
     * Mocks a {@link Book}'s pages using {@link Faker} utilities.
     *
     * @return A mocked {@link Book} pages.
     */
    public static int validBookPages() {
        return Faker.instance().number().numberBetween(1, 2000); // Book has at most 2000 pages
    }

    /**
     * Mocks a {@link Book}'s isbn using {@link Faker} utilities.
     *
     * @return A mocked {@link Book} isbn.
     */
    public static String validBookIsbn() {
        return Faker.instance().code().isbn13();
    }


    /**
     * Mocks a {@link User}'s id using {@link Faker} utilities.
     *
     * @return A mocked {@link Book} id.
     */
    public static long validUserId() {
        return Faker.instance().number().numberBetween(1, Long.MAX_VALUE);
    }

    /**
     * Mocks a {@link User}'s username using {@link Faker} utilities.
     *
     * @return A mocked {@link User} username.
     */
    public static String validUserUsername() {
        return Faker.instance().name().username();
    }

    /**
     * Mocks a {@link User}'s username using {@link Faker} utilities.
     *
     * @return A mocked {@link User} username.
     */
    public static String validUserName() {
        return Faker.instance().name().fullName();
    }

    /**
     * Mocks a {@link User}'s username using {@link Faker} utilities.
     *
     * @return A mocked {@link User} username.
     */
    public static LocalDate validUserBirthDate() {
        return LocalDate.ofInstant(
            Faker.instance().date().birthday().toInstant(),
            ZoneId.systemDefault()
        );
    }
}
