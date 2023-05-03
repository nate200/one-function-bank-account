package omg.simple.account.security.repository;

import java.util.Optional;
import omg.simple.account.security.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long>{
    Optional<UserAccount> findByEmail(String email);
}
