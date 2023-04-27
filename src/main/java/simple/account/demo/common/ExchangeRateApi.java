package simple.account.demo.common;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Currency;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
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
        try{ Currency.getInstance(currency); }
        catch (NullPointerException | IllegalArgumentException e){
            throw new IllegalArgumentException("Currency:["+currency+"] is invalid");
        }
    }
    private void checkIfAmountValid(BigDecimal amount){
        if(amount == null || BigDecimal.ZERO.compareTo(amount) >= 0)
            throw new IllegalArgumentException("The exchange amount must be decimal and greater than 0");
    }

}
