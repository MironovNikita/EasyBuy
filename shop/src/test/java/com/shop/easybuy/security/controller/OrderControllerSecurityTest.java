package com.shop.easybuy.security.controller;

import com.shop.easybuy.entity.order.OrderItemDto;
import com.shop.easybuy.entity.order.OrderRsDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

public class OrderControllerSecurityTest extends CommonSecurityTest {

    @Test
    @WithAnonymousUser
    @DisplayName("Проверка отсутствия доступа к покупке неавторизованного пользователя")
    void shouldNotAccessBuyIfNotAuthenticated() {

        webClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/login");
    }

    @Test
    @WithMockUser
    @DisplayName("Проверка наличия доступа к покупке авторизованного пользователя")
    void shouldAccessBuyIfAuthenticated() {
        Long userId = 50L;
        Long orderId = 1L;
        OrderItemDto orderItemDto = new OrderItemDto(1L, 1L, "t", "t", "i", 5000L, 1L);
        OrderRsDto orderRsDto = new OrderRsDto(orderId, 5000L, LocalDateTime.now(), List.of(orderItemDto));

        when(securityUserContextHandler.getCurrentUserId()).thenReturn(Mono.just(userId));
        when(orderService.buyItemsInCartByUserId(userId)).thenReturn(Mono.just(orderRsDto));

        webClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/orders/%d?newOrder=true".formatted(orderId));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Проверка отсутствия доступа к получению всех заказов у неавторизованного пользователя")
    void shouldNotShowOrdersIfUserNotAuthenticated() {
        webClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/login");
    }

    @Test
    @WithMockUser
    @DisplayName("Проверка наличия доступа к получению всех заказов у авторизованного пользователя")
    void shouldShowOrdersIfUserAuthenticated() {
        Long userId = 50L;
        Long orderId = 1L;
        OrderItemDto orderItemDto = new OrderItemDto(1L, 1L, "t", "t", "i", 5000L, 1L);
        OrderRsDto orderRsDto = new OrderRsDto(orderId, 5000L, LocalDateTime.now(), List.of(orderItemDto));

        when(securityUserContextHandler.getCurrentUserId()).thenReturn(Mono.just(userId));
        when(orderService.findAllByUserId(userId)).thenReturn(Flux.just(orderRsDto));

        webClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> assertThat(result.getResponseBody()).contains("orders"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Проверка отказа отображения заказа для неавторизованного пользователя")
    void shouldNotShowOrderIfUserNotAuthenticated() {
        Long orderId = 1L;
        webClient.get()
                .uri("/orders/{id}", orderId)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/login");
    }

    @Test
    @WithMockUser
    @DisplayName("Проверка отказа отображения заказа для неавторизованного пользователя")
    void shouldShowOrderIfUserAuthenticated() {
        Long orderId = 1L;
        Long userId = 50L;
        OrderItemDto orderItemDto = new OrderItemDto(1L, 1L, "t", "t", "i", 5000L, 1L);
        OrderRsDto orderRsDto = new OrderRsDto(orderId, 5000L, LocalDateTime.now(), List.of(orderItemDto));

        when(securityUserContextHandler.getCurrentUserId()).thenReturn(Mono.just(userId));
        when(orderService.findByIdAndUserId(orderId, userId)).thenReturn(Mono.just(orderRsDto));

        webClient.get()
                .uri("/orders/{id}", orderId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> assertThat(result.getResponseBody()).contains("order"));
    }
}
