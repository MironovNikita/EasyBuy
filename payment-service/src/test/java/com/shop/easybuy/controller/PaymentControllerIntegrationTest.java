package com.shop.easybuy.controller;

import com.shop.easybuy.model.payment.BalanceRs;
import com.shop.easybuy.model.payment.ErrorRs;
import com.shop.easybuy.model.payment.PaymentRq;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.BodyInserters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PaymentControllerIntegrationTest extends BaseIntegrationTest {

    @AfterEach
    void restoreBalance() {
        redisTemplate.opsForValue()
                .set("balance", "15000")
                .block();
    }

    @Test
    @DisplayName("Проверка получения баланса")
    void shouldGetBalance() {

        webClient.get()
                .uri("/balance")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BalanceRs.class)
                .consumeWith(response -> {
                    assertNotNull(response.getResponseBody());
                    assertEquals(response.getResponseBody().getBalance(), 15000L);
                });
    }

    @Test
    @DisplayName("Получение баланса с ошибкой, если такого ключа не будет найдено")
    void shouldNotGetBalance() {

        redisTemplate.opsForValue()
                .delete("balance")
                .subscribe();

        webClient.get()
                .uri("/balance")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorRs.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    assertNotNull(body);
                    assertEquals("404", body.getErrorCode());
                });
    }

    @Test
    @DisplayName("Проверка оплаты и списания баланса")
    void shouldPayAndDecrementBalance() {

        webClient.post()
                .uri("/pay")
                .body(BodyInserters.fromValue(new PaymentRq().amount(5000L)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(BalanceRs.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    assertNotNull(body);
                    assertEquals(response.getResponseBody().getBalance(), 10000L);
                });
    }

    @Test
    @DisplayName("Проверка оплаты и списания баланса при недостаточном балансе")
    void shouldNotPayAndDecrementBalanceIfNotEnoughFunds() {

        webClient.post()
                .uri("/pay")
                .body(BodyInserters.fromValue(new PaymentRq().amount(50000L)))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorRs.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    assertNotNull(body);
                    assertEquals("402", body.getErrorCode());
                });
    }

    @Test
    @DisplayName("Проверка оплаты и списания баланса при отсутствии значения баланса")
    void shouldNotPayAndDecrementBalanceIfBalanceNotExists() {

        redisTemplate.opsForValue()
                .delete("balance")
                .subscribe();

        webClient.post()
                .uri("/pay")
                .body(BodyInserters.fromValue(new PaymentRq().amount(50000L)))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorRs.class)
                .consumeWith(response -> {
                    var body = response.getResponseBody();
                    assertNotNull(body);
                    assertEquals("404", body.getErrorCode());
                });
    }
}
