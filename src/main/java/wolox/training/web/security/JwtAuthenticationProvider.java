package wolox.training.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import wolox.training.exceptions.AuthenticationException;
import wolox.training.services.authentication.JwtTokenService;

/**
 * An {@link AuthenticationProvider} that can handle {@link PreAuthenticatedJwtToken}s, transforming
 * them into {@link UsernameAndGrantsAuthentication}s if the sent bearer tokens are valid.
 */
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    /**
     * The {@link JwtTokenService} used to decode and verify the sent bearer token.
     */
    private final JwtTokenService jwtTokenService;

    /**
     * A flag indicating whether a request with an invalid or blacklisted JWT can be interpreted as
     * an anonymous request. However, note that if the endpoint is secured, the authentication will
     * fail.
     */
    private final boolean allowAnonymous;

    /**
     * Constructor.
     *
     * @param jwtTokenService The {@link JwtTokenService} used to decode and verify the sent bearer
     * token.
     * @param allowAnonymous A flag indicating whether a request with an invalid or blacklisted JWT
     * can be interpreted as an anonymous request. However, note that if the endpoint is secured,
     * the authentication will fail.
     */
    @Autowired
    public JwtAuthenticationProvider(
        final JwtTokenService jwtTokenService,
        @Value("${authentication.jwt.allow_anonymous}") final boolean allowAnonymous) {
        this.jwtTokenService = jwtTokenService;
        this.allowAnonymous = allowAnonymous;
    }


    @Override
    public Authentication authenticate(final Authentication authentication) {
        Assert.notNull(authentication, "The authentication must not be null");
        Assert.isInstanceOf(PreAuthenticatedJwtToken.class, authentication,
            "The authentication must be a PreAuthenticatedJwtToken");

        return jwtTokenService
            .decodeAndVerify(((PreAuthenticatedJwtToken) authentication).getRawToken())
            .map(data -> new UsernameAndGrantsAuthentication(data.getUsername(), data.getGrants()))
            .map(UsernameAndGrantsAuthentication::authenticate)
            .map(auth -> (Authentication) auth)
            .orElseGet(() -> {
                if (allowAnonymous) {
                    return Constants.ANONYMOUS_AUTHENTICATION_TOKEN;
                }
                throw new AuthenticationException("Anonymous is not allowed");
            })
            ;
    }

    @Override
    public boolean supports(final Class<?> authenticationClass) {
        return ClassUtils.isAssignable(PreAuthenticatedJwtToken.class, authenticationClass);
    }
}
