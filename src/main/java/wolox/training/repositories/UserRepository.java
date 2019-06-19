package wolox.training.repositories;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import wolox.training.models.User;

/**
 * A repository for {@link User}s.
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Returns one {@link User} with the given {@code username}
     *
     * @param username The username used to search for a {@link User}.
     * @return An {@link Optional} containing a {@link User} with the given {@code username} if
     * there is such, or empty otherwise.
     */
    Optional<User> getFirstByUsername(final String username);
}
