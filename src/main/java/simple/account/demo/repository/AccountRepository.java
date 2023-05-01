package simple.account.demo.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import simple.account.demo.model.Account;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Modifying
    @Query("""
        UPDATE Account
        SET total = total + :amount
        WHERE id = :accId
    """)
    int changeTotal(//https://stackoverflow.com/questions/59595923/throwing-exception-for-update-query-if-where-get-empty-result
        @Param("amount") BigDecimal amount,
        @Param("accId") long accId
    );

    Optional<Account> findByEmail(String email);
}
