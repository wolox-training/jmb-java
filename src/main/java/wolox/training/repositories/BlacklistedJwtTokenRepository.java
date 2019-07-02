package wolox.training.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import wolox.training.models.BlacklistedJwtToken;

/**
 * A repository for {@link BlacklistedJwtToken}.
 */
@Repository
public interface BlacklistedJwtTokenRepository extends CrudRepository<BlacklistedJwtToken, String> {

}
