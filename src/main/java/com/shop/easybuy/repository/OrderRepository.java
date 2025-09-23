package com.shop.easybuy.repository;

import com.shop.easybuy.entity.order.Order;
import com.shop.easybuy.entity.order.OrderFlatDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

public interface OrderRepository extends R2dbcRepository<Order, Long> {

    @Query("""
                SELECT o.id AS orderId,
                        o.total AS orderTotal,
                        o.created_at AS createdAt,
                        oi.id AS orderItemId,
                        i.id AS itemId,
                        i.title AS itemTitle,
                        i.price AS itemPrice
                FROM orders o
                LEFT JOIN order_items oi ON o.id = oi.order_id
                LEFT JOIN items i ON oi.item_id = i.id
                ORDER BY o.created_at
            """)
    Flux<OrderFlatDto> findAllOrders();

    @Query("""
            SELECT o.id AS orderId,
                    o.total AS orderTotal,
                    o.created_at AS createdAt,
                    oi.id AS orderItemId,
                    i.id AS itemId,
                    i.title AS itemTitle,
                    i.price AS itemPrice
            FROM orders o
            LEFT JOIN order_items oi ON o.id = oi.order_id
            LEFT JOIN items i ON oi.item_id = i.id
            WHERE o.id = :orderId
            """)
    Flux<OrderFlatDto> findByOrderId(@Param("orderId") Long orderId);
}
