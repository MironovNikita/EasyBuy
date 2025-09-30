package com.shop.easybuy.controller;

import com.shop.easybuy.entity.cart.CartItem;
import com.shop.easybuy.entity.order.Order;
import com.shop.easybuy.entity.order.OrderItem;
import com.shop.easybuy.entity.order.OrderItemDto;
import com.shop.easybuy.entity.order.OrderRsDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.shop.easybuy.DataInserter.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Оформление покупки товаров в корзине")
    void shouldCreateOrder() {
        Long orderId = 1L;

        insertIntoCartTable(databaseClient, List.of(
                new CartItem(1L, 3),
                new CartItem(2L, 5)
        )).block();

        webClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/orders/%d?newOrder=true".formatted(orderId));

        OrderRsDto order = orderService.findById(orderId).block();
        assertNotNull(order);
        assertEquals(2L, order.getItems().size());
        assertEquals(18500L, order.getTotal());
        assertTrue(order.getItems().stream().map(OrderItemDto::getItemId).toList().containsAll(List.of(1L, 2L)));
    }

    @Test
    @DisplayName("Ошибка при оформлении покупки с пустой корзиной")
    void shouldThrowCartEmptyExceptionsIfCartIsEmpty() {

        webClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(String.class)
                .consumeWith(result -> {
                    String body = result.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Невозможно оформить заказ: корзина пуста!"));
                });
    }

    @Test
    @DisplayName("Показать все заказы, если их ещё не было")
    void shouldShowOrdersIfOrdersIsEmpty() {

        webClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    String body = result.getResponseBody();
                    assertNotNull(body);
                    assertFalse(body.contains("Заказ №"));
                });
    }

    @Test
    @DisplayName("Показать все заказы")
    void shouldShowOrders() {

        insertIntoOrdersTable(databaseClient, List.of(
                new Order().setId(1L).setTotal(5000L).setCreated(LocalDateTime.parse("2025-09-14T21:56:39.047928")),
                new Order().setId(2L).setTotal(10000L).setCreated(LocalDateTime.parse("2025-09-14T21:56:39.047928"))
        )).block();

        insertIntoOrderItemsTable(databaseClient, List.of(
                new OrderItem(1L, 1L, 1L, 1L),
                new OrderItem(2L, 2L, 1L, 2L)
        )).block();

        webClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    String body = result.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Заказ №1"));
                    assertTrue(body.contains("Заказ №2"));
                });

        List<OrderRsDto> orders = orderService.findAll().collectList().block();
        assertNotNull(orders);
        assertEquals(2, orders.size());
        var totals = orders.stream().map(OrderRsDto::getTotal).toList();
        assertTrue(totals.contains(5000L));
        assertTrue(totals.contains(10000L));
        assertTrue(orders.stream().noneMatch(o -> o.getItems().isEmpty()));
    }

    @Test
    @DisplayName("Получение заказа по его ID")
    void shouldFindOrderById() {
        Long orderId = 1L;

        insertIntoOrdersTable(databaseClient, List.of(
                new Order().setId(orderId).setTotal(5000L).setCreated(LocalDateTime.parse("2025-09-14T21:56:39.047928"))
        )).block();

        insertIntoOrderItemsTable(databaseClient, List.of(
                new OrderItem(1L, 1L, 1L, 1L)
        )).block();

        webClient.get()
                .uri("/orders/%d".formatted(orderId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    String body = result.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Майка чёрная М"));
                    assertTrue(body.contains("1 шт."));
                    assertTrue(body.contains("5000 руб."));
                });

        OrderRsDto order = orderService.findById(orderId).block();
        assertNotNull(order);
        assertEquals(1L, order.getItems().size());
        assertEquals(5000L, order.getTotal());
    }

    @Test
    @DisplayName("Выброс исключения, если заказ не найден")
    void shouldThrowObjectNotFoundExceptionIfOrderNotExists() {
        long nonExistingOrderId = 1L;

        webClient.get()
                .uri("/orders/%d".formatted(nonExistingOrderId))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .consumeWith(result -> {
                    String body = result.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Заказ с ID: 1 не найден!"));
                });
    }
}
