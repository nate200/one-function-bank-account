package simple.account.demo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Before;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import simple.account.demo.model.Account;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;


@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)//https://stackoverflow.com/questions/41315386/spring-boot-1-4-datajpatest-error-creating-bean-with-name-datasource
class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepo;
    @PersistenceContext
    EntityManager entityManager;//https://stackoverflow.com/questions/52857963/how-to-test-method-from-repository-which-marked-as-modifying

    final List<Account> ACCOUNTS = List.of(
        new Account(null, BigDecimal.TEN, "THB"),
        new Account(null, BigDecimal.valueOf(50000.0), "USD")
    );

    @Test
    void test_Autowired(){
        assertNotNull(accountRepo);
    }

    @Test
    void test_insert_null_to_NonNull(){
        jakarta.validation.ConstraintViolationException error = assertThrows(
                jakarta.validation.ConstraintViolationException.class,
            () -> accountRepo.save(new Account(null,BigDecimal.TEN,null)),
            "Expected ConstraintViolationException.class"
        );

        assertThat(error.getMessage(), containsString("must not be null"));
    }

    @Test
    void test_insertSuccess(){
        accountRepo.saveAll(ACCOUNTS);
        assertTrue(ACCOUNTS.size() <= accountRepo.count());
    }

    @Test
    void test_findCurrencyById() {
        Account acc = accountRepo.save(ACCOUNTS.get(0));

        String currency = accountRepo.findCurrencyById(acc.getId());

        assertEquals(acc.getCurrency(), currency);
    }

    @Test
    @Transactional
    void test_changeTotal_constraint(){
        Account acc = accountRepo.save(ACCOUNTS.get(0));

        DataIntegrityViolationException error = assertThrows(
            DataIntegrityViolationException.class,
            () -> accountRepo.changeTotal(new BigDecimal(-999999), acc.getId()),
            "Expected DataIntegrityViolationException.class"
        );

        System.out.println(error.getClass().getName() + " " + error.getMessage() + " " + error.getLocalizedMessage());
        assertThat(error.getCause(), instanceOf(ConstraintViolationException.class));
        //ConstraintViolationException cause = (ConstraintViolationException)error.getCause();
        //assertEquals("account_total_check", cause.getConstraintName()); //H2 not Postgres
    }

    @ParameterizedTest
    @CsvSource({"0,50", "1,-50"})
    @Transactional
    void test_changeTotal(int accIndex, BigDecimal amount) {
        Account acc = accountRepo.save(ACCOUNTS.get(accIndex));
        long accId = acc.getId();

        BigDecimal totalBefore = getTotalById(accId);

        int res = accountRepo.changeTotal(amount, accId);
        System.out.println(res);

        entityManager.clear();
        BigDecimal totalAfter = getTotalById(accId);
        BigDecimal totalExpected = totalBefore.add(amount);
        assertThat(totalExpected, comparesEqualTo(totalAfter));
    }

    @Test
    @Transactional
    void test_changeTotal_affectedRows() {
        accountRepo.saveAll(ACCOUNTS);

        List<Account> accs = accountRepo.findAll();
        long delAccId = accs.get(0).getId();

        int affectedRows = accountRepo.changeTotal(BigDecimal.ONE, delAccId);
        assertEquals(1, affectedRows);

        accountRepo.deleteById(delAccId);
        affectedRows = accountRepo.changeTotal(BigDecimal.ONE, delAccId);
        assertEquals(0, affectedRows);
    }

    private BigDecimal getTotalById(long accId){
        Account account = accountRepo.findById(accId).orElseThrow();
        return account.getTotal();
    }
}