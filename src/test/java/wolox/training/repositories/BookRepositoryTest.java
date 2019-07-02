package wolox.training.repositories;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import wolox.training.models.Book;
import wolox.training.utils.BookAssertions;
import wolox.training.utils.TestHelper;
import wolox.training.utils.ValuesGenerator;

/**
 * Tests for the {@link BookRepository}.
 */
@DataJpaTest(
    properties = {
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.format_sql=true"
    }
)
class BookRepositoryTest {

    /**
     * The {@link BookRepository} to be tested.
     */
    private final BookRepository bookRepository;

    /**
     * The {@link EntityManager} used to prepare the database before performing the action to be
     * tested, or to query the database state to perform assertions once the action being tested has
     * been performed.
     */
    private final EntityManager entityManager;

    /**
     * Constructor.
     *
     * @param bookRepository The {@link BookRepository} to be tested.
     * @param entityManager The {@link EntityManager} used to prepare the database before performing
     * the action to be tested, or to query the database state to perform assertions once the action
     * being tested has been performed.
     */
    @Autowired
    BookRepositoryTest(
        final BookRepository bookRepository,
        final EntityManager entityManager) {

        this.bookRepository = bookRepository;
        this.entityManager = entityManager;
    }


    /**
     * Tests that saving a {@link Book} with the {@link BookRepository#save(Object)} performs as
     * expected: Sets and id to the {@link Book} and stores it in the database with the
     * corresponding values.
     */
    @Test
    @DisplayName("Save Book")
    void testSavingABook() {
        RepositoriesTestHelper.testSave(
            bookRepository,
            entityManager,
            TestHelper::mockBook,
            Book.class,
            Book::getId,
            0L,
            Long::compare,
            BookAssertions::assertSame
        );
    }

    /**
     * Tests that retrieving an existing {@link Book} with the {@link BookRepository#findById(Object)}
     * performs as expected: Returns a non empty {@link java.util.Optional} containing the existing
     * {@link Book} (i.e with the same values).
     */
    @Test
    @DisplayName("Retrieve Book by id")
    void testRetrieveById() {
        retrieveTesting(BookRepository::findById, Book::getId);
    }

    /**
     * Tests that retrieving an existing {@link Book} with the {@link BookRepository#getFirstByAuthor(String)}
     * performs as expected: Returns a non empty {@link java.util.Optional} containing the existing
     * {@link Book} (i.e with the same values).
     */
    @Test
    @DisplayName("Retrieve Book by author")
    void testRetrieveByAuthor() {
        retrieveTesting(BookRepository::getFirstByAuthor, Book::getAuthor);
    }

    /**
     * Tests that searching by author does not throw any exception when the database has more than
     * one {@link Book} with the same author.
     */
    @Test
    @DisplayName("Retrieve Book by author - More than one Book with the same author in the database")
    void testSeveralBooksWithSameAuthor() {
        RepositoriesTestHelper.testSeveralInstancesAndJustOneSearch(
            bookRepository,
            BookRepository::getFirstByAuthor,
            Book::getAuthor,
            entityManager,
            TestHelper::mockBook,
            BookRepositoryTest::cloneBook,
            "An unexpected exception was thrown when retrieving a Book by author"
                + " when there is more than one Book with the same author in the database"
        );
    }

    /**
     * Tests that searching by publisher, genre and year returns all the matching {@link Book}s,
     * using the naming template method.
     */
    @Test
    @DisplayName("Search by publisher, genre and year (naming template) - Contained")
    void testSearchByPublisherAndGenreAndYearContainedNamingTemplate() {
        testSearchByPublisherAndGenreAndYear(bookRepository::getByPublisherAndGenreAndYear);
    }

    /**
     * Tests that searching by publisher, genre and year returns all the matching {@link Book}s,
     * using the custom query method.
     */
    @Test
    @DisplayName("Search by publisher, genre and year (custom query) - Contained")
    void testSearchByPublisherAndGenreAndYearContainedCustomQuery() {
        testSearchByPublisherAndGenreAndYear(bookRepository::getWithPublisherGenreAndYear);
    }

