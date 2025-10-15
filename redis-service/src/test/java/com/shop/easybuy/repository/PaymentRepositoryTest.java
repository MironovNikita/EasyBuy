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
        when(valueOperations.get("balance")).thenReturn(Mono.just("15000"));

        StepVerifier.create(paymentRepository.getBalance())
                .assertNext(balance -> assertEquals(balance, 15000L))
                .verifyComplete();

        verify(valueOperations).get("balance");
    }

    @Test
    @DisplayName("Проверка уменьшения баланса при достаточном количестве средств")
    void shouldDecrementBalance() {
        when(valueOperations.get("balance")).thenReturn(Mono.just("15000"));
        when(valueOperations.set("balance", "5000")).thenReturn(Mono.just(true));

        StepVerifier.create(paymentRepository.decrementBalance(10000))
                .assertNext(balance -> assertEquals(balance, 5000L))
                .verifyComplete();

        verify(valueOperations).get("balance");
        verify(valueOperations).set("balance", "5000");
    }

    @Test
    @DisplayName("Проверка уменьшения баланса при недостатке средств")
    void shouldNotDecrementBalanceIfNotEnoughFunds() {
        when(valueOperations.get("balance")).thenReturn(Mono.just("1000"));

        StepVerifier.create(paymentRepository.decrementBalance(10000))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Недостаточно средств для оформления заказа"))
                .verify();

        verify(valueOperations, never()).set(anyString(), anyString());
    }

    @Test
    @DisplayName("Проверка ошибки при проблеме с Redis")
    void shouldThrowExceptionIfRedisUnavailable() {
        when(valueOperations.get("balance")).thenReturn(Mono.error(new RuntimeException("Redis недоступен")));

        StepVerifier.create(paymentRepository.getBalance())
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Redis недоступен"))
                .verify();

        verify(valueOperations, never()).set(anyString(), anyString());
    }
}
