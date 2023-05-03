package omg.simple.account.security.controller;

import lombok.AllArgsConstructor;
import omg.simple.account.security.model.security.AuthenticationRequest;
import omg.simple.account.security.model.security.AuthenticationResponse;
import omg.simple.account.security.model.security.RegisterRequest;
import omg.simple.account.security.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    AuthenticationService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
        @RequestBody RegisterRequest request
    ) {
      return ResponseEntity.ok(authService.register(request));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
        @RequestBody AuthenticationRequest request
    ) {
      return ResponseEntity.ok(authService.authenticate(request));
    }
}
