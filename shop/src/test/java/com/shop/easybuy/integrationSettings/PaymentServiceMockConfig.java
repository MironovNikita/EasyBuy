package com.shop.easybuy.integrationSettings;

import com.shop.easybuy.client.api.payment.PaymentApi;
import com.shop.easybuy.client.model.payment.BalanceRs;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@TestConfiguration
public class PaymentServiceMockConfig {

    @Bean
    @Primary
    public PaymentApi paymentApi() {
        PaymentApi paymentApi = Mockito.mock(PaymentApi.class);

        Mockito.when(paymentApi.getBalance(anyLong())).thenReturn(Mono.just(new BalanceRs().balance(20000L)));
        Mockito.when(paymentApi.payWithHttpInfo(any())).thenReturn(Mono.just(ResponseEntity.ok(new BalanceRs().balance(20000L))));
        Mockito.when(paymentApi.setBalance(any())).thenReturn(Mono.just(true));

        return paymentApi;
    }
}
