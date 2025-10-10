package com.shop.easybuy.service;

import com.shop.easybuy.model.cache.CacheSavedRs;
import com.shop.easybuy.model.cache.CachedItem;
import com.shop.easybuy.model.cache.SortEnum;
import com.shop.easybuy.repository.CacheRepository;
import com.shop.easybuy.utils.KeyCacheGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
                .doOnNext(status -> log.info("Товар с ID {} успешно извлечён из кеша", itemId))
                .doOnSuccess(item -> {
                    if (item == null) log.info("Товар с ID {} не найден в кеше", itemId);
                });
    }

    public Mono<CacheSavedRs> cacheMainItems(Flux<CachedItem> items,
                                             String search,
                                             SortEnum sort,
                                             Integer pageSize,
                                             Integer pageNumber) {
        String key = KeyCacheGenerator.generateKey(search, sort.name(), pageSize, pageNumber);
        return cacheRepository
                .cacheMainItems(items, key)
                .doOnSuccess(status -> log.info("Товары успешно кешированы с параметрами: search - {}, sort - {}, pageSize - {}, pageNumber - {}.",
                        search, sort, pageSize, pageNumber));
    }

    public Flux<CachedItem> getMainItemsByParams(String search,
                                                 SortEnum sort,
                                                 Integer pageSize,
                                                 Integer pageNumber) {
        String key = KeyCacheGenerator.generateKey(search, sort.name(), pageSize, pageNumber);
        return cacheRepository
                .getMainItemsByKey(key)
                .doOnComplete(() -> log.info("Товары успешно извлечены из кеша по параметрам: search - {}, sort - {}, pageSize - {}, pageNumber - {}.",
                        search, sort, pageSize, pageNumber))
                .switchIfEmpty(Flux.defer(() -> {
                    log.info("Товары не найдены в кеше по параметрам: search - {}, sort - {}, pageSize - {}, pageNumber - {}.",
                            search, sort, pageSize, pageNumber);
                    return Flux.empty();
                }));
    }
}
