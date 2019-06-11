package wolox.training.exceptions;

import wolox.training.models.Book;
import wolox.training.models.User;

/**
 * A {@link RuntimeException} thrown when trying to add a {@link Book} to a {@link User} when he/she
 * already owns it.
 */
public class BookAlreadyOwnedException extends RuntimeException {

    /**
     * Default constructor. Sets {@code null} as message and cause.
     */
    public BookAlreadyOwnedException() {
        super();
    }

    /**
     * Constructor that sets a message.
     *
     * @param message The exception's message.
     */
    public BookAlreadyOwnedException(final String message) {
        super(message);
    }

    /**
     * Constructor that sets a cause (i.e another {@link Throwable} that causes this exception to be
     * thrown).
     *
     * @param cause The {@link Throwable} that caused this exception to be thrown.
     */
    public BookAlreadyOwnedException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructor that sets a message and a cause i.e another {@link Throwable} that causes this
     * exception to be thrown).
     *
     * @param message The exception's message.
     * @param cause The {@link Throwable} that caused this exception to be thrown.
     */
    public BookAlreadyOwnedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
