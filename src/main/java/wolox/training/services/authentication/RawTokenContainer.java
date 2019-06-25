package wolox.training.services.authentication;


import lombok.Getter;

/**
 * Object that encapsulates an encoded JWT, together with its id.
 */
@Getter
public final class RawTokenContainer {

    /**
     * The token's id.
     */
    private final String id;
    /**
     * The token's owner username.
     */
    private final String rawToken;


    /**
     * @param id The token's id.
     * @param rawToken The token (in encoded format)
     */
    RawTokenContainer(final String id, final String rawToken) {
        this.id = id;
        this.rawToken = rawToken;
    }
}
