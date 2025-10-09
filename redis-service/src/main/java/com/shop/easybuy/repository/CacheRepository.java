package com.shop.easybuy.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.easybuy.common.exception.DataNotFoundException;
import com.shop.easybuy.common.exception.DeserializationException;
import com.shop.easybuy.model.cache.CacheSavedRs;
import com.shop.easybuy.model.cache.CachedItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

//TODO Добавить интерфейс
@Slf4j
@Repository
@RequiredArgsConstructor
public class CacheRepository {

    private final ReactiveRedisTemplate<String, Object> redisTemplate; //TODO Заменить Object на свою сущность после генерации API
    private final ObjectMapper objectMapper;
    private static final String ITEM_KEY_PREFIX = "item:";
    private static final String ORDER_ITEMS_LIST_KEY_PREFIX = "items:order:";

    public Mono<CacheSavedRs> cacheItem(CachedItem item) {
        String key = ITEM_KEY_PREFIX + item.getId();
        return redisTemplate
                .opsForValue()
                .set(key, item)
                .map(CacheSavedRs::new)
                .onErrorResume(error -> {
                    log.error("Ошибка при кешировании товара с ID {}: {}", item.getId(), error.getMessage());
                    return Mono.just(new CacheSavedRs(false));
                });
    }

    public Mono<CachedItem> getItemById(Long id) {
        String key = ITEM_KEY_PREFIX + id;
        return redisTemplate
                .opsForValue()
                .get(key)
                .flatMap(object -> {
                    if (object == null) return Mono.error(new DataNotFoundException(key));
                    try {
                        CachedItem item = objectMapper.convertValue(object, CachedItem.class);
                        return Mono.just(item);
                    } catch (IllegalArgumentException e) {
                        log.error("Ошибка десериализации CachedItem: {}", e.getMessage());
                        return Mono.error(new DeserializationException("CachedItem"));
                    }
                })
                .switchIfEmpty(Mono.error(new DataNotFoundException(key)));
    }

    public Mono<CacheSavedRs> cacheOrderItems(List<CachedItem> items, Long orderId) {
        String key = ORDER_ITEMS_LIST_KEY_PREFIX + orderId;
        return redisTemplate
                .opsForValue()
                .set(key, items)
                .map(CacheSavedRs::new)
                .onErrorResume(error -> {
                    log.error("Ошибка при кешировании товаров заказа с ID {}: {}", orderId, error.getMessage());
                    return Mono.just(new CacheSavedRs(false));
                });
    }

    public Flux<CachedItem> getItemsByOrderId(Long orderId) {
        String key = ORDER_ITEMS_LIST_KEY_PREFIX + orderId;
        return redisTemplate
                .opsForValue()
                .get(key)
                .flatMapMany(object -> {
                    if (object == null) return Flux.error(new DataNotFoundException(key));

                    try {
                        List<CachedItem> items = objectMapper.convertValue(object, new TypeReference<>() {
                        });
                        return Flux.fromIterable(items);
                    } catch (IllegalArgumentException e) {
                        log.error("Ошибка при десериализации списка CachedItem: {}", e.getMessage());
                        return Flux.error(new DeserializationException("Список CachedItem"));
                    }
                });
    }
}
