package com.shop.easybuy.repository;

import com.shop.easybuy.annotation.JpaTestConfig;
import com.shop.easybuy.common.exception.ObjectNotFoundException;
import com.shop.easybuy.entity.item.Item;
import com.shop.easybuy.entity.order.Order;
import com.shop.easybuy.entity.order.OrderItem;
import com.shop.easybuy.testDB.AbstractTestDatabase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static com.shop.easybuy.DataCreator.createItem;
import static com.shop.easybuy.DataCreator.createOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JpaTestConfig
@ActiveProfiles("test")
public class OrderRepositoryTest extends AbstractTestDatabase {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("Проверка поиска заказа по ID")
    void shouldFindOrderById() {
        Item item1 = createItem();
        Item item2 = createItem();

        itemRepository.save(item1);
        itemRepository.save(item2);

        Order order = createOrder();
        List<OrderItem> orderedItems = List.of(new OrderItem(null, order, item1, 3), new OrderItem(null, order, item2, 2));
        order.setItems(orderedItems);
        order.setTotal(1000L);
        order.setCreatedAt(LocalDateTime.of(2025, 1, 1, 12, 0));

        orderRepository.save(order);

        Order foundOrder = orderRepository.findOrderByOrderId(order.getId()).orElseThrow(() -> new ObjectNotFoundException("Заказ", order.getId()));

        assertEquals(order.getId(), foundOrder.getId());
        assertEquals(order.getCreatedAt(), foundOrder.getCreatedAt());
        assertEquals(order.getTotal(), foundOrder.getTotal());
        assertEquals(order.getItems(), foundOrder.getItems());
    }

    @Test
    @DisplayName("Проверка поиска всех заказов")
    void shouldFindAllOrders() {
        Item item1 = createItem();
        Item item2 = createItem();

        itemRepository.save(item1);
        itemRepository.save(item2);

        Order order1 = createOrder();
        List<OrderItem> orderedItems1 = List.of(new OrderItem(null, order1, item1, 3));
        order1.setItems(orderedItems1);
        order1.setTotal(1000L);
        order1.setCreatedAt(LocalDateTime.of(2025, 1, 1, 12, 0));
        Order order2 = createOrder();
        List<OrderItem> orderedItems2 = List.of(new OrderItem(null, order2, item2, 3));
        order2.setItems(orderedItems2);
        order2.setTotal(100L);
        order2.setCreatedAt(LocalDateTime.of(2025, 1, 2, 12, 0));

        orderRepository.save(order1);
        orderRepository.save(order2);

        List<Order> foundOrders = orderRepository.findAllOrders();

        assertEquals(2, foundOrders.size());
        assertTrue(foundOrders.contains(order1));
        assertTrue(foundOrders.contains(order2));
    }
}
