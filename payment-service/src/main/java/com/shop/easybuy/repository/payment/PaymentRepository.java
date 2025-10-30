package com.shop.easybuy.repository.payment;

import reactor.core.publisher.Mono;

public interface PaymentRepository {

    Mono<Long> getBalance(Long userId);

    Mono<Long> decrementBalance(Long userId, Long amount);

    Mono<Boolean> setBalance(Long userId, Long balance);
}
