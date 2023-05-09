package omg.simple.account.core.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import omg.simple.account.core.exception.ApiError;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import omg.simple.account.core.model.business.Transaction;
import omg.simple.account.core.service.TransferManager;

@RestController
@AllArgsConstructor
public class TransactionController {
    TransferManager transferManager;

    @PostMapping("transfer-with-in-app")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Transaction object. Include only these given fields, other fields will be ignored",
        content = @Content(examples = {@ExampleObject(value = "{ \"currency\": \"CHF\", \"fromAcc\": 1, \"toAcc\": 2, \"amount\": 50.0 }")},
            schema = @Schema(implementation = Transaction.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "return a message if everything went ok",
            content = @Content(examples = {@ExampleObject(value = "Transfer successful")},
                schema = @Schema(implementation = String.class), mediaType = MediaType.TEXT_PLAIN_VALUE)),
        @ApiResponse(responseCode = "400", description = "Transaction request has field(s) with processable value such as null, wrong format, etc",
            content = @Content(examples = {@ExampleObject(value = "{\"status\":\"BAD_REQUEST\",\"message\":\"The exchange amount must be decimal and greater than 0\",\"timestamp\":\"May 1, 2023, 10:22:50 PM\"}")},
                schema = @Schema(implementation = ApiError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "422", description = "Transaction request is valid, but the api can't process the transaction. This can happen for example, when an account has invalid currency.",
            content = @Content(examples = {@ExampleObject(value = "{\"status\":\"UNPROCESSABLE_ENTITY\",\"message\":\"Account id: 1 has invalid currency\",\"timestamp\":\"May 1, 2023, 10:22:10 PM\"}")},
                schema = @Schema(implementation = ApiError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "500", description = "internal error",
            content = @Content(examples = {@ExampleObject(value = "{\"status\":\"INTERNAL_SERVER_ERROR\",\"message\":\"my bad ;(\",\"timestamp\":\"May 1, 2023, 10:19:31 PM\"}")},
                schema = @Schema(implementation = ApiError.class), mediaType = MediaType.APPLICATION_JSON_VALUE))})
    public ResponseEntity<String> transferToWithInApp(@RequestBody Transaction transaction) throws Exception{
        transferManager.transferWithInApp(transaction);
        return ResponseEntity.ok("Transfer successful");
    }
}
