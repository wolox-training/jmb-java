package wolox.training.repositories;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import wolox.training.models.Book;
import wolox.training.utils.BookAssertions;
import wolox.training.utils.TestHelper;

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
     * Tests that retrieving an existing {@link Book} with the {@link BookRepository#getByAuthor(String)}
     * performs as expected: Returns a non empty {@link java.util.Optional} containing the existing
     * {@link Book} (i.e with the same values).
     */
    @Test
    @DisplayName("Retrieve Book by author")
    void testRetrieveByAuthor() {
        retrieveTesting(BookRepository::getByAuthor, Book::getAuthor);
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
}
