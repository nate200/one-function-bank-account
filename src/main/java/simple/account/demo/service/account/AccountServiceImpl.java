package simple.account.demo.service.account;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simple.account.demo.common.ExchangeRateApi;
import simple.account.demo.model.Account;
import simple.account.demo.model.Transaction;
import simple.account.demo.repository.AccountRepository;
import simple.account.demo.repository.TransactionRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    AccountRepository accountRepo;

    public Account saveAccount(Account account) {
        Optional<Account> savedAcc = accountRepo.findById(account.getId());
        if(savedAcc.isPresent())
            throw new IllegalArgumentException("Account already exist with given Id:" + account.getId());

        return accountRepo.save(account);
    }

    public Account getAccountById(long accId) {
        return accountRepo.findById(accId)
            .orElseThrow(() -> new EntityNotFoundException("no account id:" + accId));
    }

    public String getAccountCurrency(long accId) {
        return getAccountById(accId).getCurrency();
    }

    public void changeTotal(BigDecimal amount, long id){
        accountRepo.changeTotal(amount, id);
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
