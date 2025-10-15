package com.shop.easybuy.entity.cart;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table(name = "cart")
@NoArgsConstructor
public class CartItem {

    @Id
    @Column("item_id")
    private Long itemId;

    private Integer quantity;

    @Column("added_at")
    private LocalDateTime addedAt = LocalDateTime.now();

    public CartItem(Long itemId, Integer quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }
}
