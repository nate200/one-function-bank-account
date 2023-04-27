package simple.account.demo.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import simple.account.demo.model.Transaction;
import simple.account.demo.repository.TransactionRepository;

@Service
@RequiredArgsConstructor
public class TransactionService {
    TransactionRepository transactionRepo;

    public Transaction saveTransaction(@NonNull Transaction transaction){
        return transactionRepo.save(transaction);
    }
}
