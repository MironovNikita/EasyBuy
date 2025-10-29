package com.shop.easybuy.controller;

import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.entity.cart.CartItem;
import com.shop.easybuy.entity.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;

import static com.shop.easybuy.DataCreator.createUser;
import static com.shop.easybuy.DataInserter.insertIntoCartTable;
import static com.shop.easybuy.DataInserter.insertIntoUserTable;
import static org.junit.jupiter.api.Assertions.*;

public class CartControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Отображение товаров в корзине")
    void shouldReturnEmptyListIfCartIsNotEmpty() {
        Long userId = 1L;
        User user = createUser(userId);
        insertIntoUserTable(databaseClient, user).block();

        insertIntoCartTable(databaseClient, List.of(
                new CartItem(1L, 3, userId),
                new CartItem(2L, 5, userId)
        )).block();

        webClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertNotNull(result.getResponseBody());
                    assertTrue(result.getResponseBody().contains("Итого: 18500"));
                });
    }

    @Test
    @DisplayName("Отображение товаров в корзине, если корзина пуста")
    void shouldReturnEmptyListIfCartIsEmpty() {
        Long userId = 1L;
        User user = createUser(userId);
        insertIntoUserTable(databaseClient, user).block();

        webClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertNotNull(result.getResponseBody());
                    assertTrue(result.getResponseBody().contains("Ваша корзина пуста"));
                });
    }

    @Test
    @DisplayName("Изменение количества товара в корзине: PLUS")
    void shouldPlusQuantityOfItemInCart() {
        Long itemId = 1L;
        Long userId = 1L;
        User user = createUser(userId);
        insertIntoUserTable(databaseClient, user).block();

        insertIntoCartTable(databaseClient, List.of(
                new CartItem(1L, 3, userId),
                new CartItem(2L, 5, userId)
        )).block();

        webClient.post()
                .uri("/cart/items/%d".formatted(itemId))
                .body(BodyInserters.fromFormData("action", ActionEnum.PLUS.name()))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");

        CartItem cartItem = cartRepository.findCartItemByItemIdAndUserId(itemId, userId).block();
        assertNotNull(cartItem);
        assertEquals(4, cartItem.getQuantity());
    }


    @Test
    @DisplayName("Изменение количества товара в корзине: MINUS")
    void shouldMinusQuantityOfItemInCart() {
        Long itemId = 1L;
        Long userId = 1L;
        User user = createUser(userId);
        insertIntoUserTable(databaseClient, user).block();

        insertIntoCartTable(databaseClient, List.of(
                new CartItem(1L, 3, userId),
                new CartItem(2L, 5, userId)
        )).block();

        webClient.post()
                .uri("/cart/items/%d".formatted(itemId))
                .body(BodyInserters.fromFormData("action", ActionEnum.MINUS.name()))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");

        CartItem cartItem = cartRepository.findCartItemByItemIdAndUserId(itemId, userId).block();
        assertNotNull(cartItem);
        assertEquals(2, cartItem.getQuantity());
    }

    @Test
    @DisplayName("Изменение количества товара в корзине: DELETE")
    void shouldDeleteItemFromCart() {
        Long itemId = 1L;
        Long userId = 1L;
        User user = createUser(userId);
        insertIntoUserTable(databaseClient, user).block();

        insertIntoCartTable(databaseClient, List.of(
                new CartItem(1L, 3, userId),
                new CartItem(2L, 5, userId)
        )).block();

        webClient.post()
                .uri("/cart/items/%d".formatted(itemId))
                .body(BodyInserters.fromFormData("action", ActionEnum.DELETE.name()))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");

        assertNull(cartRepository.findCartItemByItemIdAndUserId(itemId, userId).block());
        assertEquals(1L, cartRepository.findAll().toStream().toList().size());
    }
}
