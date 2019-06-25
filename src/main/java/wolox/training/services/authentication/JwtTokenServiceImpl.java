package wolox.training.services.authentication;

import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wolox.training.exceptions.AuthenticationException;
import wolox.training.models.BlacklistedJwtToken;
import wolox.training.models.User;
import wolox.training.repositories.BlacklistedJwtTokenRepository;
import wolox.training.repositories.UserRepository;

/**
 * Concrete implementation of a {@link JwtTokenService}.
 */
@Service
@Transactional(readOnly = true)
public class JwtTokenServiceImpl implements JwtTokenService {

    /**
     * The {@link JwtTokenDecoder} used to transform a {@link String} into a {@link TokenData}.
     */
    private final JwtTokenDecoder jwtTokenDecoder;
    /**
     * The {@link JwtTokenEncoder} used to transform a {@link TokenData} into a {@link String}.
     */
    private final JwtTokenEncoder jwtTokenEncoder;
    /**
     * The {@link BlacklistedJwtTokenRepository} used to blacklist tokens and to check whether a
     * token was blacklisted.
     */
    private final BlacklistedJwtTokenRepository blacklistedJwtTokenRepository;
    /**
     * The {@link UserRepository} to search a {@link wolox.training.models.User} trying to issue a
     * token by its username.
     */
    private final UserRepository userRepository;
    /**
     * A flag indicating whether the authentication process must fail if a JWT is blacklisted. Will
     * discard the token in such case.
     */
    private final boolean failOnBlacklisted;


    /**
     * Constructor.
     *
     * @param jwtTokenDecoder The {@link JwtTokenDecoder} used to transform a {@link String} into a
     * {@link TokenData}.
     * @param jwtTokenEncoder The {@link JwtTokenEncoder} used to transform a {@link TokenData} into
     * a {@link String}.
     * @param blacklistedJwtTokenRepository The {@link BlacklistedJwtTokenRepository} used to
     * blacklist tokens and to check whether a token was blacklisted.
     * @param userRepository The {@link UserRepository} to search a {@link User} trying to issue a
     * @param failOnBlacklisted A flag indicating whether the authentication process must fail if a
     * JWT is blacklisted. Will discard the token if not valid.
     */
    @Autowired
    public JwtTokenServiceImpl(
        final JwtTokenDecoder jwtTokenDecoder,
        final JwtTokenEncoder jwtTokenEncoder,
        final BlacklistedJwtTokenRepository blacklistedJwtTokenRepository,
        final UserRepository userRepository,
        @Value("${authentication.jwt.fail_on_blacklisted}") final boolean failOnBlacklisted) {

        this.jwtTokenDecoder = jwtTokenDecoder;
        this.jwtTokenEncoder = jwtTokenEncoder;
        this.blacklistedJwtTokenRepository = blacklistedJwtTokenRepository;
        this.userRepository = userRepository;
        this.failOnBlacklisted = failOnBlacklisted;
    }

    @Override
    public RawTokenContainer issueToken(final String username, final String password) {
        return userRepository
            .getFirstByUsername(username)
            .filter(user -> user.passwordMatches(password))
            .map(JwtTokenServiceImpl::buildTokenDataForUser)
            .map(data -> new RawTokenContainer(data.getId(), jwtTokenEncoder.encode(data)))
            .orElseThrow(() -> new AuthenticationException("Passwords don't match"));
    }

    @Override
    public Optional<TokenData> decodeAndVerify(final String rawToken) {
        return jwtTokenDecoder.decode(rawToken)
            .filter(token -> {
                // Check if the token was blacklisted
                if (blacklistedJwtTokenRepository.existsById(token.getId())) {
                    // If yes, throw an AuthenticationException if the fail_on_blacklisted property
                    // is set. Otherwise, just filter the token (the Optional will be empty).
                    if (failOnBlacklisted) {
                        throw new AuthenticationException("Blacklisted token");
                    }
                    return false;
                }
                return true;
            });
    }

    @Override
    @Transactional
    public void blacklistToken(final String id) {
        if (blacklistedJwtTokenRepository.existsById(id)) {
            return;
        }
        blacklistedJwtTokenRepository.save(new BlacklistedJwtToken(id));
    }


    /**
     * Builds a {@link TokenData} for the given {@code user}.
     *
     * @param user The {@link User} for which the {@link TokenData} will be built.
     * @return The created {@link TokenData}.
     */
    private static TokenData buildTokenDataForUser(final User user) {
        return new TokenData(
            UUID.randomUUID().toString(),
            user.getUsername(),
            user.getRoles()
        );
    }
}
