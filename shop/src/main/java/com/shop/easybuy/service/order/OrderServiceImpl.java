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
import com.shop.easybuy.common.security.SecurityUserContextHandler;
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

    private final SecurityUserContextHandler securityUserContextHandler;

    @Override
    @Transactional
    public Mono<OrderRsDto> buyItemsInCartByUserId(Long userId) {

        return securityUserContextHandler.checkUserIdOrThrow(userId)
                .then(cartService.getAllItemsByUserId(userId)
                        .flatMap(found -> {
                            if (found.getFoundItems().isEmpty()) return Mono.error(() -> {
                                log.error("Невозможно оформить заказ. Корзина пуста.");
                                return new CartEmptyException();
                            });
                            var items = Utils.mergeList(found.getFoundItems());
                            var total = found.getTotalCount();

                            PaymentRq paymentRq = new PaymentRq();
                            paymentRq.setUserId(userId);
                            paymentRq.setAmount(total);

                            return paymentApi.payWithHttpInfo(paymentRq)
                                    .flatMap(rs -> {
                                        BalanceRs balanceRs = Objects.requireNonNull(rs.getBody());
                                        log.info("Списание средств успешно произведено. Текущий баланс пользователя с ID {}: {}. Формируем заказ...",
                                                userId, balanceRs.getBalance());
                                        return createOrder(items, total, userId);
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
                        .flatMap(orderDto -> cartService.clearUserCartById(userId).thenReturn(orderDto))
                        .doOnSuccess(orderRsDto -> log.info("Сформирован заказ с ID {} и количеством товаров {}.",
                                orderRsDto.getId(), orderRsDto.getItems().size())));
    }

    private Mono<OrderRsDto> createOrder(List<ItemRsDto> items, Long total, Long userId) {
        Order order = new Order();
        order.setTotal(total);
        order.setCreated(LocalDateTime.now());
        order.setUserId(userId);

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
    public Mono<OrderRsDto> findByIdAndUserId(Long id, Long userId) {

        return securityUserContextHandler.checkUserIdOrThrow(userId)
                .then(orderRepository.findByOrderIdAndUserId(id, userId)
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
                            )).doOnSuccess(foundOrder -> log.info("Для пользователя с ID {} найден заказ с ID {} и количеством товаров {}.",
                                    userId, foundOrder.getId(), foundOrder.getItems().size()));
                        }));
    }

    @Override
    public Flux<OrderRsDto> findAllByUserId(Long userId) {

        return securityUserContextHandler.checkUserIdOrThrow(userId)
                .thenMany(orderRepository.findAllOrdersByUserId(userId)
                        .collectList()
                        .flatMapMany(rows -> {
                            Map<Long, List<OrderFlatDto>> grouped = rows.stream()
                                    .collect(Collectors.groupingBy(OrderFlatDto::orderId));

                            log.info("Найдено {} заказов для пользователя с ID {}.", grouped.size(), userId);

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
                        }));
    }
}
