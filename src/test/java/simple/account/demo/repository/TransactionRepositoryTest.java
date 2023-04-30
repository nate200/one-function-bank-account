package simple.account.demo.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import simple.account.demo.model.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static simple.account.demo.model.TransactionStatus.*;

@DataJpaTest
class TransactionRepositoryTest {
    @Autowired
    TransactionRepository transactionRepo;
    @Autowired
    TestEntityManager em;

    static final String THB = "THB";

    @Test
    void test_insert() {
        Transaction expected = new Transaction(null, THB, 1L, 2L, ZERO, DONE, "OKIE DOKIE");
        em.persist(expected);

        transactionRepo.save(Transaction.builder()//expected.toBuilder().build() //return the same instance...
            .currency(expected.getCurrency())
            .fromAcc(expected.getFromAcc())
            .toAcc(expected.getToAcc())
            .amount(expected.getAmount())
            .transaction_status(expected.getTransaction_status())
            .transaction_result(expected.getTransaction_result()).build()
        );

        List<Transaction> transactions = transactionRepo.findAll();
        assertEquals(2,transactions.size());
        Transaction actual = transactions.get(0);
        assertThat(actual)
            .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }
    @ParameterizedTest
    @MethodSource("invalidTransactionData")
    void test_insert_invalidTransaction(Transaction transaction) {
        jakarta.validation.ConstraintViolationException error = assertThrows(
            jakarta.validation.ConstraintViolationException.class,
            () -> transactionRepo.save(transaction)
        );
        assertThat(error.getMessage()).contains("must not be null");
    }

    @ParameterizedTest
    @MethodSource("validUpdateStatusData")
    void test_updateStatus_valid(Transaction initTransaction, Transaction expectedTransaction) {
        transactionRepo.save(initTransaction);
        expectedTransaction.setTransactionId(initTransaction.getTransactionId());

        int affectedRows = transactionRepo.updateStatus(
            expectedTransaction.getTransaction_status(),
            expectedTransaction.getTransaction_result(),
            expectedTransaction.getTransactionId()
        );

        em.clear();
        assertEquals(1, affectedRows);
        Transaction actualTransaction = transactionRepo.findById(initTransaction.getTransactionId()).get();
        assertThat(actualTransaction)
            .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)//https://stackoverflow.com/questions/63714635/assertj-fails-to-assert-bigdecimal-equality-without-scale
            .usingRecursiveComparison()
            .isEqualTo(expectedTransaction);
    }
    @ParameterizedTest
    @MethodSource("invalidUpdateStatusData")
    void test_updateStatus_invalid_parameter(Transaction initTransaction, Transaction invalidTransaction) {
        transactionRepo.save(initTransaction);
        invalidTransaction.setTransactionId(initTransaction.getTransactionId());

        assertThrows(
                DataIntegrityViolationException.class,
                () -> transactionRepo.updateStatus(
                        invalidTransaction.getTransaction_status(),
                        invalidTransaction.getTransaction_result(),
                        invalidTransaction.getTransactionId()
                )
        );
    }
    

    static Stream<Transaction> invalidTransactionData() {
        return Stream.of(
            new Transaction(null,null, 1L, 2L, ZERO, DONE, "OKIE DOKIE"),
            new Transaction(null,THB, 1L, 2L, null, DONE, "OKIE DOKIE"),
            new Transaction(null,THB, 1L, 2L, ZERO, null, "OKIE DOKIE"),
            new Transaction(null,THB, 1L, 2L, ZERO, DONE, null)
        );
    }
    static Stream<Arguments> validUpdateStatusData() {
        return Stream.of(
            arguments(
                new Transaction(null, THB, 1L, 2L, ZERO, PROCESSING, "plz wait ;)"),
                new Transaction(null, THB, 1L, 2L, ZERO, DONE, "DONE AND DONE")
            ),
            arguments(
                new Transaction(null, THB, 1L, 2L, ZERO, PROCESSING, "plz wait a little more ;)"),
                new Transaction(null, THB, 1L, 2L, ZERO, FAIL, "oopies")
            )
        );
    }
    static Stream<Arguments> invalidUpdateStatusData() {
        return Stream.of(
                arguments(
                        new Transaction(null, THB, 1L, 2L, ZERO, PROCESSING, "plz wait ;)"),
                        new Transaction(null, THB, 1L, 2L, ZERO, null, "DONE AND DONE")
                ),
                arguments(
                        new Transaction(null, THB, 1L, 2L, ZERO, PROCESSING, "plz wait a little more ;)"),
                        new Transaction(null, THB, 1L, 2L, ZERO, FAIL, null)
                )
        );
    }
}