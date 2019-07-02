package wolox.training.services.authentication;

import io.jsonwebtoken.Jwts;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Component in charge of encoding JWTs.
 */
@Component
class JwtTokenEncoder {

    /**
     * The {@link PrivateKey} used to sign tokens created by this encoder.
     */
    private final PrivateKey privateKey;
    /**
     * The duration of the tokens created by this encoder.
     */
    private final long duration;


    /**
     * Constructor.
     *
     * @param privateKey The {@link PrivateKey} used to sign tokens created by this encoder.
     * @param duration The duration of the tokens created by this encoder.
     */
    @Autowired
    JwtTokenEncoder(
        final PrivateKey privateKey,
        @Value("${authentication.jwt.duration}") final long duration) {
        this.privateKey = privateKey;
        this.duration = duration;
    }


    /**
     * Encodes the given {@code tokenData} into a JWT.
     *
     * @param tokenData The {@link TokenData} to be encoded.
     * @return The JWT created from the given {@code tokenData}.
     */
    String encode(final TokenData tokenData) {
        Assert.notNull(tokenData, "The token must not be null");
        final var now = Instant.now();
        return Jwts.builder()
            .setId(tokenData.getId())
            .setSubject(tokenData.getUsername())
            .claim(Constants.ROLES_CLAIM_NAME, tokenData.getGrants())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(duration)))
            .signWith(privateKey, Constants.SIGNATURE_ALGORITHM)
            .compact()
            ;
    }

}
