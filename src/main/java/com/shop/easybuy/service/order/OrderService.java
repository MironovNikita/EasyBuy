package com.shop.easybuy.service.order;

import com.shop.easybuy.entity.order.OrderRsDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {

    Mono<OrderRsDto> buyItemsInCart();

    Mono<OrderRsDto> findById(Long id);

    Flux<OrderRsDto> findAll();

    //Order buyItemsInCart();

    //Order findById(Long id);

    //List<Order> findAll();
}
