package com.shop.easybuy.repository.cart;

import com.shop.easybuy.entity.cart.CartItem;
import reactor.core.publisher.Mono;

public interface CartRepositoryCustom {

    Mono<Long> addItemToCart(CartItem cartItem);
}
