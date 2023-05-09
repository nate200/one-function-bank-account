package omg.simple.account.core.repository;

import omg.simple.account.core.model.business.Token;
import omg.simple.account.core.model.business.UserAccount;
import omg.simple.account.core.model.constant.TokenType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class TokenRepositoryTest {
    @Autowired
    TokenRepository tokenRepo;
    @Autowired
    UserAccountRepository userAccRepo;

    @Test
    void save_dup_token_error(){
        UserAccount user = userAccRepo.save(UserAccount.builder().email("a..").build());
        String dupToken = "aaaa";

        tokenRepo.save(defaultToken(user).toBuilder().token(dupToken).revoked(false).expired(false).build());

        assertThrows(
                DataIntegrityViolationException.class,
                () -> tokenRepo.save(defaultToken(user).toBuilder().token(dupToken).revoked(false).expired(true).build()));
    }

    @Test
    void findAllValidByUserAccountId(){
        UserAccount user = userAccRepo.save(UserAccount.builder().email("a").build());
        List<String> expectedTokens = List.of("rfef","rfet","rtef");

        tokenRepo.save(defaultToken(user).toBuilder().token(expectedTokens.get(0)).revoked(false).expired(false).build());
        tokenRepo.save(defaultToken(user).toBuilder().token(expectedTokens.get(1)).revoked(false).expired(true).build());
        tokenRepo.save(defaultToken(user).toBuilder().token(expectedTokens.get(2)).revoked(true).expired(false).build());
        tokenRepo.save(defaultToken(user).toBuilder().token("wrong").revoked(true).expired(true).build());

        List<Token> actualTokens = tokenRepo.findAllValidByUserAccountId(user.getId());
        assertEquals(expectedTokens.size(), actualTokens.size());
        for(int i = 0; i < expectedTokens.size(); i++)
            assertEquals(expectedTokens.get(i), actualTokens.get(i).getToken());
    }

    Token defaultToken(UserAccount user){
        return Token.builder().tokenType(TokenType.BEARER)
                .user(user).build();
    }
}