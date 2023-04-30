package simple.account.demo.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import simple.account.demo.model.Account;
import simple.account.demo.model.Transaction;
import simple.account.demo.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static java.math.BigDecimal.*;

import static org.assertj.core.api.Assertions.*;
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

    final long DEFAULT_ID = 1L;

    @Test
    void saveAccount(){
        Account expected = new Account(null, ZERO, "THB");
        given(repo.save(any(Account.class))).willReturn(expected);

        service.saveAccount(expected);

        verify(repo, times(1)).save(any(Account.class));
    }
    @Test
    void saveAccount_null(){
        assertThrows(
                NullPointerException.class,
                () -> service.saveAccount(null)
        );
        verify(repo, never()).save(any(Account.class));
    }
    @Test
    void saveAccount_already_exist(){
        Account acc = new Account(1L, ZERO, "THB");
        given(repo.findById(anyLong())).willReturn(Optional.of(acc));
        /*force [... = accountRepo.findById(account.getId());] in service.saveAccount(...) to return Optional,
        which triggers the if(savedAcc.isPresent()) and throw the exception.
        skip db search, because this is mocking
        https://1kevinson.com/content/images/size/w1600/2022/08/Screenshot-2022-08-22-at-22.37.57.png*/

        IllegalArgumentException error = assertThrows(
            IllegalArgumentException.class,
            () -> service.saveAccount(acc)
        );

        assertEquals("Account already exist with given Id:" + acc.getId(), error.getMessage());
        verify(repo, never()).save(any(Account.class));
    }


    @Test
    void getAccountById() {
        var acc = new Account(DEFAULT_ID, ZERO, "THB");
        given(repo.findById(DEFAULT_ID)).willReturn(Optional.of(acc));

        service.getAccountById(DEFAULT_ID);

        verify(repo, times(1)).findById(DEFAULT_ID);
    }
    @Test
    void getAccountById_not_exist(){
        given(repo.findById(DEFAULT_ID)).willReturn(Optional.empty());
        //force repo.findById(accId) in service.getAccountById(...) to return Optional.empty() which triggers orElseThrow

        EntityNotFoundException error = assertThrows(
                EntityNotFoundException.class,
                () -> service.getAccountById(DEFAULT_ID)
        );
        assertEquals("no account id:" + DEFAULT_ID, error.getMessage());
    }


    @Test
    void getAccountCurrency(){
        Account expectedAcc = new Account(DEFAULT_ID, ZERO, "THB");
        given(repo.findById(DEFAULT_ID)).willReturn(Optional.of(expectedAcc));

        String actualCurr = service.getAccountCurrency(DEFAULT_ID);
        assertEquals(expectedAcc.getCurrency(), actualCurr);
    }
    @Test
    void getAccountCurrency_non_exist_account(){
        given(repo.findById(DEFAULT_ID)).willReturn(Optional.empty());

        EntityNotFoundException error = assertThrows(
                EntityNotFoundException.class,
                () -> service.getAccountCurrency(DEFAULT_ID)
        );
        assertEquals("no account id:" + DEFAULT_ID, error.getMessage());
    }


    @Test
    void changeTotal(){
        given(repo.changeTotal(any(BigDecimal.class), anyLong())).willReturn(1);

        service.changeTotal(TEN, DEFAULT_ID);

        verify(repo, times(1)).changeTotal(any(BigDecimal.class), anyLong());
    }
    @Test
    void changeTotal_null_amount(){
        assertThrows(
                NullPointerException.class,
                () -> service.changeTotal(null, DEFAULT_ID)
        );
        verify(repo, never()).changeTotal(any(BigDecimal.class), anyLong());
    }
    @Test
    void changeTotal_non_exist_account(){
        given(repo.changeTotal(any(BigDecimal.class), anyLong())).willReturn(0);

        EntityNotFoundException error = assertThrows(
                EntityNotFoundException.class,
                () -> service.changeTotal(TEN, DEFAULT_ID)
        );
        assertEquals("no account id:" + DEFAULT_ID, error.getMessage());
    }
}