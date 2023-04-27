package simple.account.demo.service.account;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import simple.account.demo.model.Account;
import simple.account.demo.model.Transaction;

import java.io.IOException;
import java.math.BigDecimal;

public interface AccountService {
    Account saveAccount(Account account);
    Account getAccountById(long accId);
    String getAccountCurrency(long id);
    void changeTotal(BigDecimal amount, long id);
}
