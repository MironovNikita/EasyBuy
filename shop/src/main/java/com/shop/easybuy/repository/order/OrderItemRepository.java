package com.shop.easybuy.repository.order;

import com.shop.easybuy.entity.order.OrderItem;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface OrderItemRepository extends R2dbcRepository<OrderItem, Long> {
}
