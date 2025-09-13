package com.shop.easybuy.service.order;

import com.shop.easybuy.entity.order.Order;

import java.util.List;

public interface OrderService {

    Order buyItemsInCart();

    Order findById(Long id);

    List<Order> findAll();
}
