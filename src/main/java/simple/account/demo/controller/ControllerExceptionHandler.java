package simple.account.demo.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import simple.account.demo.exception.BadRequestParameterException;
import simple.account.demo.exception.BusinessLogicException;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleErrorBeforeSending(Exception ex) {
        if(ex instanceof BadRequestParameterException)//Patterns in switch are not supported at language level '17'
            return ResponseEntity.badRequest().body(ex.getMessage());
        else
            return ResponseEntity.internalServerError().body("oopsie");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleNotFound(Exception ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}
