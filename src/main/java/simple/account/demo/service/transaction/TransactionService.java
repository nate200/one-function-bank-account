package simple.account.demo.service.transaction;

import org.springframework.lang.NonNull;
import simple.account.demo.model.Transaction;

public interface TransactionService {
    Transaction saveTransaction(@NonNull Transaction transaction);
}
