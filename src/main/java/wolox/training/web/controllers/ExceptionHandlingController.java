package wolox.training.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import wolox.training.exceptions.AuthenticationException;
import wolox.training.exceptions.AuthorizationException;
import wolox.training.exceptions.BookAlreadyOwnedException;
import wolox.training.exceptions.NoSuchEntityException;

/**
 * A {@link ControllerAdvice} that acts as an exception handler.
 */
@ControllerAdvice
public class ExceptionHandlingController {

    /**
     * An exception handler for {@link BookAlreadyOwnedException} that converts the said exception
     * into a 409 Conflict.
     */
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Data integrity violation")
    @ExceptionHandler(BookAlreadyOwnedException.class)
    public void bookAlreadyOwnedExceptionHandler() {
        // Handled by annotations
    }

    /**
     * An exception handler for {@link MethodArgumentNotValidException} that converts the said
     * exception into a 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Constraint Violation")
    public void methodArgumentNotValidException() {
        // Handled by annotations
    }

    /**
     * An exception handler for {@link NoSuchEntityException} that converts the said exception into
     * a 404 Not Found.
     */
    @ExceptionHandler(NoSuchEntityException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Not Found")
    public void noSuchEntityExceptionHandler() {
        // Handled by annotations
    }

    /**
     * An exception handler for {@link AuthenticationException} that converts the said exception
     * into a 401 Unauthorized.
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Unauthorized")
    public void authenticationExceptionHandler() {
        // Handled by annotations
    }

    /**
     * An exception handler for {@link AuthorizationException} that converts the said exception into
     * a 403 Forbidden.
     */
    @ExceptionHandler(AuthorizationException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Forbidden")
    public void authorizationExceptionHandler() {
        // Handled by annotations
    }
}
