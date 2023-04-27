package simple.account.demo.service.account;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import simple.account.demo.common.ExchangeRateApi;
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
    AccountServiceImpl service;

    @Test
    void save_one_account(){
        long accId = 50000L;
        Account expected = new Account(accId, ZERO, "THB");
        given(repo.save(any(Account.class))).willReturn(expected);

        var savedAccount = service.saveAccount(new Account(
            expected.getId(),
            expected.getTotal(),
            expected.getCurrency()
        ));

        assertThat(savedAccount).isNotNull();
        assertThat(savedAccount).usingRecursiveComparison().isEqualTo(expected);
        verify(repo, times(1)).save(any(Account.class));
    }
    @Test
    void saveAccount_fail_saving_duplicate_id(){
        long accId = 50000L;
        Account acc = new Account(accId, ZERO, "THB");
        given(repo.findById(anyLong())).willReturn(Optional.of(acc));
        /*force [... = accountRepo.findById(account.getId());] in service.saveAccount(...) to return Optional,
        which triggers the if(savedAcc.isPresent()) and throw the exception.
        skip db search, because this is mocking
        https://1kevinson.com/content/images/size/w1600/2022/08/Screenshot-2022-08-22-at-22.37.57.png*/

        IllegalArgumentException error = assertThrows(
            IllegalArgumentException.class,
            () -> service.saveAccount(
                new Account( acc.getId(), acc.getTotal(), acc.getCurrency()))
        );

        assertEquals("Account already exist with given Id:" + acc.getId(), error.getMessage());
        verify(repo, never()).save(any(Account.class));
        //verify(repo, times(1)).save(any(Account.class)); //use this and the test will fail, which is intended and correct
    }

    @Test
    void get_account() {
        long accId = 1L;
        var expected = new Account(accId, ZERO, "THB");
        given(repo.findById(anyLong())).willReturn(Optional.of(expected));

        var actual = service.getAccountById(accId);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(repo, times(1)).findById(accId);
        verifyNoMoreInteractions(repo);
    }
    @Test
    void get_account_not_exist(){
        long accId = Long.MAX_VALUE;
        given(repo.findById(accId)).willReturn(Optional.empty());
        //force repo.findById(accId) in service.getAccountById(...) to return Optional.empty() which triggers orElseThrow

        EntityNotFoundException error = assertThrows(
                EntityNotFoundException.class,
                () -> service.getAccountById(accId)
        );
        assertEquals("no account id:" + accId, error.getMessage());
    }

    @Test
    void get_currency_of_account_by_id(){
        long accId = 50000L;
        Account expectedAcc = new Account(accId, ZERO, "THB");
        given(repo.findById(accId)).willReturn(Optional.of(expectedAcc));

        String actualCurr = service.getAccountCurrency(accId);
        assertEquals(expectedAcc.getCurrency(), actualCurr);
    }
    @Test
    void get_currency_of_non_exist_account(){
        long accId = Long.MAX_VALUE;
        given(repo.findById(accId)).willReturn(Optional.empty());
        //force repo.findById(accId) in service.getAccountById(...) to return Optional.empty() which triggers orElseThrow

        EntityNotFoundException error = assertThrows(
                EntityNotFoundException.class,
                () -> service.getAccountCurrency(accId)
        );
        assertEquals("no account id:" + accId, error.getMessage());
    }

    @Test
    void changeTotal(){
        long accId = 50000L;
        given(repo.changeTotal(any(BigDecimal.class), anyLong())).willReturn(1);

        service.changeTotal(TEN, accId);

        verify(repo, times(1)).changeTotal(any(BigDecimal.class), anyLong());
    }
}