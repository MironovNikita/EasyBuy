package com.shop.easybuy.entity.order;

import java.time.LocalDateTime;

public record OrderFlatDto(
        Long orderId,
        Long orderTotal,
        LocalDateTime orderCreatedAt,
        Long orderItemId,
        Long orderItemCount,
        Long itemId,
        String itemTitle,
        String itemDescription,
        String itemImagePath,
        Long itemPrice
) {
}
