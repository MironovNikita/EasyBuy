package com.shop.easybuy.service.order;

import com.shop.easybuy.entity.order.Order;
import com.shop.easybuy.entity.order.OrderItem;
import com.shop.easybuy.mapper.ItemMapper;
import com.shop.easybuy.repository.OrderRepository;
import com.shop.easybuy.service.cart.CartService;
import com.shop.easybuy.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
        return savedOrder;
    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findOrderByOrderId(id).orElseThrow(); //TODO Дополнить исключением
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAllOrders();
    }
}
