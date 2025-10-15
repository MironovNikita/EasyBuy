package com.shop.easybuy.common.config;

import com.shop.easybuy.client.api.cache.CacheApi;
import com.shop.easybuy.client.api.payment.PaymentApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class RedisServiceConfig {

    @Value("${base.client.redis.url}")
    private String baseRedisUrl;

    @Bean
    public com.shop.easybuy.client.invoker.payment.ApiClient paymentApiClient() {
        return new com.shop.easybuy.client.invoker.payment.ApiClient()
                .setBasePath(baseRedisUrl);
    }

    @Bean
    public PaymentApi paymentApi() {
        return new PaymentApi(paymentApiClient());
    }

    @Bean
    public com.shop.easybuy.client.invoker.cache.ApiClient cacheApiClient() {
        return new com.shop.easybuy.client.invoker.cache.ApiClient()
                .setBasePath(baseRedisUrl);
    }

    @Bean
    public CacheApi cacheApi() {
        return new CacheApi(cacheApiClient());
    }
}
