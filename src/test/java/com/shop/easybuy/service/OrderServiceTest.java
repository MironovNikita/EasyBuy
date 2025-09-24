package com.shop.easybuy.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
/*
    @Mock
    private OrderRepositoryOld orderRepositoryOld;

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
        when(orderRepositoryOld.save(any(Order.class))).thenReturn(savedOrder);

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
        verify(orderRepositoryOld).save(any(Order.class));
    }

    @Test
    @DisplayName("Ошибка при покупке товаров с пустой корзиной")
    void shouldThrowCartEmptyExceptionIfCartIsEmpty() {

        Long total = 1000L;
        CartViewDto cartViewDto = new CartViewDto(Collections.emptyList(), total);

        when(cartService.getAllItems()).thenReturn(cartViewDto);

        assertThrows(CartEmptyException.class, () -> orderService.buyItemsInCart());
        verify(cartService, never()).clearCart();
        verify(orderRepositoryOld, never()).save(any(Order.class));
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

        when(orderRepositoryOld.findOrderByOrderId(orderId)).thenReturn(Optional.of(order));

        Order foundOrder = orderService.findById(orderId);

        assertEquals(orderId, foundOrder.getId());
        assertEquals(1, order.getItems().size());

        verify(orderRepositoryOld).findOrderByOrderId(orderId);
        verifyNoMoreInteractions(orderRepositoryOld);
    }

    @Test
    @DisplayName("Заказ по ID не найден")
    void shouldThrowObjectNotFoundExceptionIfOrderNotExists() {
        Long orderId = 1L;
        when(orderRepositoryOld.findOrderByOrderId(anyLong())).thenThrow(ObjectNotFoundException.class);

        assertThrows(ObjectNotFoundException.class, () -> orderService.findById(orderId));

        verify(orderRepositoryOld).findOrderByOrderId(orderId);
        verifyNoMoreInteractions(orderRepositoryOld);
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

        when(orderRepositoryOld.findAllOrders()).thenReturn(List.of(order1, order2));

        List<Order> foundOrders = orderService.findAll();

        assertEquals(2, foundOrders.size());
        assertTrue(foundOrders.contains(order1));
        assertTrue(foundOrders.contains(order2));

        verify(orderRepositoryOld).findAllOrders();
        verifyNoMoreInteractions(orderRepositoryOld);
    }
    */
}
