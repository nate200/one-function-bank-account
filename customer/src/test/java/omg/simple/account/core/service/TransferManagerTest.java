package omg.simple.account.core.service;

import jakarta.persistence.EntityNotFoundException;
import omg.simple.account.core.exception.business.BadRequestParameterException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import omg.simple.account.core.util.ExchangeRateApi;
import omg.simple.account.core.model.business.Transaction;

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

    static final Transaction DEFAULT_TRANSACTION = new Transaction(null,"USD", 1L, 2L, TEN, null, null, null);

    @Test
    void transferToWithInApp_success() throws Exception {
        given(accountService.getAccountRawCurrency(anyLong())).willReturn("CHF");
        given(exchangeApi.convert(any(Currency.class), any(Currency.class), any(BigDecimal.class))).willReturn(TEN);
        doNothing().when(accountService).changeTotal(any(BigDecimal.class), anyLong());

        transferManager.transferWithInApp(DEFAULT_TRANSACTION);

        verify(transactionService, times(1)).saveNewTransaction(any(Transaction.class));
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
                BadRequestParameterException.class,
                () -> transferManager.transferWithInApp(badTransaction)
        );
        verifyNoInteractions(accountService);
        verifyNoInteractions(exchangeApi);
        verify(transactionService, times(1)).updateStatus(any(Transaction.class));
    }

    @Test
    void account_with_invalid_currency() throws Exception {
        given(accountService.getAccountRawCurrency(DEFAULT_TRANSACTION.getFromAcc())).willReturn("LOLOLOLOLOL");

        assertThrows(
                Exception.class,
                () -> transferManager.transferWithInApp(DEFAULT_TRANSACTION)
        );

        verifyNoInteractions(exchangeApi);
        verify(accountService, never()).changeTotal(any(BigDecimal.class), anyLong());
        verify(transactionService, times(1)).updateStatus(any(Transaction.class));
    }

    @Test
    void exchangeApi_io_problem() throws Exception {
        given(accountService.getAccountRawCurrency(DEFAULT_TRANSACTION.getFromAcc())).willReturn("CHF");
        given(accountService.getAccountRawCurrency(DEFAULT_TRANSACTION.getToAcc())).willReturn("THB");
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
        given(accountService.getAccountRawCurrency(anyLong())).willThrow(new EntityNotFoundException());

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
                DEFAULT_TRANSACTION.toBuilder().currency("  TH B").build(),
                DEFAULT_TRANSACTION.toBuilder().currency("").build(),
                DEFAULT_TRANSACTION.toBuilder().currency("    ").build(),
                DEFAULT_TRANSACTION.toBuilder().amount(null).build(),
                DEFAULT_TRANSACTION.toBuilder().amount(ZERO).build(),
                DEFAULT_TRANSACTION.toBuilder().amount(TEN.negate()).build(),
                DEFAULT_TRANSACTION.toBuilder().fromAcc(DEFAULT_TRANSACTION.getToAcc()).build()
        );
    }
}
