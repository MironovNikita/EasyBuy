package com.shop.easybuy.repository.cart;

import com.shop.easybuy.entity.cart.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CartItemRepositoryCustomImpl implements CartRepositoryCustom {

    private final DatabaseClient client;

    @Override
    public Mono<CartItem> addItemToCart(CartItem cartItem) {
        return null;
    }
}
