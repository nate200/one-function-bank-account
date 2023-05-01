package simple.account.demo.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import simple.account.demo.exception.ApiError;
import simple.account.demo.model.Account;
import simple.account.demo.model.Transaction;
import simple.account.demo.service.TransferManager;

@RestController
@AllArgsConstructor
public class TransactionController {
    TransferManager transferManager;

    @PostMapping("transfer-with-in-app")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "return a message if everything went ok",
                    content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400",
                    description = "Transaction request has field(s) with processable value such as null, wrong format, etc",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422",
                    description = "Transaction request is valid, but the api can't process the transaction. This can happen for example, when an account has invalid currency.",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500",
                    description = "internal error",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<String> transferToWithInApp(@RequestBody Transaction transaction) throws Exception{
        transferManager.transferWithInApp(transaction);
        return ResponseEntity.ok("Transfer successful");
    }
}
