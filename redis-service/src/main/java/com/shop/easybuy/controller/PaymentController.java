package com.shop.easybuy.controller;

import com.shop.easybuy.api.payment.PaymentApi;
import com.shop.easybuy.model.payment.BalanceRs;
import com.shop.easybuy.model.payment.PaymentRq;
import com.shop.easybuy.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//TODO Разнести контроллеры по пакетам
@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;

    @Override
    public Mono<ResponseEntity<BalanceRs>> getBalance(ServerWebExchange exchange) {
        return paymentService
                .getBalance()
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<BalanceRs>> pay(@Valid @RequestBody Mono<PaymentRq> paymentRq,
                                               ServerWebExchange exchange) {

        return paymentRq
                .flatMap(paymentService::purchaseOrder)
                .map(ResponseEntity::ok);
    }
}
