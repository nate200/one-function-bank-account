package simple.account.demo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simple.account.demo.exception.business.BadRequestParameterException;
import simple.account.demo.model.Account;
import simple.account.demo.repository.AccountRepository;
import simple.account.demo.util.CurrencyUtil;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AccountService {
    AccountRepository accountRepo;

    public void createAccountRequest(@NonNull Account account) {
        Account processingAccount = account.toBuilder().build();

        checkValidAccountBeforeSaving(processingAccount);
        processingAccount.setId(null);
        processingAccount.setEmail(processingAccount.getEmail().trim());
        processingAccount.setCurrency(processingAccount.getCurrency().trim());

        accountRepo.save(processingAccount);
    }

    public Account getAccountById(long accId) {
        return accountRepo.findById(accId)
            .orElseThrow(() -> throwAccountNotFound(accId));
    }

    public String getAccountRawCurrency(long accId) {
        return getAccountById(accId).getCurrency();
    }

    @Transactional
    public void changeTotal(@NonNull BigDecimal amount, long accId){
        int rowAffected = accountRepo.changeTotal(amount, accId);
        if(rowAffected == 0)
            throw throwAccountNotFound(accId);
    }

    private static EntityNotFoundException throwAccountNotFound(long accId){
        return new EntityNotFoundException("no account id:" + accId);
    }

    private void checkValidAccountBeforeSaving(Account account) {
        checkNullFieldsAccount(account);
        checkCurrencySupport(account);
        checkEmail(account);
    }
    private void checkNullFieldsAccount(Account account) {
        try {
            Objects.requireNonNull(account.getEmail(), "email");
            Objects.requireNonNull(account.getTotal(), "total");
            Objects.requireNonNull(account.getCurrency(), "currency");
        }
        catch (NullPointerException e) {
            throw new BadRequestParameterException(e.getMessage() + " of the new account must not be null");
        }
    }
    private void checkCurrencySupport(Account account){
        try{ CurrencyUtil.getCurrencyFromString(account.getCurrency());}
        catch (Exception e){
            throw new BadRequestParameterException(e.getMessage());
        }
    }
    private void checkEmail(Account account){
        String email = account.getEmail().trim();
        if(!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9+_.-]+\\.(.+)$"))
            throw new BadRequestParameterException("Bad Email format:[" + account.getEmail() + "]");

        Optional<Account> savedAcc = accountRepo.findByEmail(email);
        if(savedAcc.isPresent())
            throw new BadRequestParameterException("Account with email[" + account.getEmail() + "] is already exist");
    }
}
