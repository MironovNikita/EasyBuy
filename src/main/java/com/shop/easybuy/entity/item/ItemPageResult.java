package com.shop.easybuy.entity.item;

import org.springframework.data.domain.Page;

import java.util.List;

public record ItemPageResult(Page<Item> page, List<List<ItemResponseDto>> foundItems) {
}
