package omg.simple.account.core.service;

import omg.simple.account.core.model.business.UserAccount;
import omg.simple.account.core.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {
    @Mock UserAccountRepository userAccRepo;
    @InjectMocks UserAccountService service;

    @Test
    void getAccountByEmail_UsernameNotFound(){
        given(userAccRepo.findByEmail(any(String.class))).willReturn(Optional.empty());

        assertThrows(
            UsernameNotFoundException.class,
            () -> service.getAccountByEmail("aa")
        );
    }

    @Test
    void getAccountByEmail(){
        given(userAccRepo.findByEmail(any(String.class))).willReturn(Optional.of(new UserAccount()));

        UserAccount userAcc = service.getAccountByEmail("aa");
        assertNotNull(userAcc);
    }
}