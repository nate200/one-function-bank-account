package omg.simple.account.core.exception.business;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class BadRequestParameterException extends BusinessLogicException{
    public BadRequestParameterException(String msg){
        super(msg);
        this.status = BAD_REQUEST;
    }
}
