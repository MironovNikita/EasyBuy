package com.shop.easybuy.repository.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.easybuy.entity.cache.CachedItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static com.shop.easybuy.DataCreator.createCachedItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CacheRepositoryTest {
    @Mock
    private ReactiveRedisTemplate<String, Object> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, Object> valueOperations;

    @Mock
    private Duration cacheLiveTime;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CacheRepositoryImpl cacheRepository;

    private final String ITEM_KEY_PREFIX = "item:";

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("Проверка кеширования товара")
    void shouldCacheItem() {
        long itemId = 1L;
        String itemKey = ITEM_KEY_PREFIX + itemId;
        CachedItem item = createCachedItem(itemId);

        when(valueOperations.set(itemKey, item, cacheLiveTime)).thenReturn(Mono.just(true));

        StepVerifier.create(cacheRepository.cacheItem(item))
                .assertNext(result -> assertTrue(result.saved()))
                .verifyComplete();

        verify(valueOperations).set(itemKey, item, cacheLiveTime);
    }

    @Test
    @DisplayName("Проверка негативного ответа при ошибке кеширования товара")
    void shouldThrowExceptionWhenCacheItem() {
        long itemId = 1L;
        String itemKey = ITEM_KEY_PREFIX + itemId;
        CachedItem item = createCachedItem(itemId);

        when(valueOperations.set(itemKey, item, cacheLiveTime)).thenReturn(Mono.error(new RuntimeException()));

        StepVerifier.create(cacheRepository.cacheItem(item))
                .assertNext(result -> assertFalse(result.saved()))
                .verifyComplete();

        verify(valueOperations).set(itemKey, item, cacheLiveTime);
    }

    @Test
    @DisplayName("Проверка получения товара по ID из кеша")
    void shouldReturnItemFromCache() {
        long itemId = 1L;
        String itemKey = ITEM_KEY_PREFIX + itemId;
        CachedItem item = createCachedItem(itemId);

        when(valueOperations.get(itemKey)).thenReturn(Mono.just(item));
        when(objectMapper.convertValue(any(), eq(CachedItem.class))).thenReturn(item);

        StepVerifier.create(cacheRepository.getItemById(itemId))
                .assertNext(result -> assertEquals(item, result))
                .verifyComplete();

        verify(valueOperations).get(itemKey);
    }

    @Test
    @DisplayName("Проверка получения товара по ID из кеша, если товара там нет")
    void shouldReturnEmptyIfItemNotCached() {
        long itemId = 1L;
        String itemKey = ITEM_KEY_PREFIX + itemId;

        when(valueOperations.get(itemKey)).thenReturn(Mono.empty());

        StepVerifier.create(cacheRepository.getItemById(itemId))
                .expectNextCount(0)
                .verifyComplete();

        verify(valueOperations).get(itemKey);
    }

    @Test
    @DisplayName("Проверка получения товара по ID из кеша, если ошибка во время маппинга")
    void shouldThrowIllegalArgumentExceptionWhenMapping() {
        long itemId = 1L;
        String itemKey = ITEM_KEY_PREFIX + itemId;
        CachedItem item = createCachedItem(itemId);

        when(valueOperations.get(itemKey)).thenReturn(Mono.just(item));
        when(objectMapper.convertValue(any(), eq(CachedItem.class))).thenThrow(new IllegalArgumentException());

        StepVerifier.create(cacheRepository.getItemById(itemId))
                .expectNextCount(0)
                .verifyComplete();

        verify(valueOperations).get(itemKey);
    }

    @Test
    @DisplayName("Проверка кеширования товаров с главной страницы")
    void shouldCacheItemsFromMainPage() {
        long itemId1 = 1L;
        long itemId2 = 2L;
        CachedItem item1 = createCachedItem(itemId1);
        CachedItem item2 = createCachedItem(itemId2);
        String itemsKey = "items:";
        List<CachedItem> cachedList = List.of(item1, item2);

        when(valueOperations.set(itemsKey, cachedList, cacheLiveTime)).thenReturn(Mono.just(true));

        StepVerifier.create(cacheRepository.cacheMainItems(Flux.just(item1, item2), itemsKey))
                .assertNext(result -> assertTrue(result.saved()))
                .verifyComplete();

        verify(valueOperations).set(itemsKey, cachedList, cacheLiveTime);
    }

    @Test
    @DisplayName("Проверка негативного ответа кеширования товаров, если товары не кешировались")
    void shouldReturnFalseIfItemsNotCached() {
        long itemId1 = 1L;
        long itemId2 = 2L;
        CachedItem item1 = createCachedItem(itemId1);
        CachedItem item2 = createCachedItem(itemId2);
        String itemsKey = "items:";
        List<CachedItem> cachedList = List.of(item1, item2);

        when(valueOperations.set(itemsKey, cachedList, cacheLiveTime)).thenReturn(Mono.just(false));

        StepVerifier.create(cacheRepository.cacheMainItems(Flux.just(item1, item2), itemsKey))
                .assertNext(result -> assertFalse(result.saved()))
                .verifyComplete();

        verify(valueOperations).set(itemsKey, cachedList, cacheLiveTime);
    }

    @Test
    @DisplayName("Проверка получения товаров с главной страницы из кеша")
    void shouldGetItemsFromMainPageFromCache() {
        long itemId1 = 1L;
        long itemId2 = 2L;
        CachedItem item1 = createCachedItem(itemId1);
        CachedItem item2 = createCachedItem(itemId2);
        String itemsKey = "items:";
        List<CachedItem> cachedList = List.of(item1, item2);

        when(valueOperations.get(itemsKey)).thenReturn(Mono.just(cachedList));
        when(objectMapper.convertValue(any(), eq(CachedItem[].class)))
                .thenReturn(cachedList.toArray(new CachedItem[0]));

        StepVerifier.create(cacheRepository.getMainItemsByKey(itemsKey))
                .expectNextSequence(cachedList)
                .verifyComplete();

        verify(valueOperations).get(itemsKey);
        verify(objectMapper).convertValue(any(), eq(CachedItem[].class));
    }

    @Test
    @DisplayName("Проверка получения товаров с главной страницы из кеша, если товара там нет")
    void shouldNotGetItemsFromMainPageFromCache() {
        String itemsKey = "items:";

        when(valueOperations.get(itemsKey)).thenReturn(Mono.empty());

        StepVerifier.create(cacheRepository.getMainItemsByKey(itemsKey))
                .expectNextCount(0)
                .verifyComplete();

        verify(valueOperations).get(itemsKey);
    }

    @Test
    @DisplayName("Проверка получения товаров с главной страницы из кеша, если ошибка во время маппинга")
    void shouldThrowIllegalArgumentExceptionWhenMappingItems() {
        long itemId1 = 1L;
        long itemId2 = 2L;
        CachedItem item1 = createCachedItem(itemId1);
        CachedItem item2 = createCachedItem(itemId2);
        String itemsKey = "items:";
        List<CachedItem> cachedList = List.of(item1, item2);

        when(valueOperations.get(itemsKey)).thenReturn(Mono.just(cachedList));
        when(objectMapper.convertValue(any(), eq(CachedItem[].class)))
                .thenThrow(new IllegalArgumentException());

        StepVerifier.create(cacheRepository.getMainItemsByKey(itemsKey))
                .expectNextCount(0)
                .verifyComplete();

        verify(valueOperations).get(itemsKey);
        verify(objectMapper).convertValue(any(), eq(CachedItem[].class));
    }
}
