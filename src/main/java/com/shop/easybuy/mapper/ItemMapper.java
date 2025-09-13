package com.shop.easybuy.mapper;

import com.shop.easybuy.entity.item.Item;
import com.shop.easybuy.entity.item.ItemRsDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item toItem(ItemRsDto itemRsDto);
}
