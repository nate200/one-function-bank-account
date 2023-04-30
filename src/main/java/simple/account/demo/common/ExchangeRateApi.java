package simple.account.demo.common;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;

import static java.math.BigDecimal.ZERO;

@Getter
@AllArgsConstructor
public class ExchangeRateApi {

    final String CONVERT_ENDPOINT;

    public BigDecimal convert(String base, String target, BigDecimal amount) throws IllegalArgumentException, IOException
    {
        checkIfCurrencyValid(base);
        checkIfCurrencyValid(target);
        checkIfAmountValid(amount);

        if(base.equals(target))
            return amount;

        String url_str = CONVERT_ENDPOINT + base + "/" + target + "/" + amount;
        JsonObject jsonObject = ConnectionUtil.getJsonObjFromUrl(url_str);

        return jsonObject.get("conversion_result").getAsBigDecimal();
    }
    private void checkIfCurrencyValid(String currency){
        try{ Currency.getInstance(currency.trim().toUpperCase()); }
        catch (NullPointerException | IllegalArgumentException e){
            throw new IllegalArgumentException("Currency ["+currency+"] is invalid");
        }
    }
    private void checkIfAmountValid(BigDecimal amount){
        if(amount == null || 0 <= ZERO.compareTo(amount))
            throw new IllegalArgumentException("The exchange amount must be decimal and greater than 0");
    }

}
