package simple.account.demo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import simple.account.demo.model.Transaction;
import simple.account.demo.repository.TransactionRepository;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    TransactionRepository transactionRepo;

    public Transaction saveTransaction(@NonNull Transaction transaction){
        Optional<Transaction> savedTransa = transactionRepo.findById(transaction.getTransactionId());
        if(savedTransa.isPresent())
            throw new IllegalArgumentException("Transaction already exist with given Id:" + transaction.getTransactionId());

        return transactionRepo.save(transaction);
    }

    public void updateStatus(@NonNull Transaction transaction){
        Objects.requireNonNull(
                transaction.getTransactionId(),
                "can't update transaction status with id: null"
        );

        int rowAffected = transactionRepo.updateStatus(
            transaction.getTransaction_status(),
            transaction.getTransaction_result(),
            transaction.getTransactionId()
        );

        if(rowAffected == 0)
            throw new EntityNotFoundException("can't update non existing transaction with id: " + transaction.getTransactionId());
    }
}
