package com.shop.easybuy.entity.order;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
@Table(name = "orders")
public class Order {

    @Id
    private Long id;

    private Long userId;

    private Long total;

    private LocalDateTime created;
}
