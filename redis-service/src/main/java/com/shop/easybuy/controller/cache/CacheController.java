package com.shop.easybuy.controller.cache;

import com.shop.easybuy.api.cache.CacheApi;
import com.shop.easybuy.model.cache.CacheSavedRs;
import com.shop.easybuy.model.cache.CachedItem;
import com.shop.easybuy.model.cache.SortEnum;
import com.shop.easybuy.service.cache.CacheService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class CacheController implements CacheApi {

    private final CacheService cacheService;

    @Override
    public Mono<ResponseEntity<CacheSavedRs>> cacheItem(
            @PathVariable("itemId") Long itemId,
            @Valid @RequestBody Mono<CachedItem> cachedItem,
            ServerWebExchange exchange
    ) {
        return cachedItem
                .flatMap(cacheService::cacheItem)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<CachedItem>> getItemById(
            @PathVariable("itemId") Long itemId,
            ServerWebExchange exchange
    ) {
        return cacheService
                .getItemById(itemId)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<CacheSavedRs>> cacheMainItems(
            @Valid @RequestBody Flux<CachedItem> cachedItem,
            @Size(max = 20) @Valid @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @Valid @RequestParam(value = "sort", required = false, defaultValue = "NONE") SortEnum sort,
            @Min(1) @Max(100) @Valid @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @Min(0) @Valid @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            ServerWebExchange exchange
    ) {
        return cacheService
                .cacheMainItems(cachedItem, search, sort, pageSize, pageNumber)
                .map(ResponseEntity::ok);
    }

    public Mono<ResponseEntity<Flux<CachedItem>>> getMainItemsByParams(
            @Size(max = 20) @Valid @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @Valid @RequestParam(value = "sort", required = false, defaultValue = "NONE") SortEnum sort,
            @Min(1) @Max(100) @Valid @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            @Min(0) @Valid @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            ServerWebExchange exchange
    ) {
        return Mono.just(
                ResponseEntity.ok(
                        cacheService
                                .getMainItemsByParams(search, sort, pageSize, pageNumber)));
    }
}
