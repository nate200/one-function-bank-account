package simple.account.demo.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import simple.account.demo.exception.ApiError;
import simple.account.demo.exception.business.BusinessLogicException;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleError(Exception ex) {
        if(ex instanceof BusinessLogicException bex){
            ApiError er = new ApiError(bex.getStatus(), bex.getMessage());
            return ResponseEntity.status(bex.getStatus()).body(er);
        }
        else {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "my bad ;("));
        }
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(Exception ex) {
        ApiError er = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(404).body(er);
    }
}
