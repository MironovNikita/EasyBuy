package com.shop.easybuy.repository;

import com.shop.easybuy.entity.order.Order;
import com.shop.easybuy.entity.order.OrderItem;
import com.shop.easybuy.repository.order.OrderItemRepository;
import com.shop.easybuy.repository.order.OrderRepository;
import com.shop.easybuy.testDB.AbstractRepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static com.shop.easybuy.DataCreator.createOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("Поиск заказа по ID")
    void shouldFindOrderById() {
        Order order = createOrder();

        StepVerifier.create(orderRepository.save(order)
                        .flatMap(savedOrder -> orderItemRepository.save(new OrderItem(null, 1L, 1L, 2L))
                                .thenReturn(savedOrder))
                        .flatMapMany(savedOrder -> orderRepository.findByOrderId(savedOrder.getId())))
                .assertNext(found -> {
                    assertThat(found).isNotNull();
                    assertThat(found.orderId()).isEqualTo(order.getId());
                    assertThat(found.orderTotal()).isEqualTo(order.getTotal());
                    assertThat(found.itemId()).isEqualTo(1L);
                    assertThat(found.orderItemCount()).isEqualTo(2);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Поиск заказа по ID - заказ не найден")
    void shouldNotFindOrderById() {

        StepVerifier.create(orderRepository.findByOrderId(9999L))
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Поиск всех заказов")
    void shouldFindAllOrders() {
        Order order1 = createOrder();
        Order order2 = createOrder();

        Mono<Void> setup = orderRepository.save(order1)
                .then(orderRepository.save(order2))
                .then(orderItemRepository.save(new OrderItem(null, 1L, 1L, 2L)))
                .then(orderItemRepository.save(new OrderItem(null, 2L, 1L, 2L)))
                .then();

        StepVerifier.create(setup.thenMany(orderRepository.findAllOrders()))
                .recordWith(ArrayList::new)
                .expectNextCount(2L)
                .consumeRecordedWith(orders -> {
                    assertThat(orders).isNotNull();
                    assertThat(orders.size()).isEqualTo(2);
                    assertTrue(orders.stream().anyMatch(orderFlatDto -> orderFlatDto.orderId().equals(order1.getId())));
                    assertTrue(orders.stream().anyMatch(orderFlatDto -> orderFlatDto.orderId().equals(order2.getId())));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Поиск всех заказов - заказы не найдены")
    void shouldFindNoOrders() {

        StepVerifier.create(orderRepository.findAllOrders())
                .expectNextCount(0L)
                .verifyComplete();
    }
}
