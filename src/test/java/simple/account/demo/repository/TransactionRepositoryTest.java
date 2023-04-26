package simple.account.demo.repository;

import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import simple.account.demo.model.Account;
import simple.account.demo.model.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {
    @Autowired
    TransactionRepository transactionRepo;
    @Autowired
    TestEntityManager em;

    @Test
    void test_insert() {
        Transaction tm = new Transaction(null, "THB", 1L, 2L, ZERO, "done");
        em.persist(tm);

        transactionRepo.save(Transaction.builder()
            .currency(tm.getCurrency())
            .fromAcc(tm.getFromAcc())
            .toAcc(tm.getToAcc())
            .amount(tm.getAmount())
            .transaction_result(tm.getTransaction_result()).build()
        );
        List<Transaction> transactions = transactionRepo.findAll();

        assertEquals(2,transactions.size());
        Transaction transaction = transactions.get(0);
        assertEquals(tm.getCurrency(),transaction.getCurrency());
        assertEquals(tm.getFromAcc(),transaction.getFromAcc());
        assertEquals(tm.getToAcc(),transaction.getToAcc());
        assertEquals(tm.getAmount(),transaction.getAmount());
        assertEquals(tm.getTransaction_result(),transaction.getTransaction_result());
    }

    @ParameterizedTest
    @MethodSource("invalidTransactionData")
    void test_insert_invalidTransaction(Transaction transaction) {
        jakarta.validation.ConstraintViolationException error = assertThrows(
            jakarta.validation.ConstraintViolationException.class,
            () -> transactionRepo.save(transaction)
        );

        assertThat(error.getMessage(), containsString("must not be null"));
    }

    static Stream<Transaction> invalidTransactionData() {
        return Stream.of(
            new Transaction(null,null, 1L, 2L, ZERO, "done"),
            new Transaction(null,"THB", null, 2L, ZERO, "done"),
            new Transaction(null,"THB", 1L, null, ZERO, "done"),
            new Transaction(null,"THB", 1L, 2L, null, "done"),
            new Transaction(null,"THB", 1L, 2L, ZERO, null)
        );
    }
}