package com.shop.easybuy.entity.cart;

import com.shop.easybuy.entity.item.ItemRsDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CartViewDto {

    private List<List<ItemRsDto>> foundItems;

    private Long totalCount;

    private Boolean canPay;

    private Boolean paymentServiceAvailable;

    private Long currentBalance;
}
