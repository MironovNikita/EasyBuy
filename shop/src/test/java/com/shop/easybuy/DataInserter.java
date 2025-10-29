package com.shop.easybuy;

import com.shop.easybuy.entity.cart.CartItem;
import com.shop.easybuy.entity.order.Order;
import com.shop.easybuy.entity.order.OrderItem;
import com.shop.easybuy.entity.user.User;
import lombok.experimental.UtilityClass;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@UtilityClass
public class DataInserter {

    public static Mono<Void> insertIntoCartTable(DatabaseClient client, List<CartItem> itemsInCart) {
        return Flux.fromIterable(itemsInCart)
                .flatMap(item -> client.sql(
                                        "INSERT INTO cart(item_id, user_id, quantity, added_at) VALUES(:itemId, :userId, :quantity, :addedAt)"
                                )
                                .bind("itemId", item.getItemId())
                                .bind("userId", item.getUserId())
                                .bind("quantity", item.getQuantity())
                                .bind("addedAt", item.getAddedAt())
                                .fetch()
                                .rowsUpdated()
                )
                .then();
    }

    public static Mono<Void> insertIntoOrdersTable(DatabaseClient client, List<Order> orders) {
        return Flux.fromIterable(orders)
                .flatMap(order -> client.sql(
                                        "INSERT INTO orders(id, user_id, total, created) VALUES(:id, :userId, :total, :created)"
                                )
                                .bind("id", order.getId())
                                .bind("userId", order.getUserId())
                                .bind("total", order.getTotal())
                                .bind("created", order.getCreated())
                                .fetch()
                                .rowsUpdated()
                )
                .then();
    }

    public static Mono<Void> insertIntoOrderItemsTable(DatabaseClient client, List<OrderItem> orderItems) {
        return Flux.fromIterable(orderItems)
                .flatMap(orderItem -> client.sql(
                                        "INSERT INTO order_items(id, order_id, item_id, count) VALUES(:id, :orderId, :itemId, :count)"
                                )
                                .bind("id", orderItem.getId())
                                .bind("orderId", orderItem.getOrderId())
                                .bind("itemId", orderItem.getItemId())
                                .bind("count", orderItem.getCount())
                                .fetch()
                                .rowsUpdated()
                )
                .then();
    }

    public static Mono<Void> insertIntoUserTable(DatabaseClient client, User user) {
        return client.sql(
                        "INSERT INTO users(id, email, password, name, surname, phone) VALUES(:id, :email, :password, :name, :surname, :phone)"
                ).bind("id", user.getId())
                .bind("email", user.getEmail())
                .bind("password", user.getPassword())
                .bind("name", user.getName())
                .bind("surname", user.getSurname())
                .bind("phone", user.getPhone())
                .then();
    }
}
