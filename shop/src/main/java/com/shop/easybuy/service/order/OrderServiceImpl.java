package com.shop.easybuy.service.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.easybuy.client.api.payment.PaymentApi;
import com.shop.easybuy.client.model.payment.BalanceRs;
import com.shop.easybuy.client.model.payment.ErrorRs;
import com.shop.easybuy.client.model.payment.PaymentRq;
import com.shop.easybuy.common.exception.CartEmptyException;
import com.shop.easybuy.common.exception.ObjectNotFoundException;
import com.shop.easybuy.common.exception.PaymentFailedException;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.entity.order.*;
import com.shop.easybuy.repository.order.OrderItemRepository;
import com.shop.easybuy.repository.order.OrderRepository;
import com.shop.easybuy.service.cart.CartService;
import com.shop.easybuy.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final CartService cartService;

    private final PaymentApi paymentApi;

    private final ObjectMapper objectMapper;

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
                    var total = found.getTotalCount();

                    PaymentRq paymentRq = new PaymentRq();
                    paymentRq.setAmount(total);

                    return paymentApi.payWithHttpInfo(paymentRq)
                            .flatMap(rs -> {
                                BalanceRs balanceRs = Objects.requireNonNull(rs.getBody());
                                log.info("Списание средств успешно произведено. Текущий баланс: {}. Формируем заказ...", balanceRs.getBalance());
                                return createOrder(items, total);
                            })
                            .onErrorResume(WebClientResponseException.class, e -> {
                                ErrorRs error;
                                try {
                                    error = objectMapper.readValue(e.getResponseBodyAsString(), ErrorRs.class);
                                } catch (JsonProcessingException ex) {
                                    error = new ErrorRs();
                                    error.setErrorCode(String.valueOf(e.getStatusCode().value()));
                                    error.setErrorInfo(e.getMessage());
                                }
                                log.error("Ошибка списания средств: {}", error.getErrorInfo());
                                return Mono.error(new PaymentFailedException(error));
                            });
                })
                .flatMap(orderDto -> cartService.clearCart().thenReturn(orderDto))
                .doOnSuccess(orderRsDto -> log.info("Сформирован заказ с ID {} и количеством товаров {}.", orderRsDto.getId(), orderRsDto.getItems().size()));
    }

    private Mono<OrderRsDto> createOrder(List<ItemRsDto> items, Long total) {
        Order order = new Order();
        order.setTotal(total);
        order.setCreated(LocalDateTime.now());

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
                                            ItemRsDto original = items.stream()
                                                    .filter(i -> i.id().equals(oi.getItemId()))
                                                    .findFirst()
                                                    .orElseThrow(() -> {
                                                        log.error("Item c ID {} не найден при формировании заказа", oi.getItemId());
                                                        return new IllegalStateException("Item c ID %d не найден при формировании заказа"
                                                                .formatted(oi.getItemId()));
                                                    });

                                            return new OrderItemDto(
                                                    oi.getId(),
                                                    oi.getItemId(),
                                                    original.title(),
                                                    original.description(),
                                                    original.image(),
                                                    original.price(),
                                                    oi.getCount()
                                            );
                                        }).toList();

                                return new OrderRsDto(
                                        order.getId(),
                                        order.getTotal(),
                                        order.getCreated(),
                                        itemDtos
                                );
                            });
                });
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
