package com.shop.easybuy.service.cache;

import com.shop.easybuy.model.cache.CacheSavedRs;
import com.shop.easybuy.model.cache.CachedItem;
import com.shop.easybuy.model.cache.SortEnum;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CacheService {

    Mono<CacheSavedRs> cacheItem(CachedItem item);

    Mono<CachedItem> getItemById(Long itemId);

    Mono<CacheSavedRs> cacheMainItems(Flux<CachedItem> items,
                                      String search,
                                      SortEnum sort,
                                      Integer pageSize,
                                      Integer pageNumber);

    Flux<CachedItem> getMainItemsByParams(String search,
                                          SortEnum sort,
                                          Integer pageSize,
                                          Integer pageNumber);
}
