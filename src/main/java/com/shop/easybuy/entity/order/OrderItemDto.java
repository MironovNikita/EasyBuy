package com.shop.easybuy.entity.order;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {

    private Long id;
    private Long itemId;
    private String title;
    private String description;
    private String imagePath;
    private Long price;
    private Long count;
}
