package simple.account.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import simple.account.demo.model.Transaction;
import simple.account.demo.repository.TransactionRepository;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    TransactionRepository repo;
    @InjectMocks
    TransactionService service;

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