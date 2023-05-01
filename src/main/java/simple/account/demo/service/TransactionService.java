package simple.account.demo.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simple.account.demo.exception.business.BadRequestParameterException;
import simple.account.demo.model.Transaction;
import simple.account.demo.repository.TransactionRepository;

@Service
@AllArgsConstructor
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
        String errMsg = null;
        if(transaction.getTransactionId() == null)
            errMsg = "id must not be null when updating transaction status";
        else if(transaction.getTransaction_status() == null)
            errMsg = "Transaction_status must not be null when updating transaction status";
        else if(transaction.getTransaction_result() == null)
            errMsg = "Transaction_result must not be null when updating transaction status";

        if(errMsg != null)
            throw new NullPointerException(errMsg);
    }
}
