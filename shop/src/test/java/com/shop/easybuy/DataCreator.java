package com.shop.easybuy;

import com.shop.easybuy.entity.item.Item;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.entity.order.Order;
import com.shop.easybuy.entity.order.OrderFlatDto;
import com.shop.easybuy.entity.order.OrderItem;
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

    public static ItemRsDto createItemRsDto(Long itemId) {
        return new ItemRsDto(
                itemId,
                "Test title",
                "Test description",
                "Test image",
                10L,
                1000L
        );
    }

    public static Item createItem(Long itemId) {
        Item item = new Item();
        item.setId(itemId);
        item.setTitle("Test title");
        item.setDescription("Test description");
        item.setImage("Test image");
        item.setPrice(1000L);
        return item;
    }

    public OrderItem createOrderItem(Long id, Long orderId, Long itemId) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(id);
        orderItem.setOrderId(orderId);
        orderItem.setItemId(itemId);
        orderItem.setCount(10L);
        return orderItem;
    }

    public OrderFlatDto createOrderFlatDto(Long orderId, Long orderItemId, Long itemId) {
        return new OrderFlatDto(
                orderId,
                10000L,
                LocalDateTime.now(),
                orderItemId,
                10L,
                itemId,
                "Test title",
                "Test description",
                "Test image",
                1000L
        );
    }
}