    /**
     * Tests that searching by publisher, genre and year using the custom method, sending a {@code
     * null} publisher.
     */
    @Test
    @DisplayName("Search by publisher, genre and year (custom query) - Null publisher")
    void testSearchByPublisherAndGenreAndYearWithNullPublisher() {
        testSearchByPublisherAndGenreAndYear(
            null,
            ValuesGenerator.validBookGenre(),
            ValuesGenerator.validBookYear(),
            bookRepository::getWithPublisherGenreAndYear
        );
    }

    /**
     * Tests that searching by publisher, genre and year using the custom method, sending a {@code
     * null} genre.
     */
    @Test
    @DisplayName("Search by publisher, genre and year (custom query) - Null genre")
    void testSearchByPublisherAndGenreAndYearWithNullGenre() {
        testSearchByPublisherAndGenreAndYear(
            ValuesGenerator.validBookPublisher(),
            null,
            ValuesGenerator.validBookYear(),
            bookRepository::getWithPublisherGenreAndYear
        );
    }

    /**
     * Tests that searching by publisher, genre and year using the custom method, sending a {@code
     * null} year.
     */
    @Test
    @DisplayName("Search by publisher, genre and year (custom query) - Null year")
    void testSearchByPublisherAndGenreAndYearWithNullYear() {
        testSearchByPublisherAndGenreAndYear(
            ValuesGenerator.validBookGenre(),
            ValuesGenerator.validBookGenre(),
            null,
            bookRepository::getWithPublisherGenreAndYear
        );
    }

    /**
     * Tests that searching by publisher, genre and year using the custom method, sending a {@code
     * null} publisher and genre.
     */
    @Test
    @DisplayName("Search by publisher, genre and year (custom query) - Null publisher and genre")
    void testSearchByPublisherAndGenreAndYearWithNullPublisherAndGenre() {
        testSearchByPublisherAndGenreAndYear(
            null,
            null,
            ValuesGenerator.validBookYear(),
            bookRepository::getWithPublisherGenreAndYear
        );
    }

    /**
     * Tests that searching by publisher, genre and year using the custom method, sending a {@code
     * null} publisher and year.
     */
    @Test
    @DisplayName("Search by publisher, genre and year (custom query) - Null publisher and year")
    void testSearchByPublisherAndGenreAndYearWithNullPublisherAndYear() {
        testSearchByPublisherAndGenreAndYear(
            null,
            ValuesGenerator.validBookGenre(),
            null,
            bookRepository::getWithPublisherGenreAndYear
        );
    }

    /**
     * Tests that searching by publisher, genre and year using the custom method, sending a {@code
     * null} genre and year.
     */
    @Test
    @DisplayName("Search by publisher, genre and year (custom query) - Null genre and year")
    void testSearchByPublisherAndGenreAndYearWithNullGenreAndYear() {
        testSearchByPublisherAndGenreAndYear(
            ValuesGenerator.validBookPublisher(),
            null,
            null,
            bookRepository::getWithPublisherGenreAndYear
        );
    }

    /**
     * Tests that searching by publisher, genre and year using the custom method, sending all {@code
     * null}s.
     */
    @Test
    @DisplayName("Search by publisher, genre and year (custom query) - Null all arguments")
    void testSearchByPublisherAndGenreAndYearWithNullEverything() {
        testSearchByPublisherAndGenreAndYear(
            null,
            null,
            null,
            bookRepository::getWithPublisherGenreAndYear
        );
    }

