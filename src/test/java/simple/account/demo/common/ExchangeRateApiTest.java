package simple.account.demo.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ExchangeRateApiTest {
    @Value("${exchangerate-api.convert-endpoint}")
    String CONVERT_ENDPOINT;
    @Autowired
    ExchangeRateApi exchangeRateApi;
    final String THB = "THB";
    final String USD = "USD";

    @Test
    void test_sameCurrencies() throws IOException {
        BigDecimal amount = BigDecimal.TEN;

        BigDecimal convertedAmountAPI = exchangeRateApi.convert(THB, THB, amount);
        BigDecimal convertedAmount = convert(THB, THB, amount);

        assertEquals(0, amount.compareTo(convertedAmount));
        assertEquals(0, convertedAmountAPI.compareTo(convertedAmount));
    }

    @Test
    void test_convert() throws IOException {
        BigDecimal amount = BigDecimal.TEN;

        BigDecimal convertedAmountAPI = exchangeRateApi.convert(THB, USD, amount);
        BigDecimal convertedAmount = convert(THB, USD, amount);

        assertEquals(0, convertedAmount.compareTo(convertedAmountAPI));
    }

    @ParameterizedTest
    @MethodSource("invalidCurrencies")
    void test_convert_invalidCurrency(String invalidCurrency){
        IllegalArgumentException error = assertThrows(
            IllegalArgumentException.class,
            () -> exchangeRateApi.convert(invalidCurrency, USD, BigDecimal.TEN)
        );
        //assertThat(error.getMessage(), containsString(invalidCurrency)); //can't test null
    }

    @ParameterizedTest
    @MethodSource("invalidAmounts")
    void test_convert_invalidAmount(BigDecimal amount) throws Exception {
        String errMsg = "The exchange amount must be greater than 0";

        IllegalArgumentException error = assertThrows(
            IllegalArgumentException.class,
            () -> exchangeRateApi.convert(THB, USD, amount)
        );

        assertEquals(error.getMessage(), errMsg);
    }

    private BigDecimal convert(String base, String target, BigDecimal amount) throws IOException {
        if(base.equals(target))
            return amount;

        String url_str = CONVERT_ENDPOINT + base + "/" + target + "/" + amount;

        URL url = new URL(url_str);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        BigDecimal result;
        try (
            InputStream is = (InputStream) request.getContent();
            InputStreamReader inr = new InputStreamReader(is)
        ) {
            JsonElement jsonElement = JsonParser.parseReader(inr);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            result = jsonObject.get("conversion_result").getAsBigDecimal();
        }

        return result;
    }

    static Stream<String> invalidCurrencies() {
        return Stream.of("TH B", "555555", "", " ", null);
    }
    static Stream<BigDecimal> invalidAmounts() {
        return Stream.of(BigDecimal.ZERO, BigDecimal.TEN.negate(), null);
    }
}
