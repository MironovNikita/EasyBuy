package com.shop.easybuy.service;

import com.shop.easybuy.common.exception.DataNotFoundException;
import com.shop.easybuy.model.BalanceRs;
import com.shop.easybuy.model.PaymentRq;
import com.shop.easybuy.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisRepository redisRepository;

    public Mono<BalanceRs> getBalance() {

        return redisRepository
                .getBalance()
                .switchIfEmpty(Mono.error(new DataNotFoundException("balance")))
                .map(b -> new BalanceRs().balance(b));
    }

    public Mono<BalanceRs> purchaseOrder(PaymentRq paymentRq) {
        return redisRepository
                .decrementBalance(paymentRq.getAmount())
                .map(s -> new BalanceRs().balance(s));
    }
}
