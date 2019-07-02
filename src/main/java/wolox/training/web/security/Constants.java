package wolox.training.web.security;

import java.util.List;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Class containing constants to be used by the authentication module.
 */
/* package */ class Constants {


    /**
     * Private constructor to avoid instantiation.
     */
    private Constants() {
    }


    /**
     * Anonymous.
     */
    private static final String ANONYMOUS = "ANONYMOUS";

    /**
     * Indicates which HTTP header includes the authentication credentials.
     */
    /* package */ static final String AUTHENTICATION_HEADER = "Authorization";

    /**
     * Indicates the authentication scheme supported by the system.
     */
    /* package */ static final String AUTHENTICATION_SCHEME = "Bearer";

    /**
     * An {@link AnonymousAuthenticationToken} used when authentication fails.
     */
    /* package */ static final AnonymousAuthenticationToken ANONYMOUS_AUTHENTICATION_TOKEN =
        new AnonymousAuthenticationToken(
            ANONYMOUS,
            ANONYMOUS,
            List.of(new SimpleGrantedAuthority(ANONYMOUS))
        );
}
