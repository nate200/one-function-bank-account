package omg.simple.account.core.service;

import omg.simple.account.core.exception.business.BadRequestParameterException;
import omg.simple.account.core.model.api.AuthenticationRequest;
import omg.simple.account.core.model.api.RegisterRequest;
import omg.simple.account.core.model.business.Token;
import omg.simple.account.core.model.business.UserAccount;
import omg.simple.account.core.repository.TokenRepository;
import omg.simple.account.core.repository.UserAccountRepository;
import omg.simple.account.security.component.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock UserAccountRepository userRepo;
    @Mock TokenRepository tokenRepo;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtUtil jwtUtil;
    @Mock AuthenticationManager authenticationManager;

    @InjectMocks
    AuthenticationService service;

    @Test
    void register(){
        RegisterRequest registerRequest = RegisterRequest.builder().email("www@www.com").password("asda").build();

        given(userRepo.findByEmail(any(String.class))).willReturn(Optional.empty());
        given(passwordEncoder.encode(any(String.class))).willReturn("encrpytedPassword");
        given(userRepo.save(any(UserAccount.class))).willReturn(new UserAccount());
        given(jwtUtil.generateToken(any(UserAccount.class))).willReturn("token");
        given(tokenRepo.save(any(Token.class))).willReturn(new Token());

        service.register(registerRequest);

        verify(userRepo, times(1)).save(any(UserAccount.class));
        verify(tokenRepo, times(1)).save(any(Token.class));
    }
    @Test
    void register_error_dupEmail(){
        RegisterRequest registerRequest = RegisterRequest.builder().email("www@www.com").build();
        given(userRepo.findByEmail(any(String.class))).willReturn(Optional.of(new UserAccount()));

        assertThrows(
                BadRequestParameterException.class,
                () -> service.register(registerRequest)
        );

        verifyNoMoreInteractions(userRepo);
        verifyNoInteractions(tokenRepo);
    }

    @Test
    void authenticate_revokeExistingTokens(){
        AuthenticationRequest authReq = new AuthenticationRequest("email", "password");
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(null);
        given(userRepo.findByEmail(any(String.class))).willReturn(Optional.of(UserAccount.builder().id(1L).build()));
        given(jwtUtil.generateToken(any(UserAccount.class))).willReturn("token");
        given(tokenRepo.save(any(Token.class))).willReturn(new Token());
        given(tokenRepo.findAllValidByUserAccountId(anyLong())).willReturn(List.of(new Token()));
        given(tokenRepo.saveAll(anyList())).willReturn(null);

        service.authenticate(authReq);

        verify(tokenRepo, times(1)).save(any(Token.class));
        verify(tokenRepo, times(1)).saveAll(anyList());
    }

    @Test
    void authenticate_no_ExistingTokens_to_revoke(){
        AuthenticationRequest authReq = new AuthenticationRequest("email", "password");
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(null);
        given(userRepo.findByEmail(any(String.class))).willReturn(Optional.of(UserAccount.builder().id(1L).build()));
        given(jwtUtil.generateToken(any(UserAccount.class))).willReturn("token");
        given(tokenRepo.save(any(Token.class))).willReturn(new Token());
        given(tokenRepo.findAllValidByUserAccountId(anyLong())).willReturn(List.of());

        service.authenticate(authReq);

        verify(tokenRepo, times(1)).save(any(Token.class));
        verify(tokenRepo, never()).saveAll(anyList());
    }

    @Test
    void authenticate_user_not_found(){
        AuthenticationRequest authReq = new AuthenticationRequest("email----------------------------------------------------", "password");
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willThrow(UsernameNotFoundException.class);

        assertThrows(
                UsernameNotFoundException.class,
                () ->service.authenticate(authReq)
        );

        verifyNoInteractions(userRepo);
        verifyNoInteractions(tokenRepo);
    }
}