package wolox.training.services.authentication;


import java.util.Collections;
import java.util.Set;
import lombok.Getter;

/**
 * Object that encapsulates information taken of a JWT token.
 */
@Getter
public final class TokenData {

    /**
     * The token's id.
     */
    private final String id;

    /**
     * The token's owner username.
     */
    private final String username;

    /**
     * The grants given to the token.
     */
    private final Set<String> grants;

    /**
     * @param id The token's id.
     * @param username The token's owner username.
     * @param grants The grants given to the token.
     */
    TokenData(final String id, final String username, final Set<String> grants) {
        this.id = id;
        this.username = username;
        this.grants = Collections.unmodifiableSet(grants); // This set cannot be changed.
    }
}
