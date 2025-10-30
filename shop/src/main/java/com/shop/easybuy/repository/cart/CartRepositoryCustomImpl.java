package com.shop.easybuy.repository.cart;

import com.shop.easybuy.entity.cart.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CartRepositoryCustomImpl implements CartRepositoryCustom {

    private final DatabaseClient client;

    @Override
    public Mono<Long> addItemToCart(CartItem cartItem) {

        String sql = """
                INSERT INTO cart (item_id, user_id, quantity, added_at) VALUES (:itemId, :userId, :quantity, :addedAt)
                ON CONFLICT (item_id, user_id) DO UPDATE SET quantity = EXCLUDED.quantity
                """;

        return client.sql(sql)
                .bind("itemId", cartItem.getItemId())
                .bind("userId", cartItem.getUserId())
                .bind("quantity", cartItem.getQuantity())
                .bind("addedAt", cartItem.getAddedAt())
                .fetch()
                .rowsUpdated();
    }
}
