package wolox.training.web.security;

import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * A {@link PreAuthenticatedAuthenticationToken} that stores a raw JWT token.
 */
/* package */ class PreAuthenticatedJwtToken extends PreAuthenticatedAuthenticationToken {

    /**
     * Constructor.
     *
     * @param rawToken The Raw JWT token.
     */
    /* package */  PreAuthenticatedJwtToken(final String rawToken) {
        super(rawToken, ""); // The principal is the token.
    }


    /**
     * @return The Raw JWT token.
     */
    String getRawToken() {
        return (String) getPrincipal();
    }


    @Override
    public final void setAuthenticated(final boolean authenticated) {
        throw new RuntimeException("This token cannot be authenticated");
    }

    @Override
    public final void setDetails(final Object details) {
        throw new RuntimeException("This tokens cannot store details");
    }
}
