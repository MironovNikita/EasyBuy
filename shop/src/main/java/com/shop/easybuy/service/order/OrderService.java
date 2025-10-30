package com.shop.easybuy.service.order;

import com.shop.easybuy.entity.order.OrderRsDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {

    Mono<OrderRsDto> buyItemsInCartByUserId(Long userId);

    Mono<OrderRsDto> findByIdAndUserId(Long id, Long userId);

    Flux<OrderRsDto> findAllByUserId(Long userId);
}
