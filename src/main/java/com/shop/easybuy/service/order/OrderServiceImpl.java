package com.shop.easybuy.service.order;

import com.shop.easybuy.common.exception.CartEmptyException;
import com.shop.easybuy.common.exception.ObjectNotFoundException;
import com.shop.easybuy.entity.order.*;
import com.shop.easybuy.mapper.ItemMapper;
import com.shop.easybuy.repository.OrderRepository;
import com.shop.easybuy.service.cart.CartService;
import com.shop.easybuy.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
//TODO Навести порядок
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final ItemMapper itemMapper;

    private final CartService cartService;

    @Override
    @Transactional
    public Order buyItemsInCart() {

        var foundInCart = cartService.getAllItems();

        if (foundInCart.getFoundItems().isEmpty()) {
            log.error("Невозможно оформить заказ. Корзина пуста.");
            throw new CartEmptyException();
        }

        var items = Utils.mergeList(foundInCart.getFoundItems());
        Order order = new Order();

        List<OrderItem> orderedItems = items.stream()
                .map(itemRsDto -> OrderItem
                        .builder()
                        .item(itemMapper.toItem(itemRsDto))
                        .order(order)
                        .count(itemRsDto.getCount())
                        .build())
                .toList();

        order.setItems(orderedItems);
        order.setTotal(foundInCart.getTotalCount());
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart();

        log.info("Сформирован заказ с ID {} и количеством товаров {}.", savedOrder.getId(), savedOrder.getItems().size());

        return savedOrder;
    }

    @Override
    public Mono<OrderRsDto> findById(Long id) {

        return orderRepository.findByOrderId(id)
                .collectList()
                .flatMap(rows -> {
                    if (rows.isEmpty()) {
                        return Mono.error(new ObjectNotFoundException("Заказ", id));
                    }

                    var first = rows.get(0);
                    var items = rows.stream()
                            .map(r -> new OrderItemDto(
                                    r.orderItemId(),
                                    r.itemId(),
                                    r.itemTitle(),
                                    r.itemDescription(),
                                    r.itemImagePath(),
                                    r.itemPrice(),
                                    r.orderItemCount()
                            )).toList();

                    return Mono.just(new OrderRsDto(
                            first.orderId(),
                            first.orderTotal(),
                            first.orderCreatedAt(),
                            items
                    )).doOnSuccess(foundOrder -> log.info("Найден заказ с ID {} и количеством товаров {}.", foundOrder.getId(), foundOrder.getItems().size()));
                });
    }

    @Override
    public Flux<OrderRsDto> findAll() {

        return orderRepository.findAllOrders()
                .collectList()
                .flatMapMany(rows -> {
                    Map<Long, List<OrderFlatDto>> grouped = rows.stream()
                            .collect(Collectors.groupingBy(OrderFlatDto::orderId));

                    log.info("Найдено {} заказов.", grouped.size());

                    return Flux.fromIterable(grouped.values())
                            .map(orderRows -> {
                                var first = orderRows.get(0);
                                var items = orderRows.stream()
                                        .map(r -> new OrderItemDto(
                                                r.orderItemId(),
                                                r.itemId(),
                                                r.itemTitle(),
                                                r.itemDescription(),
                                                r.itemImagePath(),
                                                r.itemPrice(),
                                                r.orderItemCount()
                                        ))
                                        .toList();

                                return new OrderRsDto(
                                        first.orderId(),
                                        first.orderTotal(),
                                        first.orderCreatedAt(),
                                        items
                                );
                            });
                });
    }
}
