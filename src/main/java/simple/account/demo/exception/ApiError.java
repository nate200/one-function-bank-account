package simple.account.demo.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@ToString
@JsonInclude(Include.NON_EMPTY)
//@Getter//json output field
public class ApiError {//https://www.toptal.com/java/spring-boot-rest-api-error-handling
    private HttpStatus status;
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;

    private ApiError() {
        timestamp = LocalDateTime.now();
    }
    public ApiError(HttpStatus status, String message) {
        this();
        this.status = status;
        this.message = message;
    }
}
