package com.shop.easybuy.service.cart;

import com.shop.easybuy.common.ActionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//TODO Дополнить интерфейс + @Override
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    public void changeQuantity(Long id, ActionEnum action) {

    }
}
