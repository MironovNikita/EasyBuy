package com.shop.easybuy.repository.cache;

import com.shop.easybuy.model.cache.CacheSavedRs;
import com.shop.easybuy.model.cache.CachedItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CacheRepository {

    Mono<CacheSavedRs> cacheItem(CachedItem item);

    Mono<CachedItem> getItemById(Long id);

    Mono<CacheSavedRs> cacheMainItems(Flux<CachedItem> items, String key);

    Flux<CachedItem> getMainItemsByKey(String key);
}
