package com.shop.easybuy.controller.payment;

import com.shop.easybuy.api.payment.PaymentApi;
import com.shop.easybuy.model.payment.BalanceRs;
import com.shop.easybuy.model.payment.BalanceSetRq;
import com.shop.easybuy.model.payment.PaymentRq;
import com.shop.easybuy.service.payment.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;

    @Override
    public Mono<ResponseEntity<BalanceRs>> getBalance(
            @NotNull @Valid @RequestParam(value = "userId", required = true) Long userId,
            ServerWebExchange exchange
    ) {
        return paymentService
                .getBalance(userId)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<BalanceRs>> pay(@Valid @RequestBody Mono<PaymentRq> paymentRq,
                                               ServerWebExchange exchange) {

        return paymentRq
                .flatMap(paymentService::purchaseOrder)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Boolean>> setBalance(@Valid @RequestBody Mono<BalanceSetRq> balanceSetRq,
                                                    ServerWebExchange exchange) {
        return balanceSetRq
                .flatMap(paymentService::setBalance)
                .map(ResponseEntity::ok);
    }
}
