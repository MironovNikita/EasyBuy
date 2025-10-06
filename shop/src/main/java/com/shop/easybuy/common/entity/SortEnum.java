package com.shop.easybuy.common.entity;

import lombok.Getter;
import org.springframework.data.domain.Sort;

@Getter
public enum SortEnum {
    NO(Sort.unsorted()),
    ALPHA(Sort.by("title")),
    PRICE_ASC(Sort.by(Sort.Direction.ASC, "price")),
    PRICE_DESC(Sort.by(Sort.Direction.DESC, "price"));

    private final Sort sort;

    SortEnum(Sort sort) {
        this.sort = sort;
    }
}
