package simple.account.demo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import simple.account.demo.model.Account;
import simple.account.demo.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    AccountRepository accountRepo;

    public Account saveAccount(@NonNull Account account) {
        Optional<Account> savedAcc = accountRepo.findById(account.getId());
        if(savedAcc.isPresent())
            throw new IllegalArgumentException("Account already exist with given Id:" + account.getId());

        return accountRepo.save(account);
    }

    public Account getAccountById(long accId) {
        return accountRepo.findById(accId)
            .orElseThrow(() -> throwAccountNotFound(accId));
    }

    public String getAccountRawCurrency(long accId) {
        return getAccountById(accId).getCurrency();
    }

    public void changeTotal(@NonNull BigDecimal amount, long accId){
        int rowAffected = accountRepo.changeTotal(amount, accId);
        if(rowAffected == 0)
            throw throwAccountNotFound(accId);
    }

    private static EntityNotFoundException throwAccountNotFound(long accId){
        return new EntityNotFoundException("no account id:" + accId);
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
