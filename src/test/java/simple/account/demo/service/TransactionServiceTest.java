package simple.account.demo.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import simple.account.demo.model.Transaction;
import simple.account.demo.model.TransactionStatus;
import simple.account.demo.repository.TransactionRepository;

import java.util.Optional;

import static java.math.BigDecimal.TEN;
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

    final Transaction DEFAULT_TRANSACTION = new Transaction(null, "THB", 1L, 2L, TEN, null,null);

    @Test
    void saveTransaction(){
        given(repo.save(any(Transaction.class))).willReturn(DEFAULT_TRANSACTION);

        service.saveTransaction(DEFAULT_TRANSACTION);

        verify(repo, times(1)).save(any(Transaction.class));
    }
    @Test
    void saveTransaction_null(){
        assertThrows(
                NullPointerException.class,
                () -> service.saveTransaction(null)
        );
        verify(repo, never()).save(any(Transaction.class));
    }
    @Test
    void saveTransaction_already_exist(){
        Transaction existingTran = DEFAULT_TRANSACTION.toBuilder().transactionId(1L).build();
        given(repo.findById(anyLong())).willReturn(Optional.of(existingTran));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.saveTransaction(existingTran)
        );

        verify(repo, never()).save(any(Transaction.class));
    }


    @Test
    void updateStatus(){
        given(repo.updateStatus(any(TransactionStatus.class),any(String.class),anyLong())).willReturn(1);

        service.updateStatus(DEFAULT_TRANSACTION.toBuilder().transactionId(1L).build());

        verify(repo, times(1)).updateStatus(any(TransactionStatus.class),any(String.class),anyLong());
    }
    @Test
    void updateStatus_null(){
        assertThrows(
                NullPointerException.class,
                () -> service.updateStatus(null)
        );
        verify(repo, never()).updateStatus(any(TransactionStatus.class),any(String.class),anyLong());
    }
    @Test
    void updateStatus_id_null(){
        assertThrows(
                NullPointerException.class,
                () -> service.updateStatus(DEFAULT_TRANSACTION)
        );
        verify(repo, never()).updateStatus(any(TransactionStatus.class),any(String.class),anyLong());
    }
    @Test
    void updateStatus_non_exist_transaction(){
        Transaction badTransaction = DEFAULT_TRANSACTION.toBuilder().transactionId(Long.MAX_VALUE).build();
        given(repo.updateStatus(any(TransactionStatus.class),any(String.class),anyLong())).willReturn(0);

        assertThrows(
                EntityNotFoundException.class,
                () -> service.updateStatus(badTransaction)
        );
    }
}