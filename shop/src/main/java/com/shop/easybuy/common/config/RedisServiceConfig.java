package com.shop.easybuy.common.config;

import com.shop.easybuy.client.api.PaymentApi;
import com.shop.easybuy.client.invoker.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisServiceConfig {

    @Bean
    public ApiClient apiClient() {
        return new ApiClient()
                .setBasePath("http://localhost:8081");
        //TODO Возможно не сработает на Docker!
    }

    @Bean
    public PaymentApi paymentApi() {
        return new PaymentApi(apiClient());
    }
}
