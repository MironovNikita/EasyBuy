package com.shop.easybuy.entity.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderRsDto {
    private Long id;

    private Long total;

    private LocalDateTime orderCreated;

    private List<OrderItemDto> items;
}


