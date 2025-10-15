package com.shop.easybuy.service;

import com.shop.easybuy.model.cache.CacheSavedRs;
import com.shop.easybuy.model.cache.CachedItem;
import com.shop.easybuy.model.cache.SortEnum;
import com.shop.easybuy.repository.cache.CacheRepository;
import com.shop.easybuy.service.cache.CacheServiceImpl;
import com.shop.easybuy.utils.KeyCacheGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CacheServiceTest {

    @Mock
    private CacheRepository cacheRepository;

    @InjectMocks
    private CacheServiceImpl cacheService;

    @Test
    @DisplayName("Проверка кеширования товара")
    void shouldCacheItem() {
        Long itemId = 1L;
        CachedItem item = new CachedItem().id(itemId);

        when(cacheRepository.cacheItem(item)).thenReturn(Mono.just(new CacheSavedRs(true)));

        StepVerifier.create(cacheService.cacheItem(item))
                .assertNext(result -> assertTrue(result.getSaved()))
                .verifyComplete();

        verify(cacheRepository).cacheItem(item);
    }

    @Test
    @DisplayName("Проверка получения товара из кеша по ID")
    void shouldGetItemById() {
        Long itemId = 1L;
        CachedItem item = new CachedItem().id(itemId);

        when(cacheRepository.getItemById(itemId)).thenReturn(Mono.just(item));

        StepVerifier.create(cacheService.getItemById(itemId))
                .assertNext(result -> assertEquals(item.getId(), result.getId()))
                .verifyComplete();

        verify(cacheRepository).getItemById(itemId);
    }

    @Test
    @DisplayName("Проверка кеширования товаров с главной страницы")
    void shouldCacheMainItems() {
        Long itemId1 = 1L;
        Long itemId2 = 2L;
        CachedItem item1 = new CachedItem().id(itemId1);
        CachedItem item2 = new CachedItem().id(itemId2);
        List<CachedItem> items = List.of(item1, item2);
        Flux<CachedItem> cachedItems = Flux.fromIterable(items);
        SortEnum sort = SortEnum.NONE;
        String key = KeyCacheGenerator.generateKey("", sort.name(), 5, 0);

        when(cacheRepository.cacheMainItems(cachedItems, key)).thenReturn(Mono.just(new CacheSavedRs(true)));

        StepVerifier.create(cacheService.cacheMainItems(cachedItems, "", sort, 5, 0))
                .assertNext(result -> assertTrue(result.getSaved()))
                .verifyComplete();

        verify(cacheRepository).cacheMainItems(cachedItems, key);
    }

    @Test
    @DisplayName("Проверка получения товаров с главной страницы")
    void shouldGetMainPageItems() {
        Long itemId1 = 1L;
        Long itemId2 = 2L;
        CachedItem item1 = new CachedItem().id(itemId1);
        CachedItem item2 = new CachedItem().id(itemId2);
        List<CachedItem> items = List.of(item1, item2);
        Flux<CachedItem> cachedItems = Flux.fromIterable(items);
        SortEnum sort = SortEnum.ALPHA;
        String key = KeyCacheGenerator.generateKey("", sort.name(), 5, 0);

        when(cacheRepository.getMainItemsByKey(key)).thenReturn(cachedItems);

        StepVerifier.create(cacheService.getMainItemsByParams("", sort, 5, 0))
                .expectNextSequence(items)
                .verifyComplete();

        verify(cacheRepository).getMainItemsByKey(key);
    }

    @Test
    @DisplayName("Проверка получения товаров с главной страницы, если результат пустой")
    void shouldNotGetMainPageItems() {
        SortEnum sort = SortEnum.ALPHA;
        String key = KeyCacheGenerator.generateKey("", sort.name(), 5, 0);

        when(cacheRepository.getMainItemsByKey(key)).thenReturn(Flux.empty());

        StepVerifier.create(cacheService.getMainItemsByParams("", sort, 5, 0))
                .expectNextCount(0)
                .verifyComplete();

        verify(cacheRepository).getMainItemsByKey(key);
    }
}
