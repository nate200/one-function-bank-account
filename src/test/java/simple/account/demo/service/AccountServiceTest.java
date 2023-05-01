package simple.account.demo.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import simple.account.demo.exception.BadRequestParameterException;
import simple.account.demo.model.Account;
import simple.account.demo.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import static java.math.BigDecimal.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    AccountRepository repo;
    @InjectMocks
    AccountService service;

    final long DEFAULT_ACCID = 1L;
    final Account DEFAULT_ACCOUNT = new Account(null, ZERO, "THB", "a@a.com");

    @ParameterizedTest
    @MethodSource("validAccounts")
    void saveAccount(Account acc){
        given(repo.save(any(Account.class))).willReturn(acc);

        service.createAccountRequest(acc);

        verify(repo, times(1)).save(any(Account.class));
    }
    @Test
    void saveAccount_null(){
        assertThrows(
                NullPointerException.class,
                () -> service.createAccountRequest(null)
        );
        verify(repo, never()).save(any(Account.class));
    }
    @ParameterizedTest
    @MethodSource("AccountsInvalidFieldExceptEmail")
    void saveAccount_bad_field_except_email(Account invalidAcc){
        assertThrows(
            BadRequestParameterException.class,
            () -> service.createAccountRequest(invalidAcc)
        );
        verify(repo, never()).save(any(Account.class));
    }
    @ParameterizedTest
    @MethodSource("AccountsInvalidEmail")
    void saveAccount_invalid_email(Account invalidAcc){
        assertThrows(
                BadRequestParameterException.class,
                () -> service.createAccountRequest(invalidAcc)
        );

        verify(repo, never()).save(any(Account.class));
    }
    @Test
    void saveAccount_duplicate_email(){
        Account exisitingAcc = DEFAULT_ACCOUNT.toBuilder().id(1L).build();
        given(repo.findByEmail(any(String.class))).willReturn(Optional.of(exisitingAcc));

        assertThrows(
                BadRequestParameterException.class,
                () -> service.createAccountRequest(DEFAULT_ACCOUNT)
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

    static Stream<Account> validAccounts(){
        return Stream.of(
                Account.builder().total(ZERO).currency("THB").email("a@a.com").build(),
                Account.builder().total(TEN.negate()).currency("THB").email("a@a.com").build(),
                Account.builder().total(TEN).currency("THB").email("a@a.com").build(),
                Account.builder().total(TEN).currency("     THB").email("a@a.com").build(),
                Account.builder().total(TEN).currency("THB       ").email("a@a.com").build(),
                Account.builder().total(TEN).currency("     THB     ").email("a@a.com").build(),
                Account.builder().total(TEN).currency("THB").email("     a@a.com").build(),
                Account.builder().total(TEN).currency("THB").email("a@a.com     ").build(),
                Account.builder().total(TEN).currency("THB").email("      a@a.com     ").build()
        );
    }
    static Stream<Account> AccountsInvalidFieldExceptEmail(){
        return Stream.of(
                Account.builder().total(null).currency("THB").email("a@a.com").build(),
                Account.builder().total(TEN).currency(null).email("a@a.com").build()
        );
    }
    static Stream<Account> AccountsInvalidEmail(){
        return Stream.of(
                Account.builder().total(TEN).currency("THB").email(null).build(),
                Account.builder().total(TEN).currency("THB").email(" aaa   ").build(),
                Account.builder().total(TEN).currency("THB").email(" a aa@.com   ").build(),
                Account.builder().total(BigDecimal.valueOf(500.0)).currency("THB   ").email("         th b@c1.com   ").build()
        );
    }
}