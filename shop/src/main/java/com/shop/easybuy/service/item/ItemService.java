package com.shop.easybuy.service.item;

import com.shop.easybuy.common.entity.PageResult;
import com.shop.easybuy.common.entity.SortEnum;
import com.shop.easybuy.entity.item.ItemRsDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface ItemService {

    Mono<PageResult<ItemRsDto>> getAllByParams(String search, Pageable pageable, SortEnum sort);

    Mono<ItemRsDto> findItemById(Long id);
}
