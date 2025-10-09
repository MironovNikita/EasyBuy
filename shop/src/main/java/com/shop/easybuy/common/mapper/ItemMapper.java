package com.shop.easybuy.common.mapper;

import com.shop.easybuy.client.model.cache.CachedItem;
import com.shop.easybuy.entity.item.ItemRsDto;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    //TODO Что делать с count?
    //TODO Добавить запрос количества товара в репозиторий и добавить в маппер!!!
    public ItemRsDto toItemRsDto(CachedItem cachedItem) {
        return new ItemRsDto(
                cachedItem.getId(),
                cachedItem.getTitle(),
                cachedItem.getDescription(),
                cachedItem.getImage(),
                null,
                cachedItem.getPrice()
        );
    }
}
