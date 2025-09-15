package com.shop.easybuy.service.order;

import com.shop.easybuy.common.exception.CartEmptyException;
import com.shop.easybuy.common.exception.ObjectNotFoundException;
import com.shop.easybuy.entity.order.Order;
import com.shop.easybuy.entity.order.OrderItem;
import com.shop.easybuy.mapper.ItemMapper;
import com.shop.easybuy.repository.OrderRepository;
import com.shop.easybuy.service.cart.CartService;
import com.shop.easybuy.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
    public Order findById(Long id) {
        var foundOrder = orderRepository.findOrderByOrderId(id).orElseThrow(() -> {
            log.error("");
            return new ObjectNotFoundException("Заказ", id);
        });
        log.info("Найден заказ с ID {} и количеством товаров {}.", foundOrder.getId(), foundOrder.getItems().size());
        return foundOrder;
    }

    @Override
    public List<Order> findAll() {
        var foundOrders = orderRepository.findAllOrders();
        log.info("Найдено {} заказов.", foundOrders.size());
        return foundOrders;
    }
}
