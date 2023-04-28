package simple.account.demo.config;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import simple.account.demo.common.ExchangeRateApi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class TransferResourceConfigTest {
    @Autowired
    ExchangeRateApi exchangeRateApi;

    @Test
    public void test_Autowired(@Value("${exchangerate-api.convert-endpoint}") String endPoint){
        assertNotNull(exchangeRateApi);
        System.out.println(endPoint);
        System.out.println(exchangeRateApi.getCONVERT_ENDPOINT());
        assertEquals(endPoint, exchangeRateApi.getCONVERT_ENDPOINT());
    }
}
