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
        checkValidAccountBeforeSaving(account);
        account.setId(null);
        account.setEmail(account.getEmail().trim());
        account.setCurrency(account.getCurrency().trim());
        accountRepo.save(account);
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
        System.out.println("changeTotal: " + amount + " " + accId);
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

    /*
    move to TransferManager
    @Transactional
    public void transferToWithInApp(Transaction transaction) throws IllegalArgumentException, IOException
    {
        validateTransaction(transaction);
        System.out.println("transaction: " + transaction);

        String currencyTran = transaction.getCurrency();
        BigDecimal amount = transaction.getAmount();//ex: INR -> USD -> CND

        String currencyFrom = getAccountCurrency(transaction.getFromAcc());
        BigDecimal convertedWithdraw = exchangeRateApi.convert(currencyTran, currencyFrom, amount);
        accountRepo.changeTotal(convertedWithdraw.negate(), transaction.getFromAcc());

        String currencyTo = getAccountCurrency(transaction.getToAcc());
        BigDecimal convertedTransfer = exchangeRateApi.convert(currencyTran, currencyTo, amount);
        accountRepo.changeTotal(convertedTransfer, transaction.getToAcc());
    }
    private void validateTransaction(Transaction transaction){
        //more of these checking...will have to create a checker class instead
        if(transaction.getToAcc().equals(transaction.getFromAcc()))
            throw new IllegalArgumentException("Can't do a transaction on the same account");
        if(transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Transaction amount must be greater than 0");
    }
    private String getAccountCurrency(long id) {
        String currency = accountRepo.findCurrencyById(id);
        System.out.println("account[" + id + "]=" + currency);
        return currency;
    }*/
}
