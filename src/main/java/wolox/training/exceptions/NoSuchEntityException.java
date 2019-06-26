package wolox.training.exceptions;

/**
 * A {@link RuntimeException} thrown when trying to access an entity that does not exist.
 */
public class NoSuchEntityException extends RuntimeException {

    /**
     * Default constructor. Sets {@code null} as message and cause.
     */
    public NoSuchEntityException() {
        super();
    }

    /**
     * Constructor that sets a message.
     *
     * @param message The exception's message.
     */
    public NoSuchEntityException(final String message) {
        super(message);
    }

    /**
     * Constructor that sets a cause (i.e another {@link Throwable} that causes this exception to be
     * thrown).
     *
     * @param cause The {@link Throwable} that caused this exception to be thrown.
     */
    public NoSuchEntityException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructor that sets a message and a cause i.e another {@link Throwable} that causes this
     * exception to be thrown).
     *
     * @param message The exception's message.
     * @param cause The {@link Throwable} that caused this exception to be thrown.
     */
    public NoSuchEntityException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
