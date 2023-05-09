package omg.simple.account.core.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import omg.simple.account.core.model.api.AuthenticationRequest;
import omg.simple.account.core.model.api.AuthenticationResponse;
import omg.simple.account.core.model.api.RegisterRequest;
import omg.simple.account.core.model.business.Account;
import omg.simple.account.core.service.AuthenticationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    AuthenticationService authService;

    @PostMapping("/register")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "create a new user with given information",
        content = @Content(examples = {@ExampleObject(value = "{\"firstname\" : \"firstname\",\"lastname\" : \"lastname\",\"email\" : \"a@a.com\",\"password\" : \"55555\"}")},
                schema = @Schema(implementation = RegisterRequest.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",description = "return a token of a new created user account",
            content = @Content(examples = {@ExampleObject(value = "{\"accessToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c\"}")},
                schema = @Schema(implementation = AuthenticationResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE))})
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }
    @PostMapping("/authenticate")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "create a new user with given information",
            content = @Content(examples = {@ExampleObject(value = "{\"email\" : \"a@a.com\",\"password\" : \"55555\"}")},
                    schema = @Schema(implementation = RegisterRequest.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "return a token of a new created user account",
                    content = @Content(examples = {@ExampleObject(value = "{\"accessToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c\"}")},
                            schema = @Schema(implementation = AuthenticationResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE))})
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
