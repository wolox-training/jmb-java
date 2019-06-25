package wolox.training.services.authentication;

import java.util.Optional;

/**
 * Defines behaviour for an object that can provide JWT token services.
 */
public interface JwtTokenService {

    /**
     * Issues a token for a {@link wolox.training.models.User} with the given {@code username} if
     * the given {@code password} matches the {@link wolox.training.models.User}'s password.
     *
     * @param username The {@link wolox.training.models.User}'s username.
     * @param password The {@link wolox.training.models.User}'s password.
     * @return A JWT for the {@link wolox.training.models.User} with the given {@code username}.
     * @throws wolox.training.exceptions.AuthenticationException If there is no {@link
     * wolox.training.models.User} with the given {@code username}, or if the {@code password} does
     * not match.
     */
    String issueToken(final String username, final String password);

    /**
     * Decodes the given {@code rawToken} trying to interpret it as a JWT. If it can be decoded,
     * then it is verified (i.e using the public key, and by checking it is not blacklisted).
     *
     * @param rawToken The {@link String} to be decoded.
     * @return An {@link Optional} containing the {@link TokenData} extracted from the JWT if it
     * could be decoded being interpreted as a JWT, and if the verification did not fail, or empty
     * if either of the mentioned steps failed, and both the {@code failOnInvalid} and {@code
     * failOnBlacklisted} flags are not set.
     * @throws wolox.training.exceptions.AuthenticationException If the given {@code rawToken} could
     * not be decoded or verified with the public key, and the {@code failOnInvalid} flag is set, or
     * if the {@code rawToken} could be decoded and verified with the public key, but it was
     * blacklisted, and the {@code failOnBlacklisted} flag is set.
     */
    Optional<TokenData> decodeAndVerify(final String rawToken);

    /**
     * Blacklists the token with the given {@code id}.
     *
     * @param id The id of the token to be blacklisted.
     */
    void blacklistToken(final String id);
}
