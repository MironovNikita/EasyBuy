package com.shop.easybuy.controller;

import com.shop.easybuy.api.cache.CacheApi;
import com.shop.easybuy.model.cache.CacheSavedRs;
import com.shop.easybuy.model.cache.CachedItem;
import com.shop.easybuy.service.CacheService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//TODO Разнести контроллеры по пакетам
@RestController
@RequiredArgsConstructor
public class CacheController implements CacheApi {

    //TODO Интерфейс!!!
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
    public Mono<ResponseEntity<CacheSavedRs>> cacheOrderItems(
            @PathVariable("orderId") Long orderId,
            @Valid @RequestBody Flux<CachedItem> cachedItems,
            ServerWebExchange exchange
    ) {
        return cachedItems
                .collectList()
                .flatMap(list -> cacheService.cacheOrderItems(list, orderId))
                .map(ResponseEntity::ok);
    }

    public Mono<ResponseEntity<Flux<CachedItem>>> getItemsByOrderId(
            @PathVariable("orderId") Long orderId,
            ServerWebExchange exchange
    ) {
        return Mono.just(ResponseEntity.ok(cacheService.getItemsByOrderId(orderId)));
    }
}
