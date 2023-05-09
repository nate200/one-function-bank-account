package omg.simple.account.core.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import omg.simple.account.core.exception.business.BadRequestParameterException;
import omg.simple.account.core.model.business.Account;
import omg.simple.account.core.model.business.UserAccount;
import omg.simple.account.core.model.constant.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserAccountRepositoryTest {
    @Autowired
    UserAccountRepository userAccRepo;
    @PersistenceContext
    EntityManager em;

    @Test
    void insert() {
        UserAccount expected = defaultUserAcc().toBuilder().email("a@a.com").passw("1234").build();
        userAccRepo.save(expected);
        em.clear();

        UserAccount actual = userAccRepo.findById(expected.getId()).get();
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
    @Test
    void insert_dupEmail_error() {
        String dupEmail = "a1@a1.com";
        UserAccount user = defaultUserAcc().toBuilder().email(dupEmail).passw("1234").build();
        em.persist(user);

        UserAccount newUser = defaultUserAcc().toBuilder().email(dupEmail).passw("5678").build();
        assertThrows(
            DataIntegrityViolationException.class,
            () -> userAccRepo.save(newUser));
        //assertEquals(2, userAccRepo.count());
    }

    UserAccount defaultUserAcc(){
        return UserAccount.builder().fname("fname").lname("lname").role(Role.USER).build();
    }
}