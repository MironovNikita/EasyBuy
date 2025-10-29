package com.shop.easybuy.repository;

import com.shop.easybuy.repository.payment.PaymentRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentRepositoryTest {

    @Mock
    private ReactiveStringRedisTemplate redisTemplate;

    @Mock
    private ReactiveValueOperations<String, String> valueOperations;

    @InjectMocks
    private PaymentRepositoryImpl paymentRepository;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("Проверка получения баланса из кеша")
    void shouldGetBalance() {
        Long userId = 1L;
        when(valueOperations.get("balance:" + userId)).thenReturn(Mono.just("15000"));

        StepVerifier.create(paymentRepository.getBalance(userId))
                .assertNext(balance -> assertEquals(balance, 15000L))
                .verifyComplete();

        verify(valueOperations).get("balance:" + userId);
    }

    @Test
    @DisplayName("Проверка уменьшения баланса при достаточном количестве средств")
    void shouldDecrementBalance() {
        Long userId = 1L;
        when(valueOperations.get("balance:" + userId)).thenReturn(Mono.just("15000"));
        when(valueOperations.set("balance:" + userId, "5000")).thenReturn(Mono.just(true));

        StepVerifier.create(paymentRepository.decrementBalance(userId, 10000L))
                .assertNext(balance -> assertEquals(balance, 5000L))
                .verifyComplete();

        verify(valueOperations).get("balance:" + userId);
        verify(valueOperations).set("balance:" + userId, "5000");
    }

    @Test
    @DisplayName("Проверка уменьшения баланса при недостатке средств")
    void shouldNotDecrementBalanceIfNotEnoughFunds() {
        Long userId = 1L;
        when(valueOperations.get("balance:" + userId)).thenReturn(Mono.just("1000"));

        StepVerifier.create(paymentRepository.decrementBalance(userId, 10000L))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Недостаточно средств для оформления заказа"))
                .verify();

        verify(valueOperations, never()).set(anyString(), anyString());
    }

    @Test
    @DisplayName("Проверка ошибки при проблеме с Redis")
    void shouldThrowExceptionIfRedisUnavailable() {
        Long userId = 1L;
        when(valueOperations.get("balance:" + userId)).thenReturn(Mono.error(new RuntimeException("Redis недоступен")));

        StepVerifier.create(paymentRepository.getBalance(userId))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Redis недоступен"))
                .verify();

        verify(valueOperations, never()).set(anyString(), anyString());
    }
}
