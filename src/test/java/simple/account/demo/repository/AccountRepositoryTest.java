package simple.account.demo.repository;

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
import simple.account.demo.model.Account;

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

    @Test
    void insert() {
        BigDecimal expectedTotal = ZERO;
        String expectedCurrency = "THB";

        Account addedAcc = accountRepo.save(new Account(null, expectedTotal, expectedCurrency));

        assertEquals(1, accountRepo.count());
        em.clear();
        Account actualAcc = accountRepo.findById(addedAcc.getId()).get();
        assertThat(actualAcc.getTotal()).isEqualByComparingTo(expectedTotal);
        assertEquals(actualAcc.getCurrency(), expectedCurrency);
    }
    @ParameterizedTest
    @MethodSource("invalidAccounts")
    void insert_invalid_account_then_throw(Account acc){
        jakarta.validation.ConstraintViolationException error = assertThrows(
                jakarta.validation.ConstraintViolationException.class,
            () -> accountRepo.save(acc)
        );
        assertThat(error.getMessage()).contains("must not be null");
    }

    @Test
    void findCurrencyById() {
        String expectedCurrency = "CHF";
        Account acc = accountRepo.save(new Account(null, ZERO, expectedCurrency));

        String currency = accountRepo.findCurrencyById(acc.getId());

        assertEquals(expectedCurrency, currency);
    }
    @Test
    void findCurrency_of_non_exist_account() {
        String currency = accountRepo.findCurrencyById(Long.MAX_VALUE);
        assertNull(currency);
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
    @MethodSource("invalidAmounts")
    @Transactional
    void changeTotal_bad_amount_trigger_total_constraint(BigDecimal invalidAmount){
        Account acc = accountRepo.save(new Account(null, TEN, "EUR"));

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
                new Account(null, null, "THB"),
                new Account(null, TEN, null)
        );
    }
    static Stream<Arguments> validTransaction() {
        return Stream.of(
                arguments(
                        new Account(null, TEN, "THB"),
                        BigDecimal.valueOf(50)
                ),
                arguments(
                        new Account(null, BigDecimal.valueOf(50000.0), "USD"),
                        BigDecimal.valueOf(-50)
                )
        );
    }
    static Stream<BigDecimal> invalidAmounts() {
        return Stream.of(null, BigDecimal.valueOf(-999999));
    }
}