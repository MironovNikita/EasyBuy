package com.shop.easybuy.service.cart;

import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.entity.cart.CartViewDto;
import reactor.core.publisher.Mono;

public interface CartService {

    Mono<Void> changeQuantity(Long itemId, ActionEnum action);

    Mono<CartViewDto> getAllItems();

    Mono<Void> clearCart();

    //void changeQuantity(Long itemId, ActionEnum action);

    //CartViewDto getAllItems();

    //void clearCart();
}
