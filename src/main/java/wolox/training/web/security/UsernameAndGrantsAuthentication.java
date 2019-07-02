package wolox.training.web.security;

import java.util.Collection;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

/**
 * An {@link org.springframework.security.core.Authentication} that stores a username as the
 * principal.
 */
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(callSuper = true, exclude = "username")
class UsernameAndGrantsAuthentication extends AbstractAuthenticationToken {

    /**
     * The username of the user to which this JWT credentials belong.
     */
    private final String username;


    /**
     * Constructor.
     *
     * @param username The username of the user to which this JWT credentials belong.
     * @param grants The grants given to the user when using this credential instance.
     */
    /* package */ UsernameAndGrantsAuthentication(final String username,
        final Collection<String> grants) {
        super(grants.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
        this.username = username;
    }


    /**
     * Authenticates this token. This is a convenient method for {@code setAuthenticated(true)}.
     *
     * @return {@code this} (for method chaining).
     * @see org.springframework.security.core.Authentication#setAuthenticated(boolean)
     */
    /* package */ UsernameAndGrantsAuthentication authenticate() {
        this.setAuthenticated(true);
        return this;
    }


    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        Assert.state(isAuthenticated(), "Not yet authenticated");
        return username;
    }

    @Override
    public void setAuthenticated(final boolean authenticated) {
        Assert.state(authenticated || !isAuthenticated(), "Can't undo authentication");
        super.setAuthenticated(authenticated);
    }
}
