package com.shop.easybuy.entity.item;

public record ItemRsDto(
        Long id,
        String title,
        String description,
        String imagePath,
        Integer count,
        Long price
) {
}
