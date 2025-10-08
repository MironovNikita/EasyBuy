package com.shop.easybuy.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final ReactiveStringRedisTemplate redisTemplate;

    private static final String BALANCE_KEY = "balance";

    public Mono<Long> getBalance() {
        return redisTemplate
                .opsForValue()
                .get(BALANCE_KEY)
                .map(Long::parseLong);
    }

    public Mono<Long> decrementBalance(long amount) {
        return redisTemplate
                .opsForValue()
                .get(BALANCE_KEY)
                .map(Long::parseLong)
                .flatMap(current -> {
                    long newBalance = current - amount;
                    if (newBalance < 0) return Mono.error(new IllegalArgumentException("Недостаточно средств для оформления заказа"));

                    return setBalance(newBalance).thenReturn(newBalance);
                });
    }

    private Mono<Boolean> setBalance(long balance) {
        return redisTemplate
                .opsForValue()
                .set(BALANCE_KEY, String.valueOf(balance));
    }
}
