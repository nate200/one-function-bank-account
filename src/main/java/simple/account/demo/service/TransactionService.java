package simple.account.demo.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import simple.account.demo.exception.BadRequestParameterException;
import simple.account.demo.model.Transaction;
import simple.account.demo.repository.TransactionRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransactionService {
    TransactionRepository transactionRepo;

    public Transaction saveTransactionRequest(@NonNull Transaction transaction){
        try {
            transaction.setTransactionId(null);
            return transactionRepo.save(transaction);
        }
        catch (ConstraintViolationException e){
            throw new BadRequestParameterException("Can't process invalid: " + transaction);
        }
    }

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
        Objects.requireNonNull(
                transaction.getTransactionId(),
                "id must not be null when updating transaction status"
        );
        Objects.requireNonNull(
                transaction.getTransaction_status(),
                "Transaction_status must not be null when updating transaction status"
        );
        Objects.requireNonNull(
                transaction.getTransaction_result(),
                "Transaction_result must not be null when updating transaction status"
        );
    }
}
