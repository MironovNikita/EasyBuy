package com.shop.easybuy.entity.order;

import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

public record OrderFlatDto(
        @Column("orderId") Long orderId,
        @Column("orderTotal") Long orderTotal,
        @Column("orderCreated") LocalDateTime orderCreatedAt,
        @Column("orderItemId") Long orderItemId,
        @Column("orderItemCount") Long orderItemCount,
        @Column("itemId") Long itemId,
        @Column("itemTitle") String itemTitle,
        @Column("itemDescription") String itemDescription,
        @Column("itemImagePath") String itemImagePath,
        @Column("itemPrice") Long itemPrice
) {
}
