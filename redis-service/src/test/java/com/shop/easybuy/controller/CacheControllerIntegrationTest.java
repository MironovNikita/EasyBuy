package com.shop.easybuy.controller;

import com.shop.easybuy.model.cache.CacheSavedRs;
import com.shop.easybuy.model.cache.CachedItem;
import com.shop.easybuy.model.cache.SortEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CacheControllerIntegrationTest extends BaseIntegrationTest {

    @AfterEach
    void cleanCache() {
        redisTemplate.keys("*")
                .filter(key -> !key.equals("balance"))
                .flatMap(redisTemplate::delete)
                .then()
                .block();
    }

    @Test
    @DisplayName("Проверка кеширования товара")
    void shouldCacheItem() {
        Long itemId = 1L;
        CachedItem item = createCachedItem(itemId);

        webClient.post()
                .uri("/cache/item/%d".formatted(itemId))
                .body(Mono.just(item), CachedItem.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CacheSavedRs.class)
                .consumeWith(result -> {
                    var body = result.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.getSaved());
                });

        var cached = cacheService
                .getItemById(itemId)
                .block();

        assertEquals(cached, item);
    }

    @Test
    @DisplayName("Проверка получения товара по ID из кеша")
    void shouldGetItemFromCacheById() {
        Long itemId = 1L;
        CachedItem item = createCachedItem(itemId);

        cacheService.cacheItem(item).block();

        webClient.get()
                .uri("/cache/item/%d".formatted(itemId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CachedItem.class)
                .consumeWith(result -> {
                    var body = result.getResponseBody();
                    assertNotNull(body);
                    assertEquals(body, item);
                });
    }

    @Test
    @DisplayName("Проверка получения товара по ID из кеша, которого там нет")
    void shouldNotFindItemInCacheById() {
        Long itemId = 1L;

        webClient.get()
                .uri("/cache/item/%d".formatted(itemId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CachedItem.class)
                .consumeWith(result -> {
                    var body = result.getResponseBody();
                    assertNull(body);
                });
    }

    @Test
    @DisplayName("Проверка сохранения нескольких товаров в кеш с главной страницы")
    void shouldCacheMainItemsByParams() {
        Long itemId1 = 1L;
        Long itemId2 = 2L;
        CachedItem item1 = createCachedItem(itemId1);
        CachedItem item2 = createCachedItem(itemId2);
        List<CachedItem> cachedItems = List.of(item1, item2);

        webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/cache/main/items")
                        .queryParam("search", "")
                        .queryParam("sort", "NONE")
                        .queryParam("pageSize", 5)
                        .queryParam("pageNumber", 0)
                        .build())
                .body(Flux.fromIterable(cachedItems), CachedItem.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CacheSavedRs.class)
                .consumeWith(result -> {
                    var body = result.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.getSaved());
                });

        var cached = cacheService.getMainItemsByParams("", SortEnum.NONE, 5, 0)
                .collectList()
                .block();

        assertNotNull(cached);
        assertEquals(2, cached.size());
        assertTrue(cached.containsAll(cachedItems));
    }

    @Test
    @DisplayName("Проверка получения нескольких товаров с главной страницы из кеша")
    void shouldGetMainItemsByParams() {

        Long itemId1 = 1L;
        Long itemId2 = 2L;
        CachedItem item1 = createCachedItem(itemId1);
        CachedItem item2 = createCachedItem(itemId2);
        List<CachedItem> cachedItems = List.of(item1, item2);

        cacheService.cacheMainItems(Flux.fromIterable(cachedItems), "", SortEnum.NONE, 5, 0).block();

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/cache/main/items")
                        .queryParam("search", "")
                        .queryParam("sort", "NONE")
                        .queryParam("pageSize", 5)
                        .queryParam("pageNumber", 0)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CachedItem.class)
                .consumeWith(result -> {
                    var body = result.getResponseBody();
                    assertNotNull(body);
                    assertEquals(2, body.size());
                    assertTrue(body.containsAll(cachedItems));
                });
    }

    @Test
    @DisplayName("Проверка получения нескольких товаров с главной страницы из кеша, если товаров там нет")
    void shouldNotGetMainItemsByParams() {

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/cache/main/items")
                        .queryParam("search", "")
                        .queryParam("sort", "NONE")
                        .queryParam("pageSize", 5)
                        .queryParam("pageNumber", 0)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CachedItem.class)
                .consumeWith(result -> {
                    var body = result.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.isEmpty());
                });
    }

    private CachedItem createCachedItem(Long id) {
        return new CachedItem()
                .id(id)
                .title("Test title")
                .description("Test description")
                .image("Test image")
                .price(5000L);
    }
}
