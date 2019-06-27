package wolox.training.repositories;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import wolox.training.models.Book;

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
     * Abstract saving test.
     *
     * @param repository The {@link CrudRepository} to be tested.
     * @param entityManager An {@link EntityManager} used to prepare the database before testing.
     * @param entitySupplier A {@link Supplier} that creates an entity to be stored.
     * @param klass The {@link Class} of the entity.
     * @param idGetter A {@link Function} that given an entity, returns its id.
     * @param defaultIdValue The default id value (i.e the id of an entity when it has not been
     * saved yet).
     * @param idComparator The comparator of the id.
     * @param assertSame An {@link Assertion} to check whether the saved and the retrieved entities
     * are the same (this performs more than equality check, which is done by the {@link
     * Object#equals(Object)} method, as it also checks other values).
     * @param <T> The concrete type of the entity.
     * @param <ID> The concrete type of the id.
     * @param <R> The concrete type of the {@code repository}.
     */
    /* package */
    static <T, ID, R extends CrudRepository<T, ID>> void testSave(
        final R repository,
        final EntityManager entityManager,
        final Supplier<T> entitySupplier,
        final Class<T> klass,
        final Function<T, ID> idGetter,
        final ID defaultIdValue,
        final Comparator<ID> idComparator,
        final Assertion<T> assertSame) {

        final var savedEntity = repository.save(entitySupplier.get());
        entityManager.flush();
        final var id = idGetter.apply(savedEntity);
        final var retrievedEntity = entityManager.find(klass, id);

        Assertions.assertAll(
            "The saving operation did not perform as expected",
            () -> Assertions.assertTrue(
                idComparator.compare(id, defaultIdValue) != 0,
                "The id has not been set"
            ),
            () -> Assertions.assertNotNull(
                retrievedEntity,
                "The entity has not been stored"
            )
        );
        assertSame.doAssert(
            savedEntity,
            retrievedEntity,
            "The entity has been saved with different values"
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
