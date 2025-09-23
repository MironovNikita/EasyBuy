package com.shop.easybuy.entity.order;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "orders")
public class Order {

    @Id
    private Long id;

    private Long total;

    @Column("created_at")
    private LocalDateTime createdAt;
}
