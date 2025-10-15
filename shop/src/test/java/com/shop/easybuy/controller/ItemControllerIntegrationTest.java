package com.shop.easybuy.controller;

import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.entity.cart.CartItem;
import com.shop.easybuy.entity.item.ItemRsDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;

import static com.shop.easybuy.DataInserter.insertIntoCartTable;
import static org.junit.jupiter.api.Assertions.*;

public class ItemControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Редирект на главную страницу")
    void shouldRedirectToMainPage() {

        webClient.get()
                .uri("/")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main/items");
    }

    @Test
    @DisplayName("Главная страница без сортировки и строки поиска")
    void shouldShowMainPageWithoutSortAndSearch() {

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/main/items")
                        .queryParam("search", "")
                        .queryParam("sort", "NONE")
                        .queryParam("pageSize", 5)
                        .queryParam("pageNumber", 0)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    String body = result.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Майка"));
                    assertTrue(body.contains("Стандартная лампочка"));
                    assertTrue(body.contains("Колбаса"));
                    assertTrue(body.contains("Моющее средство"));
                    assertTrue(body.contains("Йогурт"));
                    assertTrue(body.contains("Страница: 1"));
                });
    }

    @Test
    @DisplayName("Главная страница с сортировкой и строкой поиска")
    void shouldShowMainPageWithSortAndSearch() {

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/main/items")
                        .queryParam("search", "ма")
                        .queryParam("sort", "ALPHA")
                        .queryParam("pageSize", 5)
                        .queryParam("pageNumber", 0)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    String body = result.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Майка"));
                    assertTrue(body.contains("Мармеладки"));
                    assertTrue(body.contains("Страница: 1"));
                });
    }

    @Test
    @DisplayName("Изменение количества товара в корзине на главной странице: PLUS")
    void shouldChangeQuantityOnMainPagePlus() {
        Long itemId = 1L;

        webClient.post()
                .uri("/main/items/%d".formatted(itemId))
                .body(BodyInserters.fromFormData("action", ActionEnum.PLUS.name())
                        .with("search", "")
                        .with("sort", "NONE")
                        .with("pageNumber", "0")
                        .with("pageSize", "10"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main/items?search=&sort=NONE&pageNumber=0&pageSize=10");

        CartItem cartItem = cartRepository.findCartItemByItemId(itemId).block();
        assertNotNull(cartItem);
        assertEquals(1, cartItem.getQuantity());
    }

    @Test
    @DisplayName("Изменение количества товара в корзине на главной странице: MINUS")
    void shouldChangeQuantityOnMainPageMinus() {
        Long itemId = 1L;

        insertIntoCartTable(databaseClient, List.of(
                new CartItem(itemId, 3)
        )).block();

        webClient.post()
                .uri("/main/items/%d".formatted(itemId))
                .body(BodyInserters.fromFormData("action", ActionEnum.MINUS.name())
                        .with("search", "")
                        .with("sort", "NONE")
                        .with("pageNumber", "0")
                        .with("pageSize", "10"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main/items?search=&sort=NONE&pageNumber=0&pageSize=10");

        CartItem cartItem = cartRepository.findCartItemByItemId(itemId).block();
        assertNotNull(cartItem);
        assertEquals(2, cartItem.getQuantity());
    }

    @Test
    @DisplayName("Изменение количества товара в корзине на главной странице: DELETE")
    void shouldChangeQuantityOnMainPageDelete() {
        Long itemId = 1L;

        insertIntoCartTable(databaseClient, List.of(
                new CartItem(itemId, 3)
        )).block();

        webClient.post()
                .uri("/main/items/%d".formatted(itemId))
                .body(BodyInserters.fromFormData("action", ActionEnum.DELETE.name())
                        .with("search", "")
                        .with("sort", "NONE")
                        .with("pageNumber", "0")
                        .with("pageSize", "10"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main/items?search=&sort=NONE&pageNumber=0&pageSize=10");

        CartItem cartItem = cartRepository.findCartItemByItemId(itemId).block();
        assertNull(cartItem);
    }

    @Test
    @DisplayName("Отображение страницы товара")
    void shouldShowItemPageIfExists() {

        Long itemId = 1L;

        webClient.get()
                .uri("/items/%d".formatted(itemId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> {
                    String body = result.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Майка"));
                });

        ItemRsDto item = itemRepository.findItemById(itemId).block();
        assertNotNull(item);
        assertTrue(item.title().contains("Майка"));
    }

    @Test
    @DisplayName("Ошибка поиска товара по несуществующему ID")
    void shouldThrowObjectNotFoundExceptionIfNonExistentItemId() {
        Long nonExistentItemId = 9999L;

        webClient.get()
                .uri("/items/%d".formatted(nonExistentItemId))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .consumeWith(result -> {
                    String body = result.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Товар с ID: 9999 не найден!"));
                });

        ItemRsDto item = itemRepository.findItemById(nonExistentItemId).block();
        assertNull(item);
    }

    @Test
    @DisplayName("Изменение количества товара на странице товара: PLUS")
    void shouldChangeQuantityOnItemPagePlus() {
        Long itemId = 1L;

        webClient.post()
                .uri("/items/%d".formatted(itemId))
                .body(BodyInserters.fromFormData("action", ActionEnum.PLUS.name()))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/items/%d".formatted(itemId));

        CartItem cartItem = cartRepository.findCartItemByItemId(itemId).block();
        assertNotNull(cartItem);
        assertEquals(1, cartItem.getQuantity());
    }

    @Test
    @DisplayName("Изменение количества товара на странице товара: MINUS")
    void shouldChangeQuantityOnItemPageMinus() {
        Long itemId = 1L;

        insertIntoCartTable(databaseClient, List.of(
                new CartItem(itemId, 3)
        )).block();

        webClient.post()
                .uri("/items/%d".formatted(itemId))
                .body(BodyInserters.fromFormData("action", ActionEnum.MINUS.name()))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/items/%d".formatted(itemId));

        CartItem cartItem = cartRepository.findCartItemByItemId(itemId).block();
        assertNotNull(cartItem);
        assertEquals(2, cartItem.getQuantity());
    }
}
