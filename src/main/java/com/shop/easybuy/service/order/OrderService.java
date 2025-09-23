package com.shop.easybuy.service.order;

import com.shop.easybuy.entity.order.Order;
import com.shop.easybuy.entity.order.OrderRsDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OrderService {

    Mono<OrderRsDto> findById(Long id);

    Flux<OrderRsDto> findAll();

    Order buyItemsInCart();

    //Order findById(Long id);

    //List<Order> findAll();
}
