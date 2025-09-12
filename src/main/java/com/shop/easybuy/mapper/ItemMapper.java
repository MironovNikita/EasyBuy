package com.shop.easybuy.mapper;

import com.shop.easybuy.entity.item.Item;
import com.shop.easybuy.entity.item.ItemResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "count", ignore = true)
    ItemResponseDto convertItemForRs(Item item);
}
