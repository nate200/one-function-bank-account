package simple.account.demo.service.account;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.boot.test.context.SpringBootTest;
import simple.account.demo.common.ExchangeRateApi;
import simple.account.demo.model.Account;
import simple.account.demo.model.Transaction;
import simple.account.demo.repository.AccountRepository;
import simple.account.demo.repository.TransactionRepository;
import simple.account.demo.service.transaction.TransactionService;
import simple.account.demo.service.transaction.TransactionServiceImpl;

import java.math.BigDecimal;
import java.util.Optional;

import static java.math.BigDecimal.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    TransactionRepository repo;
    @InjectMocks
    TransactionServiceImpl service;

    @Test
    void saveTransaction_null(){
        assertThrows(
            NullPointerException.class,
            () -> service.saveTransaction(null)
        );
        verify(repo, never()).save(any(Transaction.class));
    }
    @Test
    void saveTransaction(){
        long transactionId = 1;
        Transaction expected = new Transaction(transactionId, "THB", 1L, 2L, ZERO, "done", "OKIE DOKIE");
        given(repo.save(any(Transaction.class))).willReturn(expected);

        var actual = service.saveTransaction(expected.toBuilder().build());

        assertThat(actual).isNotNull();
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(repo, times(1)).save(any(Transaction.class));
    }
}