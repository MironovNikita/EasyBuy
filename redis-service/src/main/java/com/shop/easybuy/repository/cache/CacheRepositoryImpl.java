package com.shop.easybuy.repository.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.easybuy.common.exception.DeserializationException;
import com.shop.easybuy.model.cache.CacheSavedRs;
import com.shop.easybuy.model.cache.CachedItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CacheRepositoryImpl implements CacheRepository {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final Duration cacheLiveTime;

    private static final String ITEM_KEY_PREFIX = "item:";

    @Override
    public Mono<CacheSavedRs> cacheItem(CachedItem item) {
        String key = ITEM_KEY_PREFIX + item.getId();
        return redisTemplate
                .opsForValue()
                .set(key, item, cacheLiveTime)
                .map(CacheSavedRs::new)
                .onErrorResume(error -> {
                    log.error("Ошибка при кешировании товара с ID {}: {}", item.getId(), error.getMessage());
                    return Mono.just(new CacheSavedRs(false));
                });
    }

    @Override
    public Mono<CachedItem> getItemById(Long id) {
        String key = ITEM_KEY_PREFIX + id;
        return redisTemplate
                .opsForValue()
                .get(key)
                .flatMap(object -> {
                    if (object == null) return Mono.empty();
                    try {
                        CachedItem item = objectMapper.convertValue(object, CachedItem.class);
                        return Mono.just(item);
                    } catch (IllegalArgumentException e) {
                        log.error("Ошибка десериализации CachedItem: {}", e.getMessage());
                        return Mono.error(new DeserializationException("CachedItem"));
                    }
                })
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<CacheSavedRs> cacheMainItems(Flux<CachedItem> items, String key) {

        return items
                .collectList()
                .flatMap(itemList -> redisTemplate
                        .opsForValue()
                        .set(key, itemList, cacheLiveTime)
                        .map(CacheSavedRs::new)
                        .onErrorResume(error -> {
                            log.error("Ошибка при кешировании товаров по ключу: {}. Текст ошибки: {}", key, error.getMessage());
                            return Mono.just(new CacheSavedRs(false));
                        })
                );
    }

    @Override
    public Flux<CachedItem> getMainItemsByKey(String key) {
        return redisTemplate
                .opsForValue()
                .get(key)
                .flatMapMany(object -> {
                    if (object == null) return Flux.empty();

                    try {
                        List<CachedItem> items = objectMapper.convertValue(object, new TypeReference<>() {
                        });
                        return Flux.fromIterable(items);
                    } catch (IllegalArgumentException e) {
                        log.error("Ошибка при десериализации списка CachedItem: {}", e.getMessage());
                        return Flux.error(new DeserializationException("Список CachedItem"));
                    }
                })
                .switchIfEmpty(Flux.empty());
    }
}
