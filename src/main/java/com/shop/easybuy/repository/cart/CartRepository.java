package com.shop.easybuy.repository.cart;

import com.shop.easybuy.entity.cart.CartItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface CartRepository extends R2dbcRepository<CartItem, Long>, CartRepositoryCustom {

    @Query("DELETE FROM cart")
    Mono<Integer> clearCart();

    Mono<CartItem> findCartItemByItemId(Long itemId);

    Mono<Void> deleteCartItemByItemId(Long itemId);
}
