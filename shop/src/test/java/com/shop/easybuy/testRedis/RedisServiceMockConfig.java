package com.shop.easybuy.testRedis;

import com.shop.easybuy.client.api.cache.CacheApi;
import com.shop.easybuy.client.api.payment.PaymentApi;
import com.shop.easybuy.client.model.cache.CacheSavedRs;
import com.shop.easybuy.client.model.payment.BalanceRs;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;

@TestConfiguration
public class RedisServiceMockConfig {

    @Bean
    @Primary
    public PaymentApi paymentApi() {
        PaymentApi paymentApi = Mockito.mock(PaymentApi.class);

        Mockito.when(paymentApi.getBalance()).thenReturn(Mono.just(new BalanceRs().balance(15000L)));
        Mockito.when(paymentApi.payWithHttpInfo(any())).thenReturn(Mono.just(ResponseEntity.ok(new BalanceRs().balance(15000L))));

        return paymentApi;
    }

    @Bean
    @Primary
    public CacheApi cacheApi() {
        CacheApi cacheApi = Mockito.mock(CacheApi.class);

        Mockito.when(cacheApi.getItemById(anyLong())).thenReturn(Mono.empty());
        Mockito.when(cacheApi.getMainItemsByParams(anyString(), any(), any(), any())).thenReturn(Flux.empty());
        Mockito.when(cacheApi.cacheItem(anyLong(), any())).thenReturn(Mono.just(new CacheSavedRs().saved(true)));
        Mockito.when(cacheApi.cacheMainItems(any(), any(), any(), any(), any())).thenReturn(Mono.just(new CacheSavedRs().saved(true)));

        return cacheApi;
    }
}
