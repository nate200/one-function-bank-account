package simple.account.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import simple.account.demo.util.ExchangeRateApi;

@Configuration
public class TransferResourceConfig {
    @Bean
    public ExchangeRateApi getExchangeRateApi(
        @Value("${exchangerate-api.convert-endpoint}") String convertEndpoint
    ){
        return new ExchangeRateApi(convertEndpoint);
    }
}
