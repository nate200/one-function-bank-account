package omg.simple.account.core.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.util.Currency;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateApiTest {
    @Mock
    HttpURLConnection request;
    @InjectMocks
    ExchangeRateApi exchangeRateApi = new ExchangeRateApi("https://www.dummy.com/");

    static final Currency THB = Currency.getInstance("THB");
    static final Currency USD = Currency.getInstance("USD");

    @Test
    void convert() throws Exception {
        BigDecimal expected = BigDecimal.TEN;
        JsonObject jsonObj = mockJsonObj(expected);

        //https://stackoverflow.com/questions/52830441/junit5-mock-a-static-method
        //https://www.davidvlijmincx.com/posts/mockito_mock_static_method/
        try (MockedStatic<ConnectionUtil> mocked = mockStatic(ConnectionUtil.class))
        {
            mocked.when(() -> ConnectionUtil.getJsonObjFromUrl(any(String.class)))
                    .thenReturn(jsonObj);

            BigDecimal actual = exchangeRateApi.convert(THB, USD, expected);

            assertEquals(0, expected.compareTo(actual));
        }
    }
    @Test
    void convert_sameCurrencies() throws Exception {
        BigDecimal expectedAmount = BigDecimal.TEN;

        BigDecimal actualAmount = exchangeRateApi.convert(THB, THB, expectedAmount);

        assertEquals(0, expectedAmount.compareTo(actualAmount));
        verify(request, never()).getInputStream();
    }


    @ParameterizedTest
    @MethodSource("invalidCurrenciesSet")
    void test_convert_invalidCurrencyParameter(Currency invalidCurrencyBase, Currency invalidCurrencyTarget) throws Exception {
        assertThrows(
            NullPointerException.class,
            () -> exchangeRateApi.convert(invalidCurrencyBase, invalidCurrencyTarget, BigDecimal.TEN)
        );
        verify(request, never()).getInputStream();
    }
    @ParameterizedTest
    @MethodSource("invalidAmounts")
    void test_convert_invalidAmount(BigDecimal amount) throws Exception {
        String errMsg = "The exchange amount must be decimal and greater than 0";

        IllegalArgumentException error = assertThrows(
            IllegalArgumentException.class,
            () -> exchangeRateApi.convert(THB, USD, amount)
        );

        assertEquals(errMsg, error.getMessage());
        verify(request, never()).getInputStream();
    }
    
    private JsonObject mockJsonObj(Object obj){
        String json = "{\"conversion_result\" : " + obj + "}";
        return JsonParser.parseString(json).getAsJsonObject();
    }
    static Stream<Arguments> invalidCurrenciesSet() {
        return Stream.of(
                arguments(THB,null),
                arguments(null,USD),
                arguments(null,null)
        );
    }
    static Stream<BigDecimal> invalidAmounts() {
        return Stream.of(BigDecimal.ZERO, BigDecimal.TEN.negate(), null);
    }
}
