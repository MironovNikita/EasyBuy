package com.shop.easybuy.service;

import com.shop.easybuy.common.exception.DataNotFoundException;
import com.shop.easybuy.model.payment.PaymentRq;
import com.shop.easybuy.repository.payment.PaymentRepository;
import com.shop.easybuy.service.payment.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("Проверка успешного получения баланса")
    void shouldGetBalance() {
        Long balance = 15000L;
        Long userId = 1L;

        when(paymentRepository.getBalance(userId)).thenReturn(Mono.just(balance));

        StepVerifier.create(paymentService.getBalance(userId))
                .assertNext(balanceRs -> assertEquals(balanceRs.getBalance(), balance))
                .verifyComplete();

        verify(paymentRepository).getBalance(userId);
    }

    @Test
    @DisplayName("Проверка получения баланса, если значение по ключу \"balance\" отсутствует")
    void shouldThrowDataNotFoundExceptionIfGetBalance() {
        Long userId = 1L;
        when(paymentRepository.getBalance(userId)).thenReturn(Mono.error(new DataNotFoundException("balance:" + userId)));

        StepVerifier.create(paymentService.getBalance(userId))
                .expectErrorMatches(throwable -> throwable instanceof DataNotFoundException &&
                        throwable.getMessage().equals("Данные по ключу balance:1 не были найдены в Redis."))
                .verify();

        verify(paymentRepository).getBalance(userId);
    }

    @Test
    @DisplayName("Проверка совершения списания с баланса")
    void shouldDecrementBalance() {
        Long decremented = 10000L;
        PaymentRq paymentRq = new PaymentRq().userId(1L).amount(5000L);

        when(paymentRepository.decrementBalance(paymentRq.getUserId(), paymentRq.getAmount())).thenReturn(Mono.just(decremented));

        StepVerifier.create(paymentService.purchaseOrder(paymentRq))
                .assertNext(balanceRs -> assertEquals(balanceRs.getBalance(), decremented))
                .verifyComplete();

        verify(paymentRepository).decrementBalance(paymentRq.getUserId(), paymentRq.getAmount());
    }
}
