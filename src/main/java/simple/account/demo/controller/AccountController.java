package simple.account.demo.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import simple.account.demo.exception.ApiError;
import simple.account.demo.model.Account;
import simple.account.demo.service.AccountService;

@RestController
@AllArgsConstructor
public class AccountController {
    AccountService accountService;

    @PostMapping("create-account")
    @RequestBody(
        description = "account to create. Don't include an Id, because it will be ignored anyway",
        content = @Content(examples = {@ExampleObject(value = "{\"total\": 50.0, \"currency\": \"CHF\", \"email\": \"a@a.com\" }")},
                schema = @Schema(implementation = Account.class),
                mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                description = "save and return string",
                content = @Content(examples = {@ExampleObject(value = "saved")},
                        schema = @Schema(implementation = String.class),
                        mediaType = MediaType.TEXT_PLAIN_VALUE)),
        @ApiResponse(responseCode = "400",
                description = "input account has invalid field(s)",
                content = @Content(examples = {@ExampleObject(value = "{\"status\":\"BAD_REQUEST\",\"message\":\"total of the new account must not be null\",\"timestamp\":\"May 1, 2023, 10:07:39 PM\"}")},
                        schema = @Schema(implementation = ApiError.class),
                        mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "500",
                description = "internal error",
                content = @Content(examples = {@ExampleObject(value = "{\"status\":\"INTERNAL_SERVER_ERROR\",\"message\":\"my bad ;(\",\"timestamp\":\"May 1, 2023, 10:19:31 PM\"}")},
                        schema = @Schema(implementation = ApiError.class),
                        mediaType = MediaType.APPLICATION_JSON_VALUE))})
    public ResponseEntity<String> createAccount(@RequestBody Account account) {
        accountService.createAccountRequest(account);
        return ResponseEntity.ok("saved");
    }

    @GetMapping("getAccount/{accId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "return an account with a given account id:{accId}",
                    content = @Content(examples = {@ExampleObject(value = "{\"id\":1,\"total\":10.00,\"currency\":\"THB\",\"email\":\"a@a.com\"}")},
                            schema = @Schema(implementation = Account.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "404",
                    description = "no account found with a given account id:{accId}",
                    content = @Content(examples = {@ExampleObject(value = "{\"status\":\"NOT_FOUND\",\"message\":\"no account id:-1\",\"timestamp\":\"May 1, 2023, 10:15:52 PM\"}")},
                            schema = @Schema(implementation = ApiError.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500",
                    description = "internal error",
                    content = @Content(examples = {@ExampleObject(value = "{\"status\":\"INTERNAL_SERVER_ERROR\",\"message\":\"my bad ;(\",\"timestamp\":\"May 1, 2023, 10:19:31 PM\"}")},
                            schema = @Schema(implementation = ApiError.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))})
    public Account getAccount(@PathVariable("accId") long accId) {
        return accountService.getAccountById(accId);
    }
}
