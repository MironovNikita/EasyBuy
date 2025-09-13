package com.shop.easybuy.service.cart;

import com.shop.easybuy.common.ActionEnum;
import com.shop.easybuy.entity.cart.CartViewDto;

public interface CartService {

    void changeQuantity(Long itemId, ActionEnum action);

    CartViewDto getAllItems();

    void clearCart();
}
