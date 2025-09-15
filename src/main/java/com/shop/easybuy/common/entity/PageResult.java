package com.shop.easybuy.common.entity;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResult<T>(Page<T> page, List<List<T>> foundItems) {
}
