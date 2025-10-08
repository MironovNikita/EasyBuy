package com.shop.easybuy.controller;

import com.shop.easybuy.api.PaymentApi;
import com.shop.easybuy.model.BalanceRs;
import com.shop.easybuy.model.PaymentRq;
import com.shop.easybuy.service.RedisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class RedisController implements PaymentApi {

    private final RedisService redisService;

    @Override
    public Mono<ResponseEntity<BalanceRs>> getBalance(ServerWebExchange exchange) {
        return redisService.getBalance()
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<BalanceRs>> pay(@Valid @RequestBody Mono<PaymentRq> paymentRq,
                                               ServerWebExchange exchange) {

        return paymentRq
                .flatMap(redisService::purchaseOrder)
                .map(ResponseEntity::ok);
    }
}
