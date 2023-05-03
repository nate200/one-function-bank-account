package omg.simple.account.core.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import omg.simple.account.core.model.Account;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.math.BigDecimal.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;


@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)//https://stackoverflow.com/questions/41315386/spring-boot-1-4-datajpatest-error-creating-bean-with-name-datasource
class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepo;
    @PersistenceContext
    EntityManager em;//https://stackoverflow.com/questions/52857963/how-to-test-method-from-repository-which-marked-as-modifying

    final Account DEFAULT_ACCOUNT = new Account(null, ZERO, "THB", "a@a.com");

    @Test
    void insert() {
        Account addedAcc = accountRepo.save(DEFAULT_ACCOUNT);
        em.clear();

        Account actualAcc = accountRepo.findById(addedAcc.getId()).get();
        assertThat(actualAcc)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .usingRecursiveComparison()
                .isEqualTo(DEFAULT_ACCOUNT);
    }
    @ParameterizedTest
    @MethodSource("invalidAccounts")
    void insert_invalid_account_then_throw(Account acc){
        jakarta.validation.ConstraintViolationException error = assertThrows(
                jakarta.validation.ConstraintViolationException.class,
            () -> accountRepo.save(acc)
        );
    }

    @ParameterizedTest
    @MethodSource("validTransaction")
    @Transactional
    void changeTotal(Account acc, BigDecimal amount) {
        accountRepo.save(acc);
        long accId = acc.getId();
        BigDecimal totalBefore = getTotalById(accId);

        int affectedRow = accountRepo.changeTotal(amount, accId);

        em.clear();
        assertEquals(1, affectedRow);
        BigDecimal totalAfter = getTotalById(accId);
        BigDecimal totalExpected = totalBefore.add(amount);
        assertThat(totalExpected).isEqualByComparingTo(totalAfter);
    }
    @Test
    @Transactional
    void changeTotal_on_non_exist_account(){
        int affectedRow = accountRepo.changeTotal(TEN, Long.MIN_VALUE);
        assertEquals(0, affectedRow);
    }
    @ParameterizedTest
    @MethodSource("invalidAmountsTransaction")
    @Transactional
    void changeTotal_bad_amount_trigger_total_constraint(BigDecimal invalidAmount){
        Account acc = accountRepo.save(new Account(null, TEN, "EUR", "a@a.com"));

        assertThrows(
            DataIntegrityViolationException.class,
            () -> accountRepo.changeTotal(invalidAmount, acc.getId())
        );
    }


    private BigDecimal getTotalById(long accId){
        Account account = accountRepo.findById(accId).orElseThrow();
        return account.getTotal();
    }

    static Stream<Account> invalidAccounts() {
        return Stream.of(
                new Account(null, null, "THB", "a@a.com"),
                new Account(null, TEN, null, "a@a.com"),
                new Account(null, TEN, "THB", null),
                new Account(null, TEN, "THB", ""),
                new Account(null, TEN, "THB", "      "),
                new Account(null, TEN, "THB", "a@"),
                new Account(null, TEN, "THB", "a@acom")
        );
    }
    static Stream<Arguments> validTransaction() {
        return Stream.of(
                arguments(
                        new Account(null, TEN, "THB", "a@a.com"),
                        BigDecimal.valueOf(50)
                ),
                arguments(
                        new Account(null, BigDecimal.valueOf(50000.0), "USD", "a@a.com"),
                        BigDecimal.valueOf(-50)
                )
        );
    }
    static Stream<BigDecimal> invalidAmountsTransaction() {
        return Stream.of(null, BigDecimal.valueOf(-999999));
    }
}