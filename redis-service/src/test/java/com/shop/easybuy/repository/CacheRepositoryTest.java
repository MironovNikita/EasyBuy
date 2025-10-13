package com.shop.easybuy.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.easybuy.model.cache.CachedItem;
import com.shop.easybuy.repository.cache.CacheRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
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
        CachedItem item = new CachedItem().id(itemId);

        when(valueOperations.set(itemKey, item, cacheLiveTime)).thenReturn(Mono.just(true));

        StepVerifier.create(cacheRepository.cacheItem(item))
                .assertNext(result -> assertTrue(result.getSaved()))
                .verifyComplete();

        verify(valueOperations).set(itemKey, item, cacheLiveTime);
    }
}
