package omg.simple.account.core.exception.business;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

public class UnprocessableContentException extends BusinessLogicException {
    public UnprocessableContentException(String msg){
        super(msg);
        this.status = UNPROCESSABLE_ENTITY;
    }
}
