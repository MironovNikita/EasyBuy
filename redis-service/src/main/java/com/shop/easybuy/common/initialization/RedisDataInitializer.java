package com.shop.easybuy.common.initialization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisDataInitializer implements ApplicationRunner {

    private final ReactiveStringRedisTemplate redisTemplate;

    @Value("${balance.initial.value}")
    private int initialBalance;

    @Override
    public void run(ApplicationArguments args) {
        redisTemplate.opsForValue()
                .set("balance", String.valueOf(initialBalance))
                .then()
                .doOnSuccess(l -> log.info("Значение баланса в Redis успешно проинициализировано: {} руб.", initialBalance))
                .subscribe();
    }
}
