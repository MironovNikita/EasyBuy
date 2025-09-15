package com.shop.easybuy.service;

import com.shop.easybuy.common.exception.CartEmptyException;
import com.shop.easybuy.common.exception.ObjectNotFoundException;
import com.shop.easybuy.entity.cart.CartViewDto;
import com.shop.easybuy.entity.item.Item;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.entity.order.Order;
import com.shop.easybuy.entity.order.OrderItem;
import com.shop.easybuy.mapper.ItemMapper;
import com.shop.easybuy.repository.OrderRepository;
import com.shop.easybuy.service.cart.CartService;
import com.shop.easybuy.service.order.OrderServiceImpl;
import com.shop.easybuy.utils.Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.shop.easybuy.DataCreator.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("Покупка товаров в корзине")
    void shouldBuyItemsInCart() {
        Long orderId = 1L;
        Long total = 1000L;
        ItemRsDto itemRsDto = createItemRsDto();
        itemRsDto.setId(1L);
        Item item = createItem();
        item.setId(1L);
        List<List<ItemRsDto>> foundItems = Utils.splitList(List.of(itemRsDto), 5);
        CartViewDto cartViewDto = new CartViewDto(foundItems, total);
        Order savedOrder = createOrder();
        savedOrder.setId(orderId);
        savedOrder.setItems(List.of(new OrderItem(1L, savedOrder, item, 10)));
        savedOrder.setTotal(total);
        savedOrder.setCreatedAt(LocalDateTime.now());

        when(cartService.getAllItems()).thenReturn(cartViewDto);
        when(itemMapper.toItem(itemRsDto)).thenReturn(item);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order order = orderService.buyItemsInCart();

        assertEquals(orderId, order.getId());
        assertEquals(1, order.getItems().size());

        OrderItem orderItem = order.getItems().getFirst();
        assertEquals(orderItem.getItem().getId(), item.getId());
        assertEquals(orderItem.getItem().getTitle(), item.getTitle());
        assertEquals(orderItem.getItem().getDescription(), item.getDescription());
        assertEquals(orderItem.getItem().getImagePath(), item.getImagePath());
        assertEquals(orderItem.getItem().getPrice(), item.getPrice());
        assertEquals(itemRsDto.getCount(), orderItem.getCount());
        assertEquals(order.getTotal(), total);
        assertNotNull(order.getCreatedAt());
        verify(cartService).clearCart();
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Ошибка при покупке товаров с пустой корзиной")
    void shouldThrowCartEmptyExceptionIfCartIsEmpty() {

        Long total = 1000L;
        CartViewDto cartViewDto = new CartViewDto(Collections.emptyList(), total);

        when(cartService.getAllItems()).thenReturn(cartViewDto);

        assertThrows(CartEmptyException.class, () -> orderService.buyItemsInCart());
        verify(cartService, never()).clearCart();
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Поиск заказа по ID")
    void shouldFindOrderById() {
        Long orderId = 1L;
        Item item = createItem();
        Order order = createOrder();
        order.setId(orderId);
        order.setItems(List.of(new OrderItem(1L, order, item, 10)));
        order.setTotal(1000L);
        order.setCreatedAt(LocalDateTime.now());

        when(orderRepository.findOrderByOrderId(orderId)).thenReturn(Optional.of(order));

        Order foundOrder = orderService.findById(orderId);

        assertEquals(orderId, foundOrder.getId());
        assertEquals(1, order.getItems().size());

        verify(orderRepository).findOrderByOrderId(orderId);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    @DisplayName("Заказ по ID не найден")
    void shouldThrowObjectNotFoundExceptionIfOrderNotExists() {
        Long orderId = 1L;
        when(orderRepository.findOrderByOrderId(anyLong())).thenThrow(ObjectNotFoundException.class);

        assertThrows(ObjectNotFoundException.class, () -> orderService.findById(orderId));

        verify(orderRepository).findOrderByOrderId(orderId);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    @DisplayName("Поиск всех существующих заказов")
    void shouldFindAllOrders() {
        Item item = createItem();
        Order order1 = createOrder();
        order1.setId(1L);
        order1.setItems(List.of(new OrderItem(1L, order1, item, 10)));
        order1.setTotal(1000L);
        order1.setCreatedAt(LocalDateTime.now());

        Order order2 = createOrder();
        order2.setId(2L);
        order2.setItems(List.of(new OrderItem(1L, order1, item, 10)));
        order2.setTotal(1000L);
        order2.setCreatedAt(LocalDateTime.now());

        when(orderRepository.findAllOrders()).thenReturn(List.of(order1, order2));

        List<Order> foundOrders = orderService.findAll();

        assertEquals(2, foundOrders.size());
        assertTrue(foundOrders.contains(order1));
        assertTrue(foundOrders.contains(order2));

        verify(orderRepository).findAllOrders();
        verifyNoMoreInteractions(orderRepository);
    }
}
