package omg.simple.account.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import omg.simple.account.core.util.ExchangeRateApi;

@Configuration
public class TransferResourceConfig {
    @Bean
    public ExchangeRateApi getExchangeRateApi(
        @Value("${exchangerate-api.convert-endpoint}") String convertEndpoint
    ){
        return new ExchangeRateApi(convertEndpoint);
    }
}
