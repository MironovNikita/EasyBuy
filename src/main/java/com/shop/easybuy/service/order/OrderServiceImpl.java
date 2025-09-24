package com.shop.easybuy.service.order;

import com.shop.easybuy.common.exception.CartEmptyException;
import com.shop.easybuy.common.exception.ObjectNotFoundException;
import com.shop.easybuy.entity.order.*;
import com.shop.easybuy.mapper.ItemMapper;
import com.shop.easybuy.repository.OrderItemRepository;
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

    private final OrderItemRepository orderItemRepository;

    private final CartService cartService;

    @Override
    @Transactional
    public Mono<OrderRsDto> buyItemsInCart() {

        return cartService.getAllItems()
                .flatMap(found -> {
                    if (found.getFoundItems().isEmpty()) return Mono.error(() -> {
                        log.error("Невозможно оформить заказ. Корзина пуста.");
                        return new CartEmptyException();
                    });
                    var items = Utils.mergeList(found.getFoundItems());

                    Order order = new Order();
                    order.setTotal(found.getTotalCount());
                    order.setCreatedAt(LocalDateTime.now());

                    return orderRepository.save(order)
                            .flatMap(savedOrder -> {
                                List<OrderItem> orderedItems = items.stream()
                                        .map(itemRsDto -> OrderItem.builder()
                                                .orderId(savedOrder.getId())
                                                .itemId(itemRsDto.id())
                                                .count(itemRsDto.count())
                                                .build())
                                        .toList();

                                return Flux.fromIterable(orderedItems)
                                        .flatMap(orderItemRepository::save)
                                        .collectList()
                                        .map(savedItems -> {
                                            List<OrderItemDto> itemDtos = savedItems.stream()
                                                    .map(oi -> {
                                                        OrderItemDto dto = new OrderItemDto();
                                                        dto.setId(oi.getId());
                                                        dto.setItemId(oi.getItemId());

                                                        items.stream()
                                                                .filter(i -> i.id().equals(oi.getItemId()))
                                                                .findFirst()
                                                                .ifPresent(original -> {
                                                                    dto.setTitle(original.title());
                                                                    dto.setDescription(original.description());
                                                                    dto.setImagePath(original.imagePath());
                                                                    dto.setPrice(original.price());
                                                                });

                                                        dto.setCount(oi.getCount());
                                                        return dto;
                                                    }).toList();

                                            return new OrderRsDto(
                                                    order.getId(),
                                                    order.getTotal(),
                                                    order.getCreatedAt(),
                                                    itemDtos
                                            );
                                        });
                            });

                })
                .flatMap(orderDto -> cartService.clearCart().thenReturn(orderDto))
                .doOnSuccess(orderRsDto -> log.info("Сформирован заказ с ID {} и количеством товаров {}.", orderRsDto.getId(), orderRsDto.getItems().size()));
    }

    @Override
    public Mono<OrderRsDto> findById(Long id) {

        return orderRepository.findByOrderId(id)
                .collectList()
                .flatMap(rows -> {
                    if (rows.isEmpty()) {
                        return Mono.error(new ObjectNotFoundException("Заказ", id));
                    }

                    var first = rows.getFirst();
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
                                var first = orderRows.getFirst();
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
