package com.shop.easybuy.repository.item;

import com.shop.easybuy.entity.item.ItemRsDto;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;

public interface ItemRepositoryCustom {
    Flux<ItemRsDto> findAllByTitleOrDescription(String search, int limit, long offset, Sort sort, Long userId);
}

