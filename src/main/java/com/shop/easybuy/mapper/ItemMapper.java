package com.shop.easybuy.mapper;

import com.shop.easybuy.entity.item.Item;
import com.shop.easybuy.entity.item.ItemResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public ItemResponseDto convertItemForRs(Item item) {
        return new ItemResponseDto()
                .setId(item.getId())
                .setTitle(item.getTitle())
                .setDescription(item.getDescription())
                .setImagePath(item.getImagePath())
                .setCount(0)
                .setPrice(item.getPrice());
    }
}