    /**
     * Tests that searching by publisher, genre and year returns an empty {@link java.util.List} as
     * there is no {@link Book} with the given publisher (even though, there are {@link Book}s with
     * the given genre and year).
     */
    @Test
    @DisplayName("Search by publisher, genre and year - Publisher not contained")
    void testSearchByPublisherAndGenreAndYearPublisherNotContained() {
        final var publisher = ValuesGenerator.validBookPublisher();
        testNotContained(
            Predicate.not(book -> book.getPublisher().equals(publisher)),
            (repository, first) -> repository.getByPublisherAndGenreAndYear(
                publisher,
                first.getGenre(),
                first.getYear()
            ),
            "Searching by publisher, genre and year does not work as expected."
                + " The returned List of Books should be empty"
                + " as there are no Books with the given publisher."
        );
    }

    /**
     * Tests that searching by publisher, genre and year returns an empty {@link java.util.List} as
     * there is no {@link Book} with the given genre (even though, there are {@link Book}s with the
     * given publisher and year).
     */
    @Test
    @DisplayName("Search by publisher, genre and year - Genre not contained")
    void testSearchByPublisherAndGenreAndYearGenreNotContained() {
        final var genre = ValuesGenerator.validBookGenre();
        testNotContained(
            Predicate.not(book -> book.getGenre().equals(genre)),
            (repository, first) -> repository.getByPublisherAndGenreAndYear(
                first.getPublisher(),
                genre,
                first.getYear()
            ),
            "Searching by publisher, genre and year does not work as expected."
                + " The returned List of Books should be empty"
                + " as there are no Books with the given genre."
        );
    }

    /**
     * Tests that searching by publisher, genre and year returns an empty {@link java.util.List} as
     * there is no {@link Book} with the given year (even though, there are {@link Book}s with the
     * given publisher and genre).
     */
    @Test
    @DisplayName("Search by publisher, genre and year - Year not contained")
    void testSearchByPublisherAndGenreAndYearYearNotContained() {
        final var year = ValuesGenerator.validBookYear();
        testNotContained(
            Predicate.not(book -> book.getYear().equals(year)),
            (repository, first) -> repository.getByPublisherAndGenreAndYear(
                first.getPublisher(),
                first.getGenre(),
                year
            ),
            "Searching by publisher, genre and year does not work as expected."
                + " The returned List of Books should be empty"
                + " as there are no Books with the given year."
        );
    }


    /**
     * Abstract test for retrieving operations.
     *
     * @param retrievingOperation A {@link BiFunction} that given a {@link BookRepository} and an
     * object of type {@code C}, it returns an {@link Optional} of {@link Book}. The object of type
     * {@code C} is the condition used to retrieve the {@link Book}.
     * @param conditionGetter A {@link Function} that given a {@link Book} retrieves the condition
     * used to retrieve from the {@link BookRepository}.
     * @param <C> The concrete type of the condition.
     */
    private <C> void retrieveTesting(
        final BiFunction<BookRepository, C, Optional<Book>> retrievingOperation,
        final Function<Book, C> conditionGetter) {
        RepositoriesTestHelper.retrieveTesting(
            bookRepository,
            retrievingOperation,
            conditionGetter,
            entityManager,
            TestHelper::mockBookList,
            BookAssertions::assertSame
        );
    }

    /**
     * Abstract test for searching {@link Book}s by publisher, genre and year when the returned
     * {@link List} must be empty.
     *
     * @param bookFilter A {@link Predicate} that filters {@link Book}s that should be stored in the
     * database. For example, the caller can state that {@link Book}s with a certain condition
     * should not be stored in the database (for example, with a given genre), so searching by
     * publisher, genre and year will always return an empty {@link List} when using the said
     * genre.
     * @param searchingFunction A {@link BiFunction} that will be called with the {@link
     * BookRepository} and the first {@link Book} in the generated {@link Book}s {@link List}, and
     * must return what the {@link BookRepository#getByPublisherAndGenreAndYear(String, String,
     * String)} returns.
     * @param message The message to be displayed in case of failure.
     */
    private void testNotContained(
        final Predicate<Book> bookFilter,
        final BiFunction<BookRepository, Book, List<Book>> searchingFunction,
        final String message) {

        final var size = 10;
        final var books = Stream.generate(TestHelper::mockBook)
            .filter(bookFilter)
            .limit(size)
            .collect(Collectors.toList());

        books.forEach(entityManager::persist);
        entityManager.flush();

        Assertions.assertTrue(
            searchingFunction.apply(bookRepository, books.get(0)).isEmpty(),
            message
        );
    }

