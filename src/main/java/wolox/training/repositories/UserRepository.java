package wolox.training.repositories;

import java.time.LocalDate;
import java.util.List;
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

    /**
     * Returns all the {@link User}s whose birth date is between the given {@code from} and {@code
     * to} {@link LocalDate}s, and there name contains the given {@code namePattern}
     * (case-insensitive).
     *
     * @param from The min. {@link LocalDate} for the birth date.
     * @param to The max. {@link LocalDate} for the birth date.
     * @param namePattern The pattern to be matched in the name.
     * @return The {@link User}s matching the criteria.
     */
    List<User> getByBirthDateBetweenAndNameContainingIgnoreCase(
        final LocalDate from,
        final LocalDate to,
        final String namePattern
    );
}
