package omg.simple.account.security.repository;

import java.util.List;
import java.util.Optional;
import omg.simple.account.security.model.security.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository extends JpaRepository<Token, Long>{
    @Query("""
        select t from Token t inner join UserAccount u 
        on t.user.id = u.id 
        where u.id = :uid and (t.expired = false or t.revoked = false)
    """)
    List<Token> findAllValidByUserAccountId(long uid);

    Optional<Token> findByToken(String token);
}
