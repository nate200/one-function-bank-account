package omg.simple.account.core.exception.business;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BusinessLogicException extends RuntimeException{
    HttpStatus status;
    public BusinessLogicException(String msg){
        super(msg);
    }
}
