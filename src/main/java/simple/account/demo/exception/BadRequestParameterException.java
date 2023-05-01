package simple.account.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestParameterException extends RuntimeException implements BusinessLogicException{
    public BadRequestParameterException(String msg){
        super(msg);
    }
}
