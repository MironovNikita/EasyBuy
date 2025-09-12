package com.shop.easybuy.entity.cart;

import com.shop.easybuy.entity.item.ItemResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CartViewDto {

    private List<List<ItemResponseDto>> foundItems;

    private Long totalCount;
}
