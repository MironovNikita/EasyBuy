package com.shop.easybuy.entity.item;

public record ItemRsDto(
        Long id,
        String title,
        String description,
        String image,
        Long count,
        Long price
) {
}