    /**
     * Clones the given {@link Book} (i.e creates a new {@link Book} instance using the same values
     * as the given).
     *
     * @return A new instance of the {@link Book}.
     */
    private static Book cloneBook(final Book book) {
        return new Book(
            book.getGenre(),
            book.getAuthor(),
            book.getImage(),
            book.getTitle(),
            book.getSubtitle(),
            book.getPublisher(),
            book.getYear(),
            book.getPages(),
            book.getIsbn()
        );
    }

    /**
     * A {@link FunctionalInterface} to search a {@link List} of {@link Book}s with their publisher,
     * genre and year matching the given ones.
     */
    @FunctionalInterface
    private interface BooksFinder {

        /**
         * Searches for {@link Book}s matching the given parameters.
         *
         * @param publisher The publisher to be matched
         * @param genre The genre to be matched.
         * @param year The year to be matched.
         * @return The matching {@link Book}s.
         */
        List<Book> find(final String publisher, final String genre, final String year);
    }

    /**
     * An abstract test for searching by publisher genre and year
     *
     * @param booksFinder The {@link BooksFinder} to be used.
     */
    private void testSearchByPublisherAndGenreAndYear(final BooksFinder booksFinder) {
        testSearchByPublisherAndGenreAndYear(
            ValuesGenerator.validBookPublisher(),
            ValuesGenerator.validBookGenre(),
            ValuesGenerator.validBookYear(),
            booksFinder
        );
    }

    /**
     * An abstract test for searching by publisher genre and year. It accepts {@code null} params.
     *
     * @param publisher The publisher used to search.
     * @param genre The genre used to search.
     * @param year The year used to search.
     * @param booksFinder The {@link BooksFinder} to be used.
     */
    private void testSearchByPublisherAndGenreAndYear(
        final String publisher,
        final String genre,
        final String year,
        final BooksFinder booksFinder) {

        final var size = 10;
        final var books = Stream.concat(
            Stream.generate(() ->
                withPublisherGenreAndYear(
                    Optional.ofNullable(publisher).orElseGet(ValuesGenerator::validBookGenre),
                    Optional.ofNullable(genre).orElseGet(ValuesGenerator::validBookGenre),
                    Optional.ofNullable(year).orElseGet(ValuesGenerator::validBookGenre)
                )
            ).limit(size),
            Stream.generate(TestHelper::mockBook).limit(size)
        ).collect(Collectors.toList());

        books.forEach(entityManager::persist);
        entityManager.flush();

        // The second stream might have added more
        final var matchingBooks = books.stream()
            .filter(book -> publisher == null || book.getPublisher().equals(publisher))
            .filter(book -> genre == null || book.getGenre().equals(genre))
            .filter(book -> year == null || book.getYear().equals(year))
            .collect(Collectors.toList());

        Assertions.assertEquals(
            matchingBooks,
            booksFinder.find(publisher, genre, year),
            "Searching by publisher, genre and year does not work as expected."
                + " The returned List of Books is not the expected."
        );
    }

    /**
     * Creates a {@link Book} with the given publisher, genre and year.
     *
     * @param publisher The publisher.
     * @param genre The genre.
     * @param year The year.
     * @return The created {@link Book}.
     */
    private static Book withPublisherGenreAndYear(
        final String publisher,
        final String genre,
        final String year) {

        return new Book(
            genre,
            ValuesGenerator.validBookAuthor(),
            ValuesGenerator.validBookImage(),
            ValuesGenerator.validBookTitle(),
            ValuesGenerator.validBookSubtitle(),
            publisher,
            year,
            ValuesGenerator.validBookPages(),
            ValuesGenerator.validBookIsbn()
        );
    }
}
