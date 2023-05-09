package omg.simple.account.core.util;

import java.util.Currency;

public class CurrencyUtil {
    public static Currency getCurrencyFromString(String rawCurrency){
        try{
            return Currency.getInstance(rawCurrency.trim().toUpperCase());
        }
        catch (NullPointerException | IllegalArgumentException e){
            throw new IllegalArgumentException("Currency["+rawCurrency+"] is invalid or not supported by ISO 4217 standard");
        }
    }
}
