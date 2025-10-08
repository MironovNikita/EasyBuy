package com.shop.easybuy.service;

import com.shop.easybuy.model.BalanceRs;
import com.shop.easybuy.model.PaymentRq;
import reactor.core.publisher.Mono;

public interface RedisService {

    Mono<BalanceRs> getBalance();

    Mono<BalanceRs> purchaseOrder(PaymentRq paymentRq);
}
