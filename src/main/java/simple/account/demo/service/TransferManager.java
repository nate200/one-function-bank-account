package simple.account.demo.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simple.account.demo.common.ExchangeRateApi;
import simple.account.demo.model.Transaction;
import simple.account.demo.model.TransactionStatus;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@AllArgsConstructor
public class TransferManager {
    ExchangeRateApi exchangeApi;
    AccountService accountService;
    TransactionService transactionService;

    public void transferWithInApp(@NonNull Transaction transaction) throws Exception {
        try{
            transaction.setTransaction_status(TransactionStatus.PROCESSING);
            transaction.setTransaction_result("plz wait");
            transactionService.saveTransaction(transaction);

            validateTransaction(transaction);
            transferBetweenAccounts(transaction);

            transaction.setTransaction_status(TransactionStatus.DONE);
            transaction.setTransaction_result("done");
        }
        catch (Exception e){
            transaction.setTransaction_status(TransactionStatus.FAIL);
            transaction.setTransaction_result(e.getMessage());
            throw e;
        }
        finally {
            transactionService.updateStatus(transaction);
        }
    }
    @Transactional
    private void transferBetweenAccounts(Transaction transaction) throws Exception
    {
        System.out.println("transaction: " + transaction);

        String currencyTran = transaction.getCurrency();
        BigDecimal amount = transaction.getAmount();//ex: INR -> USD -> CND

        String currencyFrom = accountService.getAccountCurrency(transaction.getFromAcc());
        BigDecimal convertedWithdraw = exchangeApi.convert(currencyTran, currencyFrom, amount);
        accountService.changeTotal(convertedWithdraw.negate(), transaction.getFromAcc());

        String currencyTo = accountService.getAccountCurrency(transaction.getToAcc());
        BigDecimal convertedTransfer = exchangeApi.convert(currencyTran, currencyTo, amount);
        accountService.changeTotal(convertedTransfer, transaction.getToAcc());
    }
    private void validateTransaction(Transaction transaction){
        Objects.requireNonNull(transaction.getCurrency(), "The currency of the transaction must not be null");
        Objects.requireNonNull(transaction.getAmount(), "The amount of the transaction must not be null");

        if(transaction.getToAcc() == transaction.getFromAcc())
            throw new IllegalArgumentException("Can't do a transaction on the same account");
        if(transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("The exchange amount must be decimal and greater than 0");
    }
}