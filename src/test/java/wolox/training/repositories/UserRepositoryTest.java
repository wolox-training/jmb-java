package wolox.training.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import wolox.training.models.Book;
import wolox.training.models.User;
import wolox.training.utils.TestHelper;

/**
 * Tests for the {@link UserRepository}.
 */
@DataJpaTest(
    properties = {
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.format_sql=true"
    }
)
class UserRepositoryTest {

    /**
     * The {@link UserRepository} to be tested.
     */
    private final UserRepository userRepository;

    /**
     * The {@link EntityManager} used to prepare the database before performing the action to be
     * tested, or to query the database state to perform assertions once the action being tested has
     * been performed.
     */
    private final EntityManager entityManager;


    /**
     * Constructor.
     *
     * @param userRepository The {@link UserRepository} to be tested.
     * @param entityManager The {@link EntityManager} used to prepare the database before performing
     * the action to be tested, or to query the database state to perform assertions once the
     */
    @Autowired
    UserRepositoryTest(
        final UserRepository userRepository,
        final EntityManager entityManager) {

        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }


    /**
     * Tests that saving a {@link User} with the {@link UserRepository#save(Object)} performs as
     * expected: Sets and id to the {@link User} and stores it in the database with the
     * corresponding values.
     */
    @Test
    @DisplayName("Save User")
    void testSavingAUser() {
        final var savedUser = userRepository.save(TestHelper.mockUser());
        entityManager.flush();
        final var retrievedUser = entityManager.find(User.class, savedUser.getId());
        Assertions.assertAll(
            "The saving operation did not perform as expected",
            () -> Assertions.assertTrue(
                savedUser.getId() != 0,
                "The id has not been set"
            ),
            () -> Assertions.assertNotNull(
                retrievedUser,
                "The user has not been stored"
            )
        );
        // Performs outside of the assertAll as this depends on the previous assertions
        RepositoriesTestHelper.assertSame(
            savedUser,
            retrievedUser,
            "The user has been saved with different values"
        );
    }

    /**
     * Tests that retrieving an existing {@link User} with the {@link UserRepository#findById(Object)}
     * performs as expected: Returns a non empty {@link Optional} containing the existing {@link
     * User} (i.e with the same values).
     */
    @Test
    @DisplayName("Retrieve User by id")
    void testRetrieveById() {
        retrieveTesting(UserRepository::findById, User::getId);
    }

    /**
     * Tests that retrieving an existing {@link User} with the {@link UserRepository#getByUsername(String)}
     * performs as expected: Returns a non empty {@link Optional} containing the existing {@link
     * User} (i.e with the same values).
     */
    @Test
    @DisplayName("Retrieve User by username")
    void testRetrieveByUsername() {
        retrieveTesting(UserRepository::getByUsername, User::getUsername);
    }

    /**
     * Tests that the {@link UserRepository} returns a {@link User} with the lazy loaded collections
     * without being initialized, when finding by id.
     *
     * @param platformTransactionManager A {@link PlatformTransactionManager} used to create a
     * {@link TransactionTemplate} in order to execute initialization stuff within a transaction.
     */
    @Test
    // DataJpaTest wraps all tests in a transaction, feature that must be turned off for this test.
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("Lazy loading - find by id")
    void testLazyLoadingWhenFindingById(
        @Autowired final PlatformTransactionManager platformTransactionManager) {
        testLazyInitialization(platformTransactionManager, UserRepository::findById, User::getId);
    }

    /**
     * Tests that the {@link UserRepository} returns a {@link User} with the lazy loaded collections
     * without being initialized, when finding by username.
     *
     * @param platformTransactionManager A {@link PlatformTransactionManager} used to create a
     * {@link TransactionTemplate} in order to execute initialization stuff within a transaction.
     */
    @Test
    // DataJpaTest wraps all tests in a transaction, feature that must be turned off for this test.
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("Lazy loading - find by username")
    void testLazyLoadingWhenFindingByUsername(
        @Autowired final PlatformTransactionManager platformTransactionManager) {
        testLazyInitialization(
            platformTransactionManager,
            UserRepository::getByUsername,
            User::getUsername
        );
    }

    /**
     * Abstract test for {@link User} retrieving operations.
     *
     * @param retrievingOperation A {@link BiFunction} that given a {@link UserRepository} and an
     * object of type {@code C}, it returns an {@link Optional} of {@link User}. The object of type
     * {@code C} is the condition used to retrieve the {@link User}.
     * @param conditionGetter A {@link Function} that given a {@link User}, it retrieves the
     * condition used to retrieve from the {@link UserRepository}.
     * @param <C> The concrete type of the condition.
     */
    private <C> void retrieveTesting(
        final BiFunction<UserRepository, C, Optional<User>> retrievingOperation,
        final Function<User, C> conditionGetter) {
        RepositoriesTestHelper.retrieveTesting(
            userRepository,
            retrievingOperation,
            conditionGetter,
            entityManager,
            TestHelper::mockUserList,
            RepositoriesTestHelper::assertSame
        );
    }

