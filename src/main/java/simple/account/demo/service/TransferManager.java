package simple.account.demo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simple.account.demo.exception.business.BadRequestParameterException;
import simple.account.demo.exception.business.UnprocessableContentException;
import simple.account.demo.model.Transaction;
import simple.account.demo.model.TransactionStatus;
import simple.account.demo.util.CurrencyUtil;
import simple.account.demo.util.ExchangeRateApi;

import java.math.BigDecimal;
import java.util.Currency;

@Service
@AllArgsConstructor
public class TransferManager {
    ExchangeRateApi exchangeApi;
    AccountService accountService;
    TransactionService transactionService;

    public void transferWithInApp(@NonNull Transaction transaction) throws Exception {
        Transaction processingTransaction = transaction.toBuilder().build();

        processingTransaction.setTransaction_status(TransactionStatus.PROCESSING);
        processingTransaction.setTransaction_result("plz wait");
        transactionService.saveNewTransaction(processingTransaction);

        try{
            validateTransaction(processingTransaction);
            transferBetweenAccounts(processingTransaction);

            processingTransaction.setTransaction_status(TransactionStatus.DONE);
            processingTransaction.setTransaction_result("done");
        }
        catch (Exception e){
            processingTransaction.setTransaction_status(TransactionStatus.FAIL);
            processingTransaction.setTransaction_result(e.getMessage());
            throw e;
        }
        finally {
            transactionService.updateStatus(processingTransaction);
        }
    }

    @Transactional
    private void transferBetweenAccounts(Transaction transaction) throws Exception
    {
        System.out.println("transaction: " + transaction);

        Currency currencyTran = getSupportedCurrency(transaction.getCurrency());
        Currency currencyFrom = getAccountCurrency(transaction.getFromAcc());
        Currency currencyTo = getAccountCurrency(transaction.getToAcc());

        BigDecimal amount = transaction.getAmount();//ex: INR -> USD -> CND
        BigDecimal convertedWithdraw = exchangeApi.convert(currencyTran, currencyFrom, amount);
        BigDecimal convertedTransfer = exchangeApi.convert(currencyTran, currencyTo, amount);

        accountService.changeTotal(convertedWithdraw.negate(), transaction.getFromAcc());
        accountService.changeTotal(convertedTransfer, transaction.getToAcc());
    }

    private void validateTransaction(Transaction transaction){
        String errorMsg = null;
        if(transaction.getToAcc() == transaction.getFromAcc())
            errorMsg = "Can't do a transaction on the same account";
        else if(transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            errorMsg = "The exchange amount must be decimal and greater than 0";

        if(errorMsg != null)
            throw new BadRequestParameterException(errorMsg);
    }

    private Currency getSupportedCurrency(String rawCurrency){
        try{ return CurrencyUtil.getCurrencyFromString(rawCurrency);}
        catch (Exception e){
            throw new BadRequestParameterException(e.getMessage());
        }
    }
    private Currency getAccountCurrency(long accId){
        try{
            String rawCurrency = accountService.getAccountRawCurrency(accId);
            return CurrencyUtil.getCurrencyFromString(rawCurrency);
        }
        catch (IllegalArgumentException e){
            throw new UnprocessableContentException("Account id: " + accId + " has invalid currency");
        }
        catch (EntityNotFoundException e){
            throw new UnprocessableContentException("Account id: " + accId + " doesn't exist, Transaction cancelled");
        }
    }
}
