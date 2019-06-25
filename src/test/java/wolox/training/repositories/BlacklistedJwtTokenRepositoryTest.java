package wolox.training.repositories;

import java.util.UUID;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import wolox.training.models.BlacklistedJwtToken;
import wolox.training.utils.BlacklistedJwtTokenAssertions;
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
class BlacklistedJwtTokenRepositoryTest {

    /**
     * The {@link BookRepository} to be tested.
     */
    private final BlacklistedJwtTokenRepository blacklistedJwtTokenRepository;

    /**
     * The {@link EntityManager} used to prepare the database before performing the action to be
     * tested, or to query the database state to perform assertions once the action being tested has
     * been performed.
     */
    private final EntityManager entityManager;

    /**
     * Constructor.
     *
     * @param blacklistedJwtTokenRepository The {@link BlacklistedJwtTokenRepository} to be tested.
     * @param entityManager The {@link EntityManager} used to prepare the database before performing
     * the action to be tested, or to query the database state to perform assertions once the
     * action
     */
    @Autowired
    BlacklistedJwtTokenRepositoryTest(
        final BlacklistedJwtTokenRepository blacklistedJwtTokenRepository,
        final EntityManager entityManager) {

        this.blacklistedJwtTokenRepository = blacklistedJwtTokenRepository;
        this.entityManager = entityManager;
    }


    /**
     * Tests that saving a {@link BlacklistedJwtToken} with the {@link BookRepository#save(Object)}
     * performs as expected: stores it in the database with the corresponding values.
     */
    @Test
    @DisplayName("Save Book")
    void testSavingABook() {
        RepositoriesTestHelper.testSave(
            blacklistedJwtTokenRepository,
            entityManager,
            TestHelper::mockBlacklistedJwtToken,
            BlacklistedJwtToken.class,
            BlacklistedJwtToken::getId,
            "",
            String::compareTo,
            BlacklistedJwtTokenAssertions::assertSame
        );
    }

    @Test
    @DisplayName("Exists by id - Entity exists")
    void testExistsWithExisting() {
        // Prepare database
        final var maxListSize = 10;
        final var entities = TestHelper.mockBlacklistedJwtTokenList(maxListSize);
        entities.forEach(entityManager::persist);
        entityManager.flush();

        final var id = entities.get(0).getId(); // There is always at least one

        // Assert that an entity is returned
        Assertions.assertTrue(
            blacklistedJwtTokenRepository.existsById(id),
            "The exists query did not work as expected: exists in db but returned false."
        );
    }

    @Test
    @DisplayName("Exists by id - Entity not exists")
    void testExistsWithNonExisting() {
        // Prepare database
        final var maxListSize = 10;
        TestHelper.mockBlacklistedJwtTokenList(maxListSize).forEach(entityManager::persist);
        entityManager.flush();

        // Assert that an entity is returned
        Assertions.assertFalse(
            blacklistedJwtTokenRepository.existsById(UUID.randomUUID().toString()),
            "The exists query did not work as expected: not exists in db but returned true."
        );
    }
}
