package simple.account.demo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simple.account.demo.model.Transaction;
import simple.account.demo.repository.TransactionRepository;

import static java.util.Objects.requireNonNull;

@Service
@AllArgsConstructor
public class TransactionService {
    TransactionRepository transactionRepo;

    public Transaction saveNewTransaction(@NonNull Transaction transaction){
        transaction.setTransactionId(null);
        return transactionRepo.save(transaction);
    }

    @Transactional
    public void updateStatus(@NonNull Transaction transaction){
        checkTransactionBeforeUpdatingStatus(transaction);

        int rowAffected = transactionRepo.updateStatus(
            transaction.getTransaction_status(),
            transaction.getTransaction_result(),
            transaction.getTransactionId()
        );

        if(rowAffected == 0)
            throw new EntityNotFoundException("can't update non existing transaction with id: " + transaction.getTransactionId());
    }
    private void checkTransactionBeforeUpdatingStatus(Transaction transaction) {
        requireNonNull(transaction.getTransactionId(), "Transaction.id must not be null when updating transaction status");
        requireNonNull(transaction.getTransaction_status(),"Transaction.transaction_status must not be null when updating transaction status");
        requireNonNull(transaction.getTransaction_result(), "Transaction.transaction_result must not be null when updating transaction status");
    }
}
