package com.shop.easybuy.service;

import com.shop.easybuy.model.cache.CacheSavedRs;
import com.shop.easybuy.model.cache.CachedItem;
import com.shop.easybuy.repository.CacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

//TODO Добавить интерфейс
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheRepository cacheRepository;

    public Mono<CacheSavedRs> cacheItem(CachedItem item) {
        return cacheRepository
                .cacheItem(item)
                .doOnSuccess(status -> log.info("Товар с ID {} успешно кеширован. Статус: {}", item.getId(), status.getSaved()));
    }

    public Mono<CachedItem> getItemById(Long itemId) {
        return cacheRepository
                .getItemById(itemId)
                .doOnNext(status -> log.info("Товар с ID {} успешно извлечён из кеша", itemId));
    }

    public Mono<CacheSavedRs> cacheOrderItems(List<CachedItem> items, Long orderId) {
        return cacheRepository
                .cacheOrderItems(items, orderId)
                .doOnSuccess(status -> log.info("Товары заказа с ID {} успешно кешированы. Статус: {}", orderId, status.getSaved()));
    }

    public Flux<CachedItem> getItemsByOrderId(Long orderId) {
        return cacheRepository
                .getItemsByOrderId(orderId)
                .doOnComplete(() -> log.info("Товары заказа с ID {} успешно извлечены из кеша", orderId));
    }
}
