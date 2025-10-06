package com.shop.easybuy.service.item;

import com.shop.easybuy.common.entity.PageResult;
import com.shop.easybuy.common.exception.ObjectNotFoundException;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.repository.item.ItemRepository;
import com.shop.easybuy.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private static final int rowSize = 5;

    private final ItemRepository itemRepository;

    @Override
    public Mono<PageResult<ItemRsDto>> getAllByParams(String search, Pageable pageable) {

        Flux<ItemRsDto> foundItems = itemRepository.findAllByTitleOrDescription(search, pageable.getPageSize(), pageable.getOffset(), pageable.getSort());
        Mono<Long> total = itemRepository.countItemsBySearch(search);

        return foundItems
                .collectList()
                .zipWith(total)
                .map(tuple -> {
                    List<ItemRsDto> list = tuple.getT1();
                    long totalCount = tuple.getT2();

                    List<List<ItemRsDto>> itemsToShow = Utils.splitList(list, rowSize);
                    Page<ItemRsDto> page = new PageImpl<>(list, pageable, totalCount);
                    log.info("По строке поиска \"{}\" было выведено {} товаров на главную страницу.", search, totalCount);
                    return new PageResult<>(page, itemsToShow);
                });
    }

    @Override
    public Mono<ItemRsDto> findItemById(Long id) {

        return itemRepository.findItemById(id)
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Товар с указанным ID {} не был найден.", id);
                    return Mono.error(new ObjectNotFoundException("Товар", id));
                }))
                .doOnNext(item -> log.info("Запрошенный по ID {} товар \"{}\" был найден.", id, item.title()));
    }
}
