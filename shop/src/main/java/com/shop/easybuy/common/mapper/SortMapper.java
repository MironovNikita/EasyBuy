package com.shop.easybuy.common.mapper;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SortMapper {

    public com.shop.easybuy.client.model.cache.SortEnum toSortEnum(com.shop.easybuy.common.entity.SortEnum sortEnum) {
        return Optional.ofNullable(sortEnum)
                .map(sort ->
                        com.shop.easybuy.client.model.cache.SortEnum.valueOf(sort.name()))
                .orElse(null);
    }
}
