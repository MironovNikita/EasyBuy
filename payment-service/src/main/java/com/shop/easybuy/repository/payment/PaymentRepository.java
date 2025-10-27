package com.shop.easybuy.repository.payment;

import reactor.core.publisher.Mono;

public interface PaymentRepository {

    Mono<Long> getBalance();

    Mono<Long> decrementBalance(long amount);
}
