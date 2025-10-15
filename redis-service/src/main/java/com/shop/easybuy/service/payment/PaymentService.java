package com.shop.easybuy.service.payment;

import com.shop.easybuy.model.payment.BalanceRs;
import com.shop.easybuy.model.payment.PaymentRq;
import reactor.core.publisher.Mono;

public interface PaymentService {

    Mono<BalanceRs> getBalance();

    Mono<BalanceRs> purchaseOrder(PaymentRq paymentRq);
}
