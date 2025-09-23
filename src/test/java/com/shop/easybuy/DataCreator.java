package com.shop.easybuy;

import com.shop.easybuy.entity.item.Item;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.entity.order.Order;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DataCreator {

    public static Item createItem() {
        Item item = new Item();
        item.setTitle("Test title");
        item.setDescription("Test description");
        item.setImagePath("Test image");
        item.setPrice(1000L);
        return item;
    }

    public static Order createOrder() {
        return new Order();
    }
/**
    public static ItemRsDto createItemRsDto() {
        ItemRsDto itemRsDto = new ItemRsDto();
        itemRsDto.setTitle("Test title");
        itemRsDto.setDescription("Test description");
        itemRsDto.setImagePath("Test image");
        itemRsDto.setPrice(1000L);
        itemRsDto.setCount(10);
        return itemRsDto;
    }
 */
}
