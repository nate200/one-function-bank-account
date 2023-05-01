package simple.account.demo.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Currency;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyUtilTest {

    @ParameterizedTest
    @MethodSource("validRawCurrency")
    void convert_rawStringToCHF_currency(String rawCurrency){
        Currency CHF = Currency.getInstance("CHF");
        Currency actual = CurrencyUtil.getCurrencyFromString(rawCurrency);
        assertEquals(actual.getCurrencyCode(), CHF.getCurrencyCode());
    }
    @ParameterizedTest
    @MethodSource("badRawCurrency")
    void convert_railOnBadRawCurrency(String rawCurrency){
        assertThrows(
            IllegalArgumentException.class,
            () -> CurrencyUtil.getCurrencyFromString(rawCurrency)
        );
    }

    static Stream<String> validRawCurrency(){
        return Stream.of("CHF", "CHF   ", "   CHF", "     CHF     ", "chf", "chf   ", "   chf", "     chf     ");
    }
    static Stream<String> badRawCurrency(){
        return Stream.of(null, "55555", "CH F", "   CFH", "T H B", "C.HF");
    }
}