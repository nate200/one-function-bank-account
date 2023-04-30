package simple.account.demo.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import simple.account.demo.util.ExchangeRateApi;
import simple.account.demo.model.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.stream.Stream;

import static java.math.BigDecimal.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferManagerTest {
    @Mock ExchangeRateApi exchangeApi;
    @Mock AccountService accountService;
    @Mock TransactionService transactionService;
    @InjectMocks TransferManager transferManager;

    static final Transaction DEFAULT_TRANSACTION = new Transaction(null,"USD", 1L, 2L, TEN, null, null);

    @Test
    void transferToWithInApp_success() throws Exception {
        given(accountService.getAccountCurrency(anyLong())).willReturn("CHF");
        given(exchangeApi.convert(any(Currency.class), any(Currency.class), any(BigDecimal.class))).willReturn(TEN);
        doNothing().when(accountService).changeTotal(any(BigDecimal.class), anyLong());

        transferManager.transferWithInApp(DEFAULT_TRANSACTION);

        verify(transactionService, times(1)).saveTransaction(any(Transaction.class));
        verify(accountService, times(2)).changeTotal(any(BigDecimal.class), anyLong());
        verify(transactionService, times(1)).updateStatus(any(Transaction.class));
    }

    @Test
    void transferToWithInApp_null(){
        assertThrows(
            NullPointerException.class,
            () -> transferManager.transferWithInApp(null)
        );
        verifyNoInteractions(transactionService);
        verifyNoInteractions(accountService);
        verifyNoInteractions(exchangeApi);
    }

    @ParameterizedTest
    @MethodSource("badTransactions")
    void validateTransaction(Transaction badTransaction) {
        assertThrows(
                Exception.class,
                () -> transferManager.transferWithInApp(badTransaction)
        );
        verifyNoInteractions(accountService);
        verifyNoInteractions(exchangeApi);
        verify(transactionService, times(1)).updateStatus(any(Transaction.class));
    }

    @Test
    void account_with_invalid_currency() throws Exception {
        given(accountService.getAccountCurrency(anyLong())).willReturn("LOLOLOLOLOL");

        assertThrows(
                Exception.class,
                () -> transferManager.transferWithInApp(DEFAULT_TRANSACTION)
        );

        verify(exchangeApi, times(1)).convert(any(Currency.class), any(Currency.class), any(BigDecimal.class));
        verify(accountService, never()).changeTotal(any(BigDecimal.class), anyLong());
        verify(transactionService, times(1)).updateStatus(any(Transaction.class));
    }

    @Test
    void exchangeApi_io_problem() throws Exception {
        given(exchangeApi.convert(any(Currency.class), any(Currency.class), any(BigDecimal.class))).willThrow(new IOException());

        assertThrows(
                Exception.class,
                () -> transferManager.transferWithInApp(DEFAULT_TRANSACTION)
        );

        verify(accountService, never()).changeTotal(any(BigDecimal.class), anyLong());
        verify(transactionService, times(1)).updateStatus(any(Transaction.class));
    }

    @Test
    void non_exist_account() {
        given(accountService.getAccountCurrency(anyLong())).willThrow(new EntityNotFoundException());

        assertThrows(
                Exception.class,
                () -> transferManager.transferWithInApp(DEFAULT_TRANSACTION)
        );

        verify(accountService, never()).changeTotal(any(BigDecimal.class), anyLong());
        verify(transactionService, times(1)).updateStatus(any(Transaction.class));
    }

    static Stream<Transaction> badTransactions(){
        return Stream.of(
                DEFAULT_TRANSACTION.toBuilder().currency(null).build(),
                DEFAULT_TRANSACTION.toBuilder().amount(null).build(),
                DEFAULT_TRANSACTION.toBuilder().fromAcc(DEFAULT_TRANSACTION.getToAcc()).build(),
                DEFAULT_TRANSACTION.toBuilder().amount(ZERO).build()
        );
    }
}
