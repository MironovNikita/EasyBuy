package com.shop.easybuy;

import com.shop.easybuy.entity.order.Order;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class DataCreator {

    public static Order createOrder() {
        Order order = new Order();
        order.setTotal(10000L);
        order.setCreated(LocalDateTime.now());
        return order;
    }

    /*public static Item createItem() {
        Item item = new Item();
        item.setTitle("Test title");
        item.setDescription("Test description");
        item.setImage("Test image");
        item.setPrice(1000L);
        return item;
    }*/

    /*private Item createItem() {
        Item item = new Item();
        item.setTitle("Test title");
        item.setDescription("Test description");
        item.setImage("Test image");
        item.setPrice(1000L);
        return itemRepository.save(item).block();
    }*/

/*
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
