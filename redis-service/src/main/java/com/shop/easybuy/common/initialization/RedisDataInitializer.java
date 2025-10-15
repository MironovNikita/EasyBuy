package com.shop.easybuy.common.initialization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final ReactiveRedisConnectionFactory connectionFactory;

    private final ReactiveStringRedisTemplate redisTemplate;

    @Value("${balance.initial.value}")
    private int initialBalance;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        waitForRedis(Duration.ofSeconds(10))
                .doOnSuccess(v -> initializeBalance())
                .subscribe();
    }

    private Mono<Void> waitForRedis(Duration timeout) {
        return Mono.defer(() -> connectionFactory.getReactiveConnection()
                        .ping()
                        .then()
                        .doOnSuccess(l -> log.info("Redis доступен.")))
                .retryWhen(
                        Retry.backoff(20, Duration.ofMillis(200))
                                .maxBackoff(Duration.ofSeconds(1))
                                .doBeforeRetry(rs -> log.info("Ожидание Redis..."))
                )
                .timeout(timeout)
                .onErrorResume(e -> {
                    log.warn("Redis недоступен после {} секунд, инициализация пропущена.", timeout.getSeconds());
                    return Mono.empty();
                });
    }

    private void initializeBalance() {
        redisTemplate.opsForValue()
                .set("balance", String.valueOf(initialBalance))
                .then()
                .doOnSuccess(l -> log.info("Значение баланса в Redis успешно проинициализировано: {} руб.", initialBalance))
                .subscribe();
    }
}
