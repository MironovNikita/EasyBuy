package com.shop.easybuy.common.initialization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisDataInitializer implements ApplicationRunner {

    private final ReactiveRedisConnectionFactory connectionFactory;

    private final ReactiveStringRedisTemplate redisTemplate;

    @Value("${balance.initial.value}")
    private int initialBalance;

    @Override
    public void run(ApplicationArguments args) {

        try {
            connectionFactory.getReactiveConnection()
                    .ping()
                    .timeout(Duration.ofSeconds(5))
                    .block();
            log.info("Соединение с Redis успешно установлено.");
        } catch (Exception e) {
            log.error("Redis недоступен, приложение будет завершено.", e);
            System.exit(1);
        }

        redisTemplate.opsForValue()
                .set("balance", String.valueOf(initialBalance))
                .then()
                .doOnSuccess(l -> log.info("Значение баланса в Redis успешно проинициализировано: {} руб.", initialBalance))
                .subscribe();
    }
}
