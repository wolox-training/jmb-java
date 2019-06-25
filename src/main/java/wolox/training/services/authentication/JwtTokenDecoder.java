package wolox.training.services.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtHandlerAdapter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.MissingClaimException;
import java.security.PublicKey;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import wolox.training.exceptions.AuthenticationException;

/**
 * Component in charge of decoding JWTs.
 */
@Component
class JwtTokenDecoder {

    /**
     * The {@link PublicKey} used to verify tokens created by this encoder.
     */
    private final PublicKey publicKey;
    /**
     * A {@link JwtHandlerAdapter} used to handle the decoding process.
     */
    private final JwtHandlerAdapter<Jws<Claims>> jwtHandlerAdapter;
    /**
     * A flag indicating whether the authentication process must fail if a JWT is invalid. Will
     * discard the token if not valid.
     */
    private final boolean failOnInvalid;


    /**
     * Constructor.
     *
     * @param publicKey The {@link PublicKey} used to verify tokens created by this encoder.
     * @param failOnInvalid A flag indicating whether the authentication process must fail if a JWT
     * is invalid. Will discard the token if not valid.
     */
    @Autowired
    JwtTokenDecoder(
        final PublicKey publicKey,
        @Value("${authentication.jwt.fail_on_invalid}") final boolean failOnInvalid) {
        this.publicKey = publicKey;
        this.failOnInvalid = failOnInvalid;
        this.jwtHandlerAdapter = new CustomJwtHandlerAdapter();
    }


    /**
     * Decodes the given {@code encoded} {@link String} into a {@link TokenData}. The given {@code
     * encoded} {@link String} will be tried to be interpreted as a JWT.
     *
     * @param encoded The {@link String} to be decoded.
     * @return The {@link TokenData} created from the given {@code encoded} {@link String} if it can
     * be interpreted as a JWT, or empty if it cannot be interpreted as a JWT, and the {@code
     * failOnInvalid} flag if not set.
     * @throws AuthenticationException If the {@code encoded} {@link String} cannot be interpreted
     * as a JWT, and the {@code failOnInvalid} flag is set.
     */
    Optional<TokenData> decode(final String encoded) {
        if (!StringUtils.hasText(encoded)) {
            throw new IllegalArgumentException("The token must not be null or empty");
        }
        try {
            final var claims = Jwts.parser()
                .setSigningKey(publicKey)
                .parse(encoded, jwtHandlerAdapter)
                .getBody();

            // Previous step validated the following values
            final var tokenId = claims.getId();
            final var username = claims.getSubject();
            @SuppressWarnings("unchecked") final var roles = (Set<String>) claims
                .get(Constants.ROLES_CLAIM_NAME, Set.class);

            return Optional.of(new TokenData(tokenId, username, roles));

        } catch (final JwtException e) {
            if (failOnInvalid) {
                throw new AuthenticationException("Invalid JWT", e);
            }
            return Optional.empty();
        }
    }

    /**
     * Custom implementation of {@link JwtHandlerAdapter} that will validate the token.
     */
    private static class CustomJwtHandlerAdapter extends JwtHandlerAdapter<Jws<Claims>> {

        @Override
        public Jws<Claims> onClaimsJws(final Jws<Claims> jws) {
            final var header = jws.getHeader();
            final var claims = jws.getBody();

            // Check jti is not missing
            final var jtiString = claims.getId();
            if (!StringUtils.hasText(jtiString)) {
                throw new MissingClaimException(header, claims, "Missing \"jwt id\" claim");
            }

            // Check roles is not missing
            final var rolesObject = claims.get(Constants.ROLES_CLAIM_NAME, Collection.class);
            if (rolesObject == null) {
                throw new MissingClaimException(header, claims, "Missing \"roles\" claim");
            }
            // Check roles Collection contains only Strings (discard those that are not strings)
            final var roles = ((Collection<?>) rolesObject).stream()
                .filter(role -> role instanceof String)
                .collect(Collectors.toSet());
            claims.put(Constants.ROLES_CLAIM_NAME, roles);

            // Check issued at date is present and it is not a future date
            final var issuedAt = Optional.ofNullable(claims.getIssuedAt())
                .orElseThrow(() ->
                    new MissingClaimException(header, claims, "Missing \"issued at\" date"));
            if (issuedAt.after(new Date())) {
                throw new MalformedJwtException("The \"issued at\" date is a future date");
            }
            // Check expiration date is not missing
            if (claims.getExpiration() == null) {
                throw new MissingClaimException(header, claims, "Missing \"expiration\" date");
            }

            return jws;
        }
    }
}
