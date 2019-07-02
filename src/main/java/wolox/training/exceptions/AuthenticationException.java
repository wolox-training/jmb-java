package wolox.training.exceptions;

/**
 * A {@link RuntimeException} thrown when authentication fails.
 */
public class AuthenticationException extends RuntimeException {

    /**
     * Default constructor. Sets {@code null} as message and cause.
     */
    public AuthenticationException() {
        super();
    }

    /**
     * Constructor that sets a message.
     *
     * @param message The exception's message.
     */
    public AuthenticationException(final String message) {
        super(message);
    }

    /**
     * Constructor that sets a cause (i.e another {@link Throwable} that causes this exception to be
     * thrown).
     *
     * @param cause The {@link Throwable} that caused this exception to be thrown.
     */
    public AuthenticationException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructor that sets a message and a cause i.e another {@link Throwable} that causes this
     * exception to be thrown).
     *
     * @param message The exception's message.
     * @param cause The {@link Throwable} that caused this exception to be thrown.
     */
    public AuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
