package com.shop.easybuy.service.item;

import com.shop.easybuy.common.entity.PageResult;
import com.shop.easybuy.entity.item.ItemRsDto;
import org.springframework.data.domain.Pageable;

public interface ItemService {

    PageResult<ItemRsDto> getAllByParams(String search, Pageable pageable);

    ItemRsDto findItemById(Long id);
}
