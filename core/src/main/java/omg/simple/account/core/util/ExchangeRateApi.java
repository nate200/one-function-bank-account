package omg.simple.account.core.util;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Currency;

import static java.math.BigDecimal.ZERO;

@Getter
@AllArgsConstructor
public class ExchangeRateApi {

    final String CONVERT_ENDPOINT;

    public BigDecimal convert(@NonNull Currency base, @NonNull Currency target, BigDecimal amount) throws Exception
    {
        checkIfAmountValid(amount);

        if(base.equals(target))
            return amount;

        String url_str = CONVERT_ENDPOINT + base.getCurrencyCode() + "/" + target.getCurrencyCode() + "/" + amount;
        JsonObject jsonObject = ConnectionUtil.getJsonObjFromUrl(url_str);
        return jsonObject.get("conversion_result").getAsBigDecimal();
    }
    private void checkIfAmountValid(BigDecimal amount){
        if(amount == null || 0 <= ZERO.compareTo(amount))
            throw new IllegalArgumentException("The exchange amount must be decimal and greater than 0");
    }
}
