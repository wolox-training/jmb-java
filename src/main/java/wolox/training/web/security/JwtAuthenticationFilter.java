package wolox.training.web.security;

import static wolox.training.web.security.Constants.ANONYMOUS_AUTHENTICATION_TOKEN;
import static wolox.training.web.security.Constants.AUTHENTICATION_HEADER;
import static wolox.training.web.security.Constants.AUTHENTICATION_SCHEME;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StringUtils;

/**
 * A {@link javax.servlet.Filter} that extracts a Bearer token from the {@link HttpServletRequest},
 * and then attempts authentication with it.
 */
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * Constructor.
     */
    public JwtAuthenticationFilter() {
        super("/**");
        this.setAuthenticationFailureHandler((req, resp, auth) -> {
            // Do nothing
        });
        this.setAuthenticationSuccessHandler((req, resp, auth) -> {
            // Do nothing
        });
    }


    @Override
    public Authentication attemptAuthentication(
        final HttpServletRequest request,
        final HttpServletResponse response) {

        return extractJwtToken(request)
            .map(PreAuthenticatedJwtToken::new)
            .map(jwtToken -> getAuthenticationManager().authenticate(jwtToken))
            .orElse(ANONYMOUS_AUTHENTICATION_TOKEN)
            ;
        // TODO: throw exception?
    }

    @Override
    protected void successfulAuthentication(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain chain,
        final Authentication authResult) throws IOException, ServletException {

        // First, process request by parent
        super.successfulAuthentication(request, response, chain, authResult);

        // Then, continue with normal flow
        chain.doFilter(request, response);
    }


    /**
     * Extracts a JWT token from the given {@link HttpServletRequest}.
     *
     * @param request The {@link HttpServletRequest} from where the token will be extracted.
     * @return An {@link Optional} containing the JWT if it exists in the {@code request}, or empty
     * otherwise.
     * @implNote This method searches for the {@link Constants#AUTHENTICATION_HEADER} header in the
     * given {@code request}, which should contain the token with the following format:
     * Bearer&lt;space&gt;&lt;token&gt;.
     */
    private static Optional<String> extractJwtToken(final HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTHENTICATION_HEADER))
            .filter(StringUtils::hasText)
            .map(header -> header.split(" "))
            .filter(splitted -> splitted.length == 2)
            .filter(splitted -> AUTHENTICATION_SCHEME.equals(splitted[0]))
            .map(splitted -> splitted[1])
            ;
    }
}
