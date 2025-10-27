package com.shop.easybuy.service.item;

import com.shop.easybuy.common.entity.PageResult;
import com.shop.easybuy.common.entity.SortEnum;
import com.shop.easybuy.common.exception.ObjectNotFoundException;
import com.shop.easybuy.common.mapper.ItemMapper;
import com.shop.easybuy.common.security.SecurityUserContextHandler;
import com.shop.easybuy.entity.cache.CachedItem;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.repository.cache.CacheRepository;
import com.shop.easybuy.repository.item.ItemRepository;
import com.shop.easybuy.utils.KeyCacheGenerator;
import com.shop.easybuy.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    @Value("${items.row.size}")
    private int rowSize;

    private final ItemRepository itemRepository;

    private final CacheRepository cacheRepository;

    private final ItemMapper itemMapper;

    private final SecurityUserContextHandler securityUserContextHandler;

    @Override
    public Mono<PageResult<ItemRsDto>> getAllByParams(String search, Pageable pageable, SortEnum sort) {
        var pageSize = pageable.getPageSize();
        var pageNumber = pageable.getPageNumber();
        var key = KeyCacheGenerator.generateKey(search, sort.name(), pageSize, pageNumber);

        return securityUserContextHandler.getCurrentUserId()
                .defaultIfEmpty(-1L)
                .flatMap(userId -> cacheRepository.getMainItemsByKey(key)
                        .collectList()
                        .flatMap(cachedItems -> {
                            if (!cachedItems.isEmpty()) {
                                log.info("Главная страница с товарами ({} шт.) получена из кеша.", cachedItems.size());
                                return Flux.fromIterable(cachedItems)
                                        .flatMap(itemMapper::toItemRsDtoMono)
                                        .collectList()
                                        .map(list -> buildPageResult(list, pageable, list.size()));
                            } else {
                                log.info("Данные по параметрам (search: \"{}\", sort: {}, pageSize: {}, pageNumber: {}) не найдены в кеше.",
                                        search, sort.name(), pageSize, pageNumber);

                                Flux<ItemRsDto> foundItems = itemRepository.findAllByTitleOrDescription(search, pageSize, pageable.getOffset(), pageable.getSort(), userId);
                                Mono<Long> total = itemRepository.countItemsBySearch(search);

                                return getPageResultAndSaveCache(foundItems, total, key, pageable);
                            }
                        })
                        .onErrorResume(ex -> {
                            log.warn("Ошибка при извлечении товаров с главной страницы из кеша: {}", ex.getMessage());

                            Flux<ItemRsDto> foundItems = itemRepository.findAllByTitleOrDescription(search, pageSize, pageable.getOffset(), pageable.getSort(), userId);
                            Mono<Long> total = itemRepository.countItemsBySearch(search);

                            return foundItems
                                    .collectList()
                                    .zipWith(total)
                                    .map(tuple -> buildPageResult(tuple.getT1(), pageable, tuple.getT2()));
                        }));
    }

    private PageResult<ItemRsDto> buildPageResult(List<ItemRsDto> list, Pageable pageable, long totalCount) {
        List<List<ItemRsDto>> itemsToShow = Utils.splitList(list, rowSize);
        Page<ItemRsDto> page = new PageImpl<>(list, pageable, totalCount);
        return new PageResult<>(page, itemsToShow);
    }

    private Mono<PageResult<ItemRsDto>> getPageResultAndSaveCache(
            Flux<ItemRsDto> foundItems,
            Mono<Long> total,
            String key,
            Pageable pageable) {
        return foundItems
                .collectList()
                .zipWith(total)
                .flatMap(tuple -> {
                    List<ItemRsDto> items = tuple.getT1();
                    long totalCount = tuple.getT2();

                    List<CachedItem> cached = items.stream()
                            .map(itemMapper::toCachedItemMono)
                            .toList();

                    return cacheRepository.cacheMainItems(Flux.fromIterable(cached), key)
                            .doOnNext(cacheSavedRs -> {
                                if (Boolean.TRUE.equals(cacheSavedRs.saved()))
                                    log.info("Товары на главной странице ({} шт.) сохранены в кеш.", items.size());
                                else
                                    log.warn("Товары на главной странице ({} шт.) не были сохранены в кеш.", items.size());
                            })
                            .onErrorResume(e -> {
                                log.warn("Ошибка при сохранении страницы с товарами в кеш: {}", e.getMessage());
                                return Mono.empty();
                            })
                            .thenReturn(buildPageResult(items, pageable, totalCount));
                });
    }

    @Override
    public Mono<ItemRsDto> findItemById(Long id) {

        return securityUserContextHandler.getCurrentUserId()
                .defaultIfEmpty(-1L)
                .flatMap(userId -> cacheRepository.getItemById(id)
                        .flatMap(cachedItem -> {
                            if (cachedItem == null) return Mono.empty();
                            log.info("Товар с ID {} был извлечён из кеша.", id);
                            return itemMapper.toItemRsDtoMono(cachedItem);
                        })
                        .onErrorResume(ex -> {
                            log.warn("Ошибка при извлечении товара с ID {} из кеша: {}", id, ex.getMessage());
                            return Mono.empty();
                        })
                        .switchIfEmpty(
                                itemRepository.findItemById(id, userId)
                                        .switchIfEmpty(Mono.defer(() -> {
                                            log.error("Товар с указанным ID {} не был найден.", id);
                                            return Mono.error(new ObjectNotFoundException("Товар", id));
                                        }))
                                        .flatMap(itemRsDto -> {
                                            CachedItem cachedItem = itemMapper.toCachedItemMono(itemRsDto);
                                            return cacheRepository.cacheItem(cachedItem)
                                                    .doOnNext(cacheSavedRs -> {
                                                        if (Boolean.TRUE.equals(cacheSavedRs.saved())) log.info("Товар с ID {} сохранён в кеш.", id);
                                                        else log.warn("Ошибка сохранения товара с ID {} в кеш.", id);
                                                    })
                                                    .onErrorResume(e -> {
                                                        log.warn("Ошибка при сохранении товара {} в кеш: {}", id, e.getMessage());
                                                        return Mono.empty();
                                                    })
                                                    .thenReturn(itemRsDto);
                                        })
                        ));
    }
}
