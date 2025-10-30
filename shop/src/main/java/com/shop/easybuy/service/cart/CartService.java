package com.shop.easybuy.service.cart;

import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.entity.cart.CartViewDto;
import reactor.core.publisher.Mono;

public interface CartService {

    Mono<Void> changeQuantityByUserId(Long itemId, ActionEnum action, Long userId);

    Mono<CartViewDto> getAllItemsByUserId(Long userId);

    Mono<Void> clearUserCartById(Long userId);
}
