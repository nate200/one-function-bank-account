package simple.account.demo.service.transaction;

import lombok.RequiredArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import simple.account.demo.model.Transaction;
import simple.account.demo.repository.TransactionRepository;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    TransactionRepository transactionRepo;

    public Transaction saveTransaction(@NonNull Transaction transaction){
        return transactionRepo.save(transaction);
    }
}
