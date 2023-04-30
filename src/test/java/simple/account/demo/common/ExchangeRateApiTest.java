package simple.account.demo.common;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateApiTest {
    @Mock
    HttpURLConnection request;
    @InjectMocks
    ExchangeRateApi exchangeRateApi = new ExchangeRateApi("https://www.dummy.com/");

    static final String THB = "THB";
    static final String USD = "USD";

    @ParameterizedTest
    @MethodSource("validCurrencies")
    void convert(String base, String target) throws IOException {
        BigDecimal expected = BigDecimal.TEN;
        JsonObject jsonObj = mockJsonObj(expected);

        //https://stackoverflow.com/questions/52830441/junit5-mock-a-static-method
        //https://www.davidvlijmincx.com/posts/mockito_mock_static_method/
        try (MockedStatic<ConnectionUtil> mocked = mockStatic(ConnectionUtil.class))
        {
            mocked.when(() -> ConnectionUtil.getJsonObjFromUrl(any(String.class)))
                    .thenReturn(jsonObj);

            BigDecimal actual = exchangeRateApi.convert(base, target, expected);

            assertEquals(0, expected.compareTo(actual));
        }
    }
    @Test
    void convert_sameCurrencies() throws IOException {
        BigDecimal expectedAmount = BigDecimal.TEN;

        BigDecimal actualAmount = exchangeRateApi.convert(THB, THB, expectedAmount);

        assertEquals(0, expectedAmount.compareTo(actualAmount));
        verify(request, never()).getInputStream();
    }
    @Disabled("use test method: [ExchangeRateApiTest.test_convert_mock_static()] instead")
    @Test
    void test_convert_mock_http() throws IOException {
        BigDecimal expected = BigDecimal.TEN;
        InputStream jsonIs = mockJsonInputStream(expected);
        given(request.getInputStream()).willReturn(jsonIs);

        BigDecimal actual = exchangeRateApi.convert(THB, USD, expected);

        assertEquals(0, expected.compareTo(actual));
    }

    @ParameterizedTest
    @MethodSource("invalidCurrencies")
    void test_convert_invalidBaseCurrency(String invalidCurrency) throws IOException {
        assertThrows(
            IllegalArgumentException.class,
            () -> exchangeRateApi.convert(invalidCurrency, USD, BigDecimal.TEN)
        );
        verify(request, never()).getInputStream();
    }
    @ParameterizedTest
    @MethodSource("invalidCurrencies")
    void test_convert_invalidTargetCurrency(String invalidCurrency) throws IOException {
        assertThrows(
            IllegalArgumentException.class,
            () -> exchangeRateApi.convert(USD, invalidCurrency, BigDecimal.TEN)
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
    private InputStream mockJsonInputStream(Object obj){
        String json = "{\"conversion_result\" : " + obj + "}";
        return new ByteArrayInputStream(json.getBytes());
    }
    static Stream<Arguments> validCurrencies(){
        return Stream.of(
                arguments(THB, USD),
                arguments("    THB    ", USD),
                arguments(THB, "      CHF"),
                arguments("thb", USD),
                arguments(THB, "usd"),
                arguments("    thb   ", USD),
                arguments(THB, "    usd   ")
        );
    }
    static Stream<String> invalidCurrencies() {
        return Stream.of("TH B", "555555+", "", " ", null);
    }
    static Stream<BigDecimal> invalidAmounts() {
        return Stream.of(BigDecimal.ZERO, BigDecimal.TEN.negate(), null);
    }
}
