package simple.account.demo.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import simple.account.demo.exception.ApiError;
import simple.account.demo.service.TransferManager;
import simple.account.demo.model.Account;
import simple.account.demo.model.Transaction;
import simple.account.demo.service.AccountService;

@RestController
@AllArgsConstructor
public class AccountController {
    AccountService accountService;

    @PostMapping("create-account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                description = "save and return string",
                content = @Content(schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "400",
                description = "input account has invalid field(s)",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "500",
                description = "internal error",
                content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<String> createAccount(@RequestBody Account account) {
        accountService.createAccountRequest(account);
        return ResponseEntity.ok("saved");
    }

    @GetMapping("getAccount/{accId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "return an account with a given account id:{accId}",
                    content = @Content(schema = @Schema(implementation = Account.class))),
            @ApiResponse(responseCode = "404",
                    description = "no account found with a given account id:{accId}",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500",
                    description = "internal error",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public Account getAccount(@PathVariable("accId") long accId) {
        return accountService.getAccountById(accId);
    }
}
