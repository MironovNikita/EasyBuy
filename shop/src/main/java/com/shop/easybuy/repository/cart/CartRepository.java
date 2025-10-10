package com.shop.easybuy.repository.cart;

import com.shop.easybuy.entity.cart.CartItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface CartRepository extends R2dbcRepository<CartItem, Long>, CartRepositoryCustom {

    @Query("DELETE FROM cart")
    Mono<Void> clearCart();

    Mono<CartItem> findCartItemByItemId(Long itemId);

    Mono<Void> deleteCartItemByItemId(Long itemId);

    @Query("""
            SELECT c.quantity
            FROM cart c
            WHERE c.item_id = :id
            """)
    Mono<Integer> findItemQuantityInCartByItemId(Long itemId);
}
