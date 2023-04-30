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
import simple.account.demo.model.Transaction;
import simple.account.demo.model.TransactionStatus;
import simple.account.demo.repository.TransactionRepository;

import java.util.Optional;
import java.util.stream.Stream;

import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static simple.account.demo.model.TransactionStatus.DONE;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    TransactionRepository repo;
    @InjectMocks
    TransactionService service;

    static final Transaction DEFAULT_TRANSACTION = new Transaction(null, "THB", 1L, 2L, TEN, null,null);

    @Test
    void saveTransaction(){
        given(repo.save(any(Transaction.class))).willReturn(DEFAULT_TRANSACTION);

        service.saveTransactionRequest(DEFAULT_TRANSACTION);

        verify(repo, times(1)).save(any(Transaction.class));
    }
    @Test
    void saveTransaction_null(){
        assertThrows(
                NullPointerException.class,
                () -> service.saveTransactionRequest(null)
        );
        verify(repo, never()).save(any(Transaction.class));
    }


    @Test
    void updateStatus(){
        Transaction transaction = DEFAULT_TRANSACTION.toBuilder()
                .transactionId(1L)
                .transaction_status(DONE)
                .transaction_result("done").build();
        given(repo.updateStatus(any(TransactionStatus.class),any(String.class),anyLong())).willReturn(1);

        service.updateStatus(transaction);

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
    @ParameterizedTest
    @MethodSource("badTransactionWithNullField")
    void updateStatus_bad_transaction_with_null_field(Transaction badTransaction){
        assertThrows(
                NullPointerException.class,
                () -> service.updateStatus(badTransaction)
        );
        verify(repo, never()).updateStatus(any(TransactionStatus.class),any(String.class),anyLong());
    }
    @Test
    void updateStatus_non_exist_transaction(){
        Transaction badTransaction = DEFAULT_TRANSACTION.toBuilder()
                .transactionId(Long.MAX_VALUE)
                .transaction_status(DONE)
                .transaction_result("done").build();
        given(repo.updateStatus(any(TransactionStatus.class),any(String.class),anyLong())).willReturn(0);

        assertThrows(
                EntityNotFoundException.class,
                () -> service.updateStatus(badTransaction)
        );
    }


    static Stream<Transaction> badTransactionWithNullField(){
        return Stream.of(
                DEFAULT_TRANSACTION,//null id
                DEFAULT_TRANSACTION.toBuilder().transactionId(1L).transaction_status(null).transaction_result("a").build(),
                DEFAULT_TRANSACTION.toBuilder().transactionId(1L).transaction_status(DONE).transaction_result(null).build()
        );
    }
}