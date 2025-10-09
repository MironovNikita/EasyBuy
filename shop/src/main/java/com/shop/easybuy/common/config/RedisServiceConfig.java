package com.shop.easybuy.common.config;

import com.shop.easybuy.client.api.cache.CacheApi;
import com.shop.easybuy.client.api.payment.PaymentApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisServiceConfig {

    @Value("${base.client.payment.url}")
    private String basePaymentUrl;

    @Value("${base.client.cache.url}")
    private String baseCacheUrl;

    @Bean
    public com.shop.easybuy.client.invoker.payment.ApiClient paymentApiClient() {
        return new com.shop.easybuy.client.invoker.payment.ApiClient()
                .setBasePath(basePaymentUrl);
        //TODO Возможно не сработает на Docker!
    }

    @Bean
    public PaymentApi paymentApi() {
        return new PaymentApi(paymentApiClient());
    }

    @Bean
    public com.shop.easybuy.client.invoker.cache.ApiClient cacheApiClient() {
        return new com.shop.easybuy.client.invoker.cache.ApiClient()
                .setBasePath(baseCacheUrl);
        //TODO Возможно не сработает на Docker!
    }

    @Bean
    public CacheApi cacheApi() {
        return new CacheApi(cacheApiClient());
    }
}
