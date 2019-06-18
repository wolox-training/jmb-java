package wolox.training.repositories;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.repository.Repository;
import wolox.training.models.Book;
import wolox.training.models.User;

/**
 * Helper class for persistence testing.
 */
/* package */ class RepositoriesTestHelper {

    /**
     * Private constructor to avoid instantiation.
     */
    private RepositoriesTestHelper() {
    }

    /**
     * Asserts that the {@code expected} and the {@code actual} {@link Book}s are the same. This
     * method is different that {@link Assertions#assertEquals(Object, Object)} as equality is not
     * checked using the {@link Book#equals(Object)} method, but by checking all the {@link Book}
     * fields.
     *
     * @param expected The expected {@link Book}.
     * @param actual The actual {@link Book}.
     * @param message A message to be displayed in case the assertion fails.
     */
    /* package */
    static void assertSame(final Book expected, final Book actual, final String message) {
        Assertions.assertAll(
            message,
            () -> Assertions.assertEquals(
                expected.getId(),
                actual.getId(),
                "There is a mismatch in the id"
            ),
            () -> Assertions.assertEquals(
                expected.getGenre(),
                actual.getGenre(),
                "There is a mismatch in the genre"
            ),
            () -> Assertions.assertEquals(
                expected.getAuthor(),
                actual.getAuthor(),
                "There is a mismatch in the author"
            ),
            () -> Assertions.assertEquals(
                expected.getImage(),
                actual.getImage(),
                "There is a mismatch in the image"
            ),
            () -> Assertions.assertEquals(
                expected.getTitle(),
                actual.getTitle(),
                "There is a mismatch in the title"
            ),
            () -> Assertions.assertEquals(
                expected.getSubtitle(),
                actual.getSubtitle(),
                "There is a mismatch in the subtitle"
            ),
            () -> Assertions.assertEquals(
                expected.getPublisher(),
                actual.getPublisher(),
                "There is a mismatch in the publisher"
            ),
            () -> Assertions.assertEquals(
                expected.getYear(),
                actual.getYear(),
                "There is a mismatch in the year"
            ),
            () -> Assertions.assertEquals(
                expected.getPages(),
                actual.getPages(),
                "There is a mismatch in the pages"
            ),
            () -> Assertions.assertEquals(
                expected.getIsbn(),
                actual.getIsbn(),
                "There is a mismatch in the isbn"
            )
        );
    }

    /**
     * Asserts that the {@code expected} and the {@code actual} {@link User}s are the same. This
     * method is different that {@link Assertions#assertEquals(Object, Object)} as equality is not
     * checked using the {@link User#equals(Object)} method, but by checking all the {@link User}
     * fields.
     *
     * @param expected The expected {@link Book}.
     * @param actual The actual {@link Book}.
     * @param message A message to be displayed in case the assertion fails.
     */
    /* package */
    static void assertSame(final User expected, final User actual, final String message) {
        Assertions.assertAll(
            message,
            () -> Assertions.assertEquals(
                expected.getId(),
                actual.getId(),
                "There is a mismatch in the id"
            ),
            () -> Assertions.assertEquals(
                expected.getUsername(),
                actual.getUsername(),
                "There is a mismatch in the username"
            ),
            () -> Assertions.assertEquals(
                expected.getName(),
                actual.getName(),
                "There is a mismatch in the name"
            ),
            () -> Assertions.assertEquals(
                expected.getBirthDate(),
                actual.getBirthDate(),
                "There is a mismatch in the birth date"
            )
        );
    }


    /**
     * Abstract test for retrieving operations.
     *
     * @param repository The {@link Repository} to be tested.
     * @param retrievingOperation A {@link BiFunction} that given a {@link BookRepository} and an
     * object of type {@code C}, it returns an {@link Optional} of {@link Book}. The object of type
     * {@code C} is the condition used to retrieve the {@link Book}.
     * @param conditionGetter A {@link Function} that given a {@link Book} retrieves the condition
     * used to retrieve from the {@link BookRepository}.
     * @param entityManager An {@link EntityManager} used to prepare the database before testing.
     * @param listMocker An {@link IntFunction} that, given an {@code int}, it creates a {@link
     * List} of mocked entities of type {@code T} to be stored in the database before testing. It
     * must return at least one entity.
     * @param assertion An {@link Assertion} used to assert that the retrieved entity is the same as
     * the expected.
     * @param <T> The concrete type of the entity.
     * @param <R> The concrete type of the {@link Repository}.
     * @param <C> The concrete type of the condition.
     */
    /* package */
    static <T, R extends Repository<T, ?>, C> void retrieveTesting(
        final R repository,
        final BiFunction<R, C, Optional<T>> retrievingOperation,
        final Function<T, C> conditionGetter,
        final EntityManager entityManager,
        final IntFunction<List<T>> listMocker,
        final Assertion<T> assertion) {

        // Prepare database
        final var maxListSize = 10;
        final var entities = listMocker.apply(maxListSize);
        final var entity = entities.get(0); // There is always at least one.
        entities.forEach(entityManager::persist);
        entityManager.flush();

        // Perform the retrieval from the database
        final var condition = conditionGetter.apply(entity);
        final var retrievedOptional = retrievingOperation.apply(repository, condition);

        // Assert that an entity is returned
        Assertions.assertTrue(
            retrievedOptional.isPresent(),
            "The retrieving operation did not retrieve an existing entity"
        );

        // Assert that the entity is the same as the expected.
        assertion.doAssert(
            entity,
            retrievedOptional.get(),
            "Another entity has been retrieved"
        );
    }

    /**
     * A {@link FunctionalInterface} for assertions taking an expected and actual objects of type
     * {@code T}s, together with a {@code message}.
     *
     * @param <T> The concrete type of the objects.
     */
    @FunctionalInterface
    interface Assertion<T> {

        /**
         * Performs the assertion with the {@code expected} and {@code actual} objects.
         *
         * @param expected The expected object.
         * @param actual The actual object.
         * @param message The message to be displayed in case the assertion fails.
         */
        void doAssert(final T expected, final T actual, final String message);
    }

}
