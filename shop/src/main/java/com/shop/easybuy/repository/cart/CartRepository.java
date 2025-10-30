package com.shop.easybuy.repository.cart;

import com.shop.easybuy.entity.cart.CartItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CartRepository extends R2dbcRepository<CartItem, Long>, CartRepositoryCustom {

    @Query("""
            DELETE FROM cart c
            WHERE c.user_id = :userId
            """)
    Mono<Void> clearUserCartById(@Param("userId") Long userId);

    Mono<CartItem> findCartItemByItemIdAndUserId(Long itemId, Long userId);

    Mono<Void> deleteCartItemByItemIdAndUserId(Long itemId, Long userId);

    @Query("""
            SELECT c.quantity
            FROM cart c
            WHERE c.item_id = :id and c.user_id = :userId
            """)
    Mono<Integer> findItemQuantityInCartByItemIdAndUserId(Long itemId, Long userId);
}
