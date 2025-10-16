package com.shop.easybuy.entity.cache;

public record CachedItem(Long id,
                         String title,
                         String description,
                         String image,
                         Long price) {

}
