package wolox.training.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import wolox.training.exceptions.BookAlreadyOwnedException;

/**
 * A {@link ControllerAdvice} that acts as an exception handler.
 */
@ControllerAdvice
public class ExceptionHandlingController {

    /**
     * An exception handler for {@link BookAlreadyOwnedException} that converts the said exception
     * into a 409 Conflict {@link ResponseEntity}.
     *
     * @return A 409 Conflict {@link ResponseEntity}.
     */
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Data integrity violation")
    @ExceptionHandler(BookAlreadyOwnedException.class)
    public ResponseEntity bookAlreadyOwnedExceptionHandler() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Constraint violation")
    public ResponseEntity methodArgumentNotValidException() {
        return ResponseEntity.badRequest().build();
    }
}
