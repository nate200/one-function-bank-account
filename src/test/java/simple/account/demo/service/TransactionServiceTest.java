package simple.account.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import simple.account.demo.model.Transaction;
import simple.account.demo.model.TransactionStatus;
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
        Transaction expected = new Transaction(null, "THB", 1L, 2L, ZERO, TransactionStatus.DONE, "OKIE DOKIE");
        given(repo.save(any(Transaction.class))).willReturn(expected);

        var actual = service.saveTransaction(expected.toBuilder().transactionId(1L).build());

        assertThat(actual).isNotNull();
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(repo, times(1)).save(any(Transaction.class));
    }

    @Test
    void call_updateStatus_null_parameter(){
        assertThrows(
                NullPointerException.class,
                () -> service.updateStatus(null)
        );
        verify(repo, never()).updateStatus(any(TransactionStatus.class),any(String.class),anyLong());
    }
    @Test
    void updateStatus(){
        Transaction expected = new Transaction(1L, "THB", 1L, 2L, ZERO, TransactionStatus.DONE, "done and done");
        given(repo.updateStatus(any(TransactionStatus.class),any(String.class),anyLong())).willReturn(1);

        service.updateStatus(expected);

        verify(repo, times(1)).updateStatus(any(TransactionStatus.class),any(String.class),anyLong());
    }

}