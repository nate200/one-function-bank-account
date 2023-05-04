package omg.simple.account.core.service;

import lombok.AllArgsConstructor;
import omg.simple.account.core.model.api.AuthenticationRequest;
import omg.simple.account.core.model.api.AuthenticationResponse;
import omg.simple.account.core.model.api.RegisterRequest;
import omg.simple.account.core.model.business.Token;
import omg.simple.account.core.model.business.UserAccount;
import omg.simple.account.core.model.constant.Role;
import omg.simple.account.core.model.constant.TokenType;
import omg.simple.account.core.repository.TokenRepository;
import omg.simple.account.core.repository.UserAccountRepository;
import omg.simple.account.security.component.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserAccountRepository userRepo;
    private final TokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = UserAccount.builder()
                .fname(request.getFirstname())
                .lname(request.getLastname())
                .email(request.getEmail())
                .passw(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER).build();
        var savedUser = userRepo.save(user);

        var jwtToken = jwtUtil.generateToken(user);
        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepo.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtUtil.generateToken(user);

        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    private void saveUserToken(UserAccount user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false).build();
        System.err.println(token);
        tokenRepo.save(token);
    }

    private void revokeAllUserTokens(UserAccount user) {
        var validUserTokens = tokenRepo.findAllValidByUserAccountId(user.getId());

        if (validUserTokens.isEmpty())
            return;

        for(var token : validUserTokens){
            token.setExpired(true);
            token.setRevoked(true);
        }

        tokenRepo.saveAll(validUserTokens);
    }
}