    /**
     * Lazy loaded collection abstract test. Checks that all lazy loaded collections are actually
     * lazy loaded.
     *
     * @param platformTransactionManager A {@link PlatformTransactionManager} used to create a
     * {@link TransactionTemplate} in order to execute initialization stuff within a transaction.
     * @param retrievingOperation A {@link BiFunction} that given a {@link UserRepository} and a
     * condition of type {@code C}, returns an {@link Optional} of {@link User} (i.e the one being
     * tested for lazy loading).
     * @param conditionGetter A {@link Function} that given a {@link User}, it retrieves the
     * condition used to retrieve from the {@link UserRepository}.
     * @param <C> The concrete type of the condition.
     * @apiNote This method MUST not be called withing a transaction.
     * @implNote This method uses reflection to access the books collection, as it is wrapped within
     * an {@link java.util.Collections#unmodifiableSet(Set)} in the {@link User#getBooks()}. If
     * calling this method outside a transaction, a {@link org.hibernate.LazyInitializationException}
     * will be thrown.
     */
    private <C> void testLazyInitialization(
        final PlatformTransactionManager platformTransactionManager,
        final BiFunction<UserRepository, C, Optional<User>> retrievingOperation,
        final Function<User, C> conditionGetter) {

        // Create a transaction manager to manually create transactions
        final var transactionTemplate = new TransactionTemplate(platformTransactionManager);

        // Sanity check: this method must no be called within a transaction
        verifyNoTransaction(transactionTemplate);

        // Mock books and user
        final var books = TestHelper.mockBookList(10);
        final var user = TestHelper.mockUser();

        // Preparation of database
        populateDatabase(transactionTemplate, user, books); // This commits: entities are stored

        // The following must be executed in a try-finally block.
        // Always execute the finally block (even when things go wrong) which clears the database
        try {
            final var condition = conditionGetter.apply(user);
            final var retrievedUser = retrievingOperation.apply(userRepository, condition)
                .orElseThrow(AssertionFailedError::new);

            // Get the books collection by reflection
            // (the User class encapsulates the books in an unmodifiable Set so,
            // if the collection was not initialized, an exception is thrown).
            final var retrievedBooks = ReflectionTestUtils.getField(retrievedUser, "books");

            // Verify lazy initialization by checking that the books were not initialized.
            Assertions.assertFalse(
                Hibernate.isInitialized(retrievedBooks),
                "A lazy loaded collection was initialized"
            );
        } finally {
            // Clears the database
            // (as stuff does not happen in a transaction that is rolled-back after each test)
            clearDatabase(transactionTemplate, user, books);
        }

        // Sanity check: check that the database is empty
        emptyDatabaseSanityCheck(user, books);
    }


    /**
     * Populates the database within a transaction.
     *
     * @param transactionTemplate The {@link TransactionTemplate} used to create the transaction in
     * which the database population will occur.
     * @param user The {@link User} to be added to the database.
     * @param books The {@link List} of {@link Book}s to be added to the database.
     * @apiNote This commits
     */
    private void populateDatabase(
        final TransactionTemplate transactionTemplate,
        final User user,
        final List<Book> books) {

        transactionTemplate.execute(status -> {
            // First persist books (will give them an id)
            books.forEach(entityManager::persist);
            entityManager.flush();

            // Then add books to the user and persist all
            books.forEach(user::addBook);
            entityManager.persist(user);
            entityManager.flush();

            return null;
        });
    }

    /**
     * Clears the database within a transaction.
     *
     * @param transactionTemplate The {@link TransactionTemplate} used to create the transaction in
     * which the database clear will occur.
     * @param user The {@link User} to be removed from the database.
     * @param books The {@link List} of {@link Book}s to be removed from the database.
     * @apiNote This commits
     */
    private void clearDatabase(
        final TransactionTemplate transactionTemplate,
        final User user,
        final List<Book> books) {

        transactionTemplate.execute(status -> {
                entityManager.remove(entityManager.merge(user));
                books.forEach(book -> entityManager.remove(entityManager.merge(book)));

                return null;
            }
        );
    }

    /**
     * Asserts that the given {@code user} and {@code books} are not in the database.
     *
     * @param user The {@link User} to be checked.
     * @param books The {@link List} of {@link Book}s to be checked.
     */
    private void emptyDatabaseSanityCheck(final User user, final List<Book> books) {
        Assert.state(
            entityManager.find(User.class, user.getId()) == null,
            "Users still in the database"
        );
        books.forEach(
            book -> Assert.state(
                entityManager.find(Book.class, book.getId()) == null,
                "Books still in the database"
            )
        );
    }

    /**
     * Verifies that there is no transaction in progress.
     *
     * @param transactionTemplate The {@link TransactionTemplate} used to start a no-op transaction
     * in which transaction-in-progress will be checked.
     */
    private static void verifyNoTransaction(final TransactionTemplate transactionTemplate) {
        transactionTemplate.execute(status -> {
            Assert.state(
                status.isNewTransaction(),
                "The method MUST not be called withing a transaction"
            );

            return null;
        });
    }
}
