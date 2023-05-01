package simple.account.demo.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import simple.account.demo.model.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static simple.account.demo.model.TransactionStatus.*;

@DataJpaTest
@EnableJpaAuditing
class TransactionRepositoryTest {
    @Autowired
    TransactionRepository transactionRepo;
    @Autowired
    TestEntityManager em;

    static final String THB = "THB";
    static Transaction DEFAULT_TRANSACTION = new Transaction(null, THB, 1L, 2L, ZERO, PROCESSING, "OKIE DOKIE", null);

    @Test
    void test_insert() {
        Transaction expected = new Transaction(null, THB, 1L, 2L, TEN, PROCESSING, "OKIE DOKIE", null);
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
            .hasNoNullFieldsOrProperties()//assertNotNull(actual.getCreateTime());//need @EnableJpaAuditing and @EntityListeners(AuditingEntityListener.class)
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
        expectedTransaction.setCreateTime(actualTransaction.getCreateTime());
        assertThat(actualTransaction)
            .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)//https://stackoverflow.com/questions/63714635/assertj-fails-to-assert-bigdecimal-equality-without-scale
            .hasNoNullFieldsOrProperties()
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
                DEFAULT_TRANSACTION.toBuilder().currency(null).build(),
                DEFAULT_TRANSACTION.toBuilder().amount(null).build(),
                DEFAULT_TRANSACTION.toBuilder().transaction_status(null).build(),
                DEFAULT_TRANSACTION.toBuilder().transaction_result(null).build()
        );
    }
    static Stream<Arguments> validUpdateStatusData() {
        Transaction processingTransaction = DEFAULT_TRANSACTION.toBuilder().transaction_status(PROCESSING).transaction_result("plz wait ;)").build();
        return Stream.of(
            arguments(
                    processingTransaction,
                    DEFAULT_TRANSACTION.toBuilder().transaction_status(DONE).transaction_result("DONE AND DONE").build()
            ),
            arguments(
                    processingTransaction.toBuilder().transaction_result("plz wait a little longer ;)").build(),//have to create a new object, hibernate will just reuse the object with the same hashcode
                    DEFAULT_TRANSACTION.toBuilder().transaction_status(FAIL).transaction_result("error").build()
            )
        );
    }
    static Stream<Arguments> invalidUpdateStatusData() {
        Transaction processingTransaction = DEFAULT_TRANSACTION.toBuilder().transaction_status(PROCESSING).transaction_result("plz wait ;)").build();
        return Stream.of(
                arguments(
                        processingTransaction,
                        DEFAULT_TRANSACTION.toBuilder().transaction_status(null).transaction_result("DONE").build()
                ),
                arguments(
                        processingTransaction.toBuilder().transaction_result("plz wait a little longer ;)").build(),
                        DEFAULT_TRANSACTION.toBuilder().transaction_status(FAIL).transaction_result(null).build()
                )
        );
    }
}