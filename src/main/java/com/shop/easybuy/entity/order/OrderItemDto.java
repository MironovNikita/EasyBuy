package com.shop.easybuy.entity.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
//TODO Забыли imagePath и description
public class OrderItemDto {

    private Long id;
    private Long itemId;
    private String title;
    private String description;
    private String imagePath;
    private Long price;
    private Long count;
}
