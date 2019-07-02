package wolox.training.repositories;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import wolox.training.utils.UserAssertions;
import wolox.training.utils.ValuesGenerator;

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
        RepositoriesTestHelper.testSave(
            userRepository,
            entityManager,
            TestHelper::mockUser,
            User.class,
            User::getId,
            0L,
            Long::compare,
            UserAssertions::assertSame
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
     * Tests that retrieving an existing {@link User} with the {@link UserRepository#getFirstByUsername(String)}
     * performs as expected: Returns a non empty {@link Optional} containing the existing {@link
     * User} (i.e with the same values).
     */
    @Test
    @DisplayName("Retrieve User by username")
    void testRetrieveByUsername() {
        retrieveTesting(UserRepository::getFirstByUsername, User::getUsername);
    }

    /**
     * Tests that searching by username does not throw any exception when the database has more than
     * one {@link User} with the same username.
     */
    @Test
    @DisplayName("Retrieve User by username - More than one User with same username in the database")
    void testSeveralUsersWithSameUsername() {
        RepositoriesTestHelper.testSeveralInstancesAndJustOneSearch(
            userRepository,
            UserRepository::getFirstByUsername,
            User::getUsername,
            entityManager,
            TestHelper::mockUser,
            UserRepositoryTest::cloneUser,
            "An unexpected exception was thrown when retrieving a User by username"
                + " when there is more than one User with the same username in the database"
        );
    }


    /**
     * Tests that searching by birth date between and name matching returns all the matching {@link
     * User}s using the naming template method.
     */
    @Test
    @DisplayName("Search by birth date between and name matching (naming template) - Contained")
    void testSearchByBirthDateBetweenAndNameMatchingContainedNamingTemplate() {
        testSearchByBirthDateBetweenAndNameMatching(
            userRepository::getByBirthDateBetweenAndNameContainingIgnoreCase
        );
    }

    /**
     * Tests that searching by birth date between and name matching returns all the matching {@link
     * User}s using the custom query method.
     */
    @Test
    @DisplayName("Search by birth date and name matching (custom query) - Contained")
    void testSearchByBirthDateBetweenAndNameMatchingContainedCustomQuery() {
        testSearchByBirthDateBetweenAndNameMatching(userRepository::getWithBirthDateAndName);
    }

    /**
     * Tests that searching by birth date between and name matching using the custom method, sending
     * a {@code null} from Date.
     */
    @Test
    @DisplayName("Search by birth date between and name matching (custom query) - Null from date")
    void testSearchByBirthDateBetweenAndNameMatchingWithNullFromDate() {
        testSearchByBirthDateBetweenAndNameMatching(
            null,
            FROM.plusYears(PLUS_YEARS),
            Faker.instance().name().firstName(),
            userRepository::getWithBirthDateAndName
        );
    }

    /**
     * Tests that searching by birth date between and name matching using the custom method, sending
     * a {@code null} to Date.
     */
    @Test
    @DisplayName("Search by birth date between and name matching (custom query) - Null to date")
    void testSearchByBirthDateBetweenAndNameMatchingWithNullToDate() {
        testSearchByBirthDateBetweenAndNameMatching(
            FROM,
            null,
            Faker.instance().name().firstName(),
            userRepository::getWithBirthDateAndName
        );
    }

    /**
     * Tests that searching by birth date between and name matching using the custom method, sending
     * a {@code null} name pattern.
     */
    @Test
    @DisplayName("Search by birth date between and name matching (custom query) - Null name pattern")
    void testSearchByBirthDateBetweenAndNameMatchingWithNullNamePattern() {
        testSearchByBirthDateBetweenAndNameMatching(
            FROM,
            FROM.plusYears(PLUS_YEARS),
            null,
            userRepository::getWithBirthDateAndName
        );
    }

    /**
     * Tests that searching by birth date between and name matching using the custom method, sending
     * a {@code null} from and to Date.
     */
    @Test
    @DisplayName("Search by birth date between and name matching (custom query) - Null from and to date")
    void testSearchByBirthDateBetweenAndNameMatchingWithNullFromAndToDate() {
        testSearchByBirthDateBetweenAndNameMatching(
            null,
            null,
            Faker.instance().name().firstName(),
            userRepository::getWithBirthDateAndName
        );
    }

    /**
     * Tests that searching by birth date between and name matching using the custom method, sending
     * a {@code null} from and name pattern.
     */
    @Test
    @DisplayName("Search by birth date between and name matching (custom query) - Null from and name pattern")
    void testSearchByBirthDateBetweenAndNameMatchingWithNullFromDateAndNamePattern() {
        testSearchByBirthDateBetweenAndNameMatching(
            null,
            FROM.plusYears(PLUS_YEARS),
            null,
            userRepository::getWithBirthDateAndName
        );
    }

    /**
     * Tests that searching by birth date between and name matching using the custom method, sending
     * a {@code null} to and name pattern.
     */
    @Test
    @DisplayName("Search by birth date between and name matching (custom query) - Null to and name pattern")
    void testSearchByBirthDateBetweenAndNameMatchingWithNullToDateAndNamePattern() {
        testSearchByBirthDateBetweenAndNameMatching(
            FROM,
            null,
            null,
            userRepository::getWithBirthDateAndName
        );
    }

    /**
     * Tests that searching by birth date between and name matching using the custom method, sending
     * a {@code null} from, to and name pattern.
     */
    @Test
    @DisplayName("Search by birth date between and name matching (custom query) - Null from, to and name pattern")
    void testSearchByBirthDateBetweenAndNameMatchingWithNullFromAndToDateAndNamePattern() {
        testSearchByBirthDateBetweenAndNameMatching(
            null,
            null,
            null,
            userRepository::getWithBirthDateAndName
        );
    }


    /**
     * Tests that searching by birth date between and name matching returns an empty {@link
     * java.util.List} as there is no {@link User} with the given name pattern (even though, there
     * are {@link User}s with the birth date between given ones).
     */
    @Test
    @DisplayName("Search by birth date between and name matching - No birth date between")
    void testSearchByBirthDateBetweenAndNameMatchingNoBirthDateBetweenNotContained() {
        final var from = FROM;
        final var to = from.plusYears(PLUS_YEARS);
        testNotContained(
            user -> user.getBirthDate().isBefore(from) || user.getBirthDate().isAfter(to),
            (repository, first) -> repository.getByBirthDateBetweenAndNameContainingIgnoreCase(
                from,
                to,
                first.getName()
            ),
            "Searching by birth date between and name matching does not work as expected."
                + " The returned List of Users should be empty"
                + " as there are no Users whose birth date is between the given from and to dates."
        );
    }

    /**
     * Tests that searching by birth date between and name matching returns an empty {@link
     * java.util.List} as there is no {@link User} with the given name pattern (even though, there
     * are {@link User}s with the birth date between given ones).
     */
    @Test
    @DisplayName("Search by birth date between and name matching - Name pattern not contained")
    void testSearchByBirthDateBetweenAndNameMatchingNamePatternNotContained() {
        final var namePattern = Faker.instance().name().firstName();
        testNotContained(
            Predicate.not(user -> user.getName().toLowerCase().contains(namePattern.toLowerCase())),
            (repository, first) -> repository.getByBirthDateBetweenAndNameContainingIgnoreCase(
                first.getBirthDate().minusYears(5),
                first.getBirthDate().plusYears(5),
                namePattern
            ),
            "Searching by birth date between and name matching does not work as expected."
                + " The returned List of Users should be empty"
                + " as there are no Users whose name match the pattern."
        );
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
            UserRepository::getFirstByUsername,
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
            UserAssertions::assertSame
        );
    }

    /**
     * Abstract test for searching {@link User}s by birth date between and name matching when the
     * returned {@link List} must be empty.
     *
     * @param userFilter A {@link Predicate} that filters {@link User}s that should be stored in the
     * database. For example, the caller can state that {@link User}s with a certain condition
     * should not be stored in the database (for example, with a given name pattern), so searching
     * by birth date between and name matching will always return an empty {@link List} when using
     * the said name pattern.
     * @param searchingFunction A {@link BiFunction} that will be called with the {@link
     * UserRepository} and the first {@link User} in the generated {@link User}s {@link List}, and
     * must return what the {@link UserRepository#getByBirthDateBetweenAndNameContainingIgnoreCase(LocalDate,
     * LocalDate, String)} returns.
     * @param message The message to be displayed in case of failure.
     */
    private void testNotContained(
        final Predicate<User> userFilter,
        final BiFunction<UserRepository, User, List<User>> searchingFunction,
        final String message) {

        final var size = 10;
        final var users = Stream.generate(TestHelper::mockUser)
            .filter(userFilter)
            .limit(size)
            .collect(Collectors.toList());

        users.forEach(entityManager::persist);
        entityManager.flush();

        Assertions.assertTrue(
            searchingFunction.apply(userRepository, users.get(0)).isEmpty(),
            message
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

    /**
     * Clones the given {@link User} (i.e creates a new {@link User} instance using the same values
     * as the given).
     *
     * @return A new instance of the {@link User}.
     */
    private static User cloneUser(final User user) {
        return new User(
            user.getUsername(),
            user.getName(),
            user.getBirthDate()
        );
    }


    /**
     * A {@link FunctionalInterface} to search a {@link List} of {@link User}s with their birth date
     * between two given {@link LocalDate}s and with a name matching a pattern.
     */
    @FunctionalInterface
    private interface UsersFinder {

        /**
         * Searches for {@link User}s matching the given parameters.
         *
         * @param from The min. birth date {@link LocalDate}.
         * @param to The max. birth date {@link LocalDate}.
         * @param namePattern A pattern to be matched in the name.
         * @return The matching {@link User}s.
         */
        List<User> find(final LocalDate from, final LocalDate to, final String namePattern);
    }

    private void testSearchByBirthDateBetweenAndNameMatching(final UsersFinder usersFinder) {
        testSearchByBirthDateBetweenAndNameMatching(
            FROM,
            FROM.plusYears(PLUS_YEARS),
            Faker.instance().name().firstName(),
            usersFinder
        );
    }

    /**
     * An abstract test for searching with birth date between and name matching a pattern. It
     * accepts {@code null} params.
     *
     * @param from The min. birth date {@link LocalDate}.
     * @param to The max. birth date {@link LocalDate}.
     * @param namePattern A pattern to be matched in the name.
     * @param usersFinder The {@link UsersFinder} to be used.
     */
    private void testSearchByBirthDateBetweenAndNameMatching(
        final LocalDate from,
        final LocalDate to,
        final String namePattern,
        final UsersFinder usersFinder) {

        final var size = 10;
        final var users = Stream.concat(
            Stream.generate(() ->
                withBirthDateBetweenAndNameContaining(
                    Optional.ofNullable(from).orElseGet(() -> FROM),
                    Optional.ofNullable(to).orElseGet(() -> FROM.plusYears(PLUS_YEARS)),
                    Optional.ofNullable(namePattern)
                        .orElseGet(() -> Faker.instance().name().firstName())
                )
            ).limit(size),
            Stream.generate(TestHelper::mockUser).limit(size)
        ).collect(Collectors.toList());

        users.forEach(entityManager::persist);
        entityManager.flush();

        // The second stream might have added more
        final var matchingUsers = users.stream()
            .filter(user -> from == null || user.getBirthDate().isAfter(from))
            .filter(user -> to == null || user.getBirthDate().isBefore(to))
            .filter(user -> namePattern == null
                || user.getName().toLowerCase().contains(namePattern.toLowerCase())
            )
            .collect(Collectors.toList());

        Assertions.assertAll(
            "Searching by birth date between and a pattern in the name"
                + " does not work as expected."
                + " The returned List of Users is not the expected.",
            () -> Assertions.assertEquals(
                matchingUsers,
                usersFinder.find(from, to, namePattern),
                "Same case is failing"
            ),
            () -> Assertions.assertEquals(
                matchingUsers,
                usersFinder.find(
                    from,
                    to,
                    Optional.ofNullable(namePattern).map(String::toLowerCase).orElse(null)
                ),
                "Lowercase not ignored"
            ),
            () -> Assertions.assertEquals(
                matchingUsers,
                usersFinder.find(
                    from,
                    to,
                    Optional.ofNullable(namePattern).map(String::toUpperCase).orElse(null)
                ),
                "Uppercase not ignored"
            )
        );
    }

    /**
     * Creates a {@link User} with the its birth date between the given {@code from} and {@code to},
     * and with a name containing the given {@code pattern}.
     *
     * @param from The min. limit for the birth date.
     * @param to The max. limit for the birth date.
     * @param pattern A pattern used to search by name.
     * @return The created {@link User}.
     */
    private static User withBirthDateBetweenAndNameContaining(
        final LocalDate from,
        final LocalDate to,
        final String pattern) {

        final var stringBuilder = new StringBuilder();
        // Randomly add some text before and after the pattern
        if (new Random().nextBoolean()) {
            stringBuilder.append(Faker.instance().name().title()).append(" ");
        }
        stringBuilder.append(pattern);
        if (new Random().nextBoolean()) {
            stringBuilder.append(" ").append(Faker.instance().name().lastName());
        }
        final var name = stringBuilder.toString();

        final var now = LocalDate.now();
        final var minDate = Period.between(to, now).getYears();
        final var maxDate = Period.between(from, now).getYears();
        final var birthDate = Faker.instance().date()
            .birthday(minDate, maxDate)
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();

        return new User(ValuesGenerator.validUserUsername(), name, birthDate);
    }

    /**
     * A {@link LocalDate} to be used as a starting point.
     */
    private static LocalDate FROM = LocalDate.ofYearDay(1950, 1);

    /**
     * A number that indicates how many years will be added to the {@link #FROM} {@link LocalDate}
     * when generating data.
     */
    private static int PLUS_YEARS = 10;
}
