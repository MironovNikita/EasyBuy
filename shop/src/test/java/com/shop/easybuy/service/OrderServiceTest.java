package com.shop.easybuy.service;

import com.shop.easybuy.client.api.payment.PaymentApi;
import com.shop.easybuy.client.model.payment.BalanceRs;
import com.shop.easybuy.common.exception.CartEmptyException;
import com.shop.easybuy.common.exception.ObjectNotFoundException;
import com.shop.easybuy.entity.cart.CartViewDto;
import com.shop.easybuy.entity.item.Item;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.entity.order.Order;
import com.shop.easybuy.entity.order.OrderFlatDto;
import com.shop.easybuy.entity.order.OrderItem;
import com.shop.easybuy.repository.order.OrderItemRepository;
import com.shop.easybuy.repository.order.OrderRepository;
import com.shop.easybuy.service.cart.CartService;
import com.shop.easybuy.service.order.OrderServiceImpl;
import com.shop.easybuy.utils.Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.shop.easybuy.DataCreator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartService cartService;

    @Mock
    private PaymentApi paymentApi;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("Покупка товаров в корзине")
    void shouldBuyItemsInCart() {
        Long orderItemId = 1L;
        Long itemId = 1L;
        Long orderId = 1L;
        Long total = 1000L;
        ItemRsDto itemRsDto = createItemRsDto(1L);
        Item item = createItem(itemId);

        List<List<ItemRsDto>> foundItems = Utils.splitList(List.of(itemRsDto), 5);
        CartViewDto cartViewDto = new CartViewDto(foundItems, total, true, true);
        Order savedOrder = createOrder();
        savedOrder.setId(orderId);
        savedOrder.setTotal(total);
        savedOrder.setCreated(LocalDateTime.now());
        OrderItem orderItem = createOrderItem(orderItemId, orderId, itemId);

        when(cartService.getAllItems()).thenReturn(Mono.just(cartViewDto));
        when(paymentApi.payWithHttpInfo(any())).thenReturn(Mono.just(ResponseEntity.ok(new BalanceRs().balance(15000L))));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order order = inv.getArgument(0);
            order.setId(orderId);
            return Mono.just(order);
        });
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(inv -> {
            OrderItem orderItem1 = inv.getArgument(0);
            orderItem1.setId(orderItemId);
            return Mono.just(orderItem1);
        });
        when(cartService.clearCart()).thenReturn(Mono.empty());

        StepVerifier.create(orderService.buyItemsInCart())
                .assertNext(orderRsDto -> {
                    assertEquals(orderId, orderRsDto.getId());
                    assertEquals(total, orderRsDto.getTotal());
                    assertEquals(1, orderRsDto.getItems().size());
                    assertEquals(itemId, orderRsDto.getItems().getFirst().getId());
                    assertEquals(item.getTitle(), orderRsDto.getItems().getFirst().getTitle());
                    assertEquals(item.getDescription(), orderRsDto.getItems().getFirst().getDescription());
                    assertEquals(item.getPrice(), orderRsDto.getItems().getFirst().getPrice());
                    assertEquals(item.getImage(), orderRsDto.getItems().getFirst().getImagePath());
                })
                .verifyComplete();

        verify(cartService).getAllItems();
        verify(paymentApi).payWithHttpInfo(any());
        verify(orderRepository).save(argThat(order -> order.getTotal().equals(total)));
        verify(orderItemRepository).save(argThat(orderedItem -> orderedItem.getCount().equals(orderItem.getCount())));
        verify(cartService).clearCart();
    }

    @Test
    @DisplayName("Ошибка при покупке товаров с пустой корзиной")
    void shouldThrowCartEmptyExceptionIfCartIsEmpty() {

        Long total = 1000L;
        CartViewDto cartViewDto = new CartViewDto(Collections.emptyList(), total, true, true);

        when(cartService.getAllItems()).thenReturn(Mono.just(cartViewDto));

        StepVerifier.create(orderService.buyItemsInCart())
                .expectErrorMatches(throwable -> throwable instanceof CartEmptyException)
                .verify();

        verify(cartService, never()).clearCart();
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Поиск заказа по ID")
    void shouldFindOrderById() {
        Long orderId = 1L;
        Long orderItemId = 1L;
        Long itemId = 1L;
        OrderFlatDto orderFlatDto = createOrderFlatDto(orderId, orderItemId, itemId);

        when(orderRepository.findByOrderId(orderId)).thenReturn(Flux.just(orderFlatDto));

        StepVerifier.create(orderService.findById(orderId))
                .assertNext(orderRsDto -> {
                    assertEquals(orderId, orderRsDto.getId());
                    assertEquals(orderItemId, orderRsDto.getItems().getFirst().getId());
                    assertEquals(itemId, orderRsDto.getItems().getFirst().getItemId());
                    assertEquals(1, orderRsDto.getItems().size());
                })
                .verifyComplete();

        verify(orderRepository).findByOrderId(orderId);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    @DisplayName("Заказ по ID не найден")
    void shouldThrowObjectNotFoundExceptionIfOrderNotExists() {
        Long orderId = 1L;

        when(orderRepository.findByOrderId(orderId)).thenReturn(Flux.error(new ObjectNotFoundException("Заказ", orderId)));

        StepVerifier.create(orderService.findById(orderId))
                .expectErrorMatches(throwable ->
                        throwable instanceof ObjectNotFoundException &&
                                throwable.getMessage().contains(orderId.toString()))
                .verify();

        verify(orderRepository).findByOrderId(orderId);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    @DisplayName("Поиск всех существующих заказов")
    void shouldFindAllOrders() {
        Long itemId = 1L;
        Long orderId1 = 1L;
        Long orderId2 = 2L;
        Long orderItemId1 = 1L;
        Long orderItemId2 = 2L;
        OrderFlatDto orderFlatDto1 = createOrderFlatDto(orderId1, orderItemId1, itemId);
        OrderFlatDto orderFlatDto2 = createOrderFlatDto(orderId2, orderItemId2, itemId);

        when(orderRepository.findAllOrders()).thenReturn(Flux.just(orderFlatDto1, orderFlatDto2));

        StepVerifier.create(orderService.findAll())
                .assertNext(orderRsDto1 -> {
                    assertEquals(orderId1, orderRsDto1.getId());
                    assertEquals(10000L, orderRsDto1.getTotal());
                    assertEquals(1, orderRsDto1.getItems().size());
                })
                .assertNext(orderRsDto2 -> {
                    assertEquals(orderId2, orderRsDto2.getId());
                    assertEquals(10000L, orderRsDto2.getTotal());
                    assertEquals(1, orderRsDto2.getItems().size());
                })
                .verifyComplete();

        verify(orderRepository).findAllOrders();
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    @DisplayName("Возврат пустого списка, если заказов нет")
    void shouldReturnEmptyFluxIfNoOrders() {
        when(orderRepository.findAllOrders()).thenReturn(Flux.empty());

        StepVerifier.create(orderService.findAll())
                .expectNextCount(0)
                .verifyComplete();

        verify(orderRepository).findAllOrders();
    }

}
