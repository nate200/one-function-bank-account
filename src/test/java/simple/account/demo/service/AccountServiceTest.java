package simple.account.demo.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import simple.account.demo.model.Account;
import simple.account.demo.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static java.math.BigDecimal.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/*
use
https://www.springcloud.io/post/2022-09/spring-boot-micro-service-test/#gsc.tab=0
https://medium.com/backend-habit/integrate-junit-and-mockito-unit-testing-for-service-layer-a0a5a811c58a
or
https://www.javaguides.net/2022/03/spring-boot-unit-testing-service-layer.html
or
https://1kevinson.com/testing-service-spring-boot/
??????????????????
* */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    AccountRepository repo;
    @InjectMocks
    AccountService service;

    final long DEFAULT_ACCID = 1L;
    final Account DEFAULT_ACCOUNT = new Account(null, ZERO, "THB", "a@a.com");

    @Test
    void saveAccount(){
        given(repo.save(any(Account.class))).willReturn(DEFAULT_ACCOUNT);

        service.createAccount(DEFAULT_ACCOUNT);

        verify(repo, times(1)).save(any(Account.class));
    }
    @Test
    void saveAccount_null(){
        assertThrows(
                NullPointerException.class,
                () -> service.createAccount(null)
        );
        verify(repo, never()).save(any(Account.class));
    }
    @Test
    void saveAccount_already_exist(){
        Account savedAcc = DEFAULT_ACCOUNT.toBuilder().id(DEFAULT_ACCID).build();
        given(repo.findByEmail(any(String.class))).willReturn(Optional.of(savedAcc));

        assertThrows(
            IllegalArgumentException.class,
            () -> service.createAccount(DEFAULT_ACCOUNT)
        );

        verify(repo, never()).save(any(Account.class));
    }


    @Test
    void getAccountById() {
        given(repo.findById(anyLong())).willReturn(Optional.of(DEFAULT_ACCOUNT));

        service.getAccountById(DEFAULT_ACCID);

        verify(repo, times(1)).findById(anyLong());
    }
    @Test
    void getAccountById_not_exist(){
        given(repo.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> service.getAccountById(DEFAULT_ACCID)
        );
    }


    @Test
    void getAccountCurrency(){
        Account expectedAcc = DEFAULT_ACCOUNT.toBuilder().id(DEFAULT_ACCID).build();
        given(repo.findById(expectedAcc.getId())).willReturn(Optional.of(expectedAcc));

        String actualCurr = service.getAccountRawCurrency(expectedAcc.getId());
        assertEquals(expectedAcc.getCurrency(), actualCurr);
    }
    @Test
    void getAccountCurrency_non_exist_account(){
        given(repo.findById(DEFAULT_ACCID)).willReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> service.getAccountRawCurrency(DEFAULT_ACCID)
        );
    }


    @Test
    void changeTotal(){
        given(repo.changeTotal(any(BigDecimal.class), anyLong())).willReturn(1);

        service.changeTotal(TEN, DEFAULT_ACCID);

        verify(repo, times(1)).changeTotal(any(BigDecimal.class), anyLong());
    }
    @Test
    void changeTotal_null_amount(){
        assertThrows(
                NullPointerException.class,
                () -> service.changeTotal(null, DEFAULT_ACCID)
        );
        verify(repo, never()).changeTotal(any(BigDecimal.class), anyLong());
    }
    @Test
    void changeTotal_non_exist_account(){
        given(repo.changeTotal(any(BigDecimal.class), anyLong())).willReturn(0);

        assertThrows(
                EntityNotFoundException.class,
                () -> service.changeTotal(TEN, DEFAULT_ACCID)
        );
    }
}