package com.shop.easybuy.repository;

import com.shop.easybuy.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
            SELECT o FROM Order o
            JOIN FETCH o.items oi
            JOIN FETCH oi.item
            WHERE o.id = :orderId
            """)
    Optional<Order> findOrderByOrderId(@Param("orderId") Long orderId);

    @Query("""
            SELECT o FROM Order o
            JOIN FETCH o.items oi
            JOIN FETCH oi.item
            """)
    List<Order> findAllOrders();
}
