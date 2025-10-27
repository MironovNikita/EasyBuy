package com.shop.easybuy.service;

import com.shop.easybuy.common.exception.DataNotFoundException;
import com.shop.easybuy.model.payment.PaymentRq;
import com.shop.easybuy.repository.payment.PaymentRepository;
import com.shop.easybuy.service.payment.PaymentServiceImpl;
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

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("Проверка успешного получения баланса")
    void shouldGetBalance() {
        long balance = 15000L;

        when(paymentRepository.getBalance()).thenReturn(Mono.just(balance));

        StepVerifier.create(paymentService.getBalance())
                .assertNext(balanceRs -> assertEquals(balanceRs.getBalance(), balance))
                .verifyComplete();

        verify(paymentRepository).getBalance();
    }

    @Test
    @DisplayName("Проверка получения баланса, если значение по ключу \"balance\" отсутствует")
    void shouldThrowDataNotFoundExceptionIfGetBalance() {
        when(paymentRepository.getBalance()).thenReturn(Mono.error(new DataNotFoundException("balance")));

        StepVerifier.create(paymentService.getBalance())
                .expectErrorMatches(throwable -> throwable instanceof DataNotFoundException &&
                        throwable.getMessage().equals("Данные по ключу balance не были найдены в Redis."))
                .verify();

        verify(paymentRepository).getBalance();
    }

    @Test
    @DisplayName("Проверка совершения списания с баланса")
    void shouldDecrementBalance() {
        long decremented = 10000L;
        PaymentRq paymentRq = new PaymentRq().amount(5000L);

        when(paymentRepository.decrementBalance(paymentRq.getAmount())).thenReturn(Mono.just(decremented));

        StepVerifier.create(paymentService.purchaseOrder(paymentRq))
                .assertNext(balanceRs -> assertEquals(balanceRs.getBalance(), decremented))
                .verifyComplete();

        verify(paymentRepository).decrementBalance(paymentRq.getAmount());
    }
}
