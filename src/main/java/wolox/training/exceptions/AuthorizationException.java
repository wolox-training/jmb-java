package wolox.training.exceptions;

/**
 * A {@link RuntimeException} thrown when an unauthorized action is tried to be performed.
 */
public class AuthorizationException extends RuntimeException {

    /**
     * Default constructor. Sets {@code null} as message and cause.
     */
    public AuthorizationException() {
        super();
    }

    /**
     * Constructor that sets a message.
     *
     * @param message The exception's message.
     */
    public AuthorizationException(final String message) {
        super(message);
    }

    /**
     * Constructor that sets a cause (i.e another {@link Throwable} that causes this exception to be
     * thrown).
     *
     * @param cause The {@link Throwable} that caused this exception to be thrown.
     */
    public AuthorizationException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructor that sets a message and a cause i.e another {@link Throwable} that causes this
     * exception to be thrown).
     *
     * @param message The exception's message.
     * @param cause The {@link Throwable} that caused this exception to be thrown.
     */
    public AuthorizationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
