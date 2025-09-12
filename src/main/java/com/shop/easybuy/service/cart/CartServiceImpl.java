package com.shop.easybuy.service.cart;

import com.shop.easybuy.common.ActionEnum;
import com.shop.easybuy.entity.cart.CartItem;
import com.shop.easybuy.entity.cart.CartViewDto;
import com.shop.easybuy.entity.item.ItemResponseDto;
import com.shop.easybuy.repository.CartRepository;
import com.shop.easybuy.repository.ItemRepository;
import com.shop.easybuy.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//TODO Дополнить интерфейс + @Override
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private static final int rowSize = 5;

    private final CartRepository cartRepository;

    private final ItemRepository itemRepository;

    //@Override
    public void changeQuantity(Long itemId, ActionEnum action) {
        switch (action) {
            case PLUS -> {
                CartItem cartItem = cartRepository.findById(itemId).orElse(new CartItem(itemId, 0));
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                cartRepository.save(cartItem);
            }
            case MINUS -> {
                cartRepository.findById(itemId).ifPresent(cartItem -> {
                    var quantity = cartItem.getQuantity();
                    if (quantity > 0 && quantity != 1) {
                        cartItem.setQuantity(quantity - 1);
                        cartRepository.save(cartItem);
                    } else {
                        cartRepository.deleteById(itemId);
                    }
                });
            }
            case DELETE -> cartRepository.deleteById(itemId);
        }
    }

    public CartViewDto getAllItems() {

        var itemsInCart = itemRepository.findAllInCart();
        var total = countTotal(itemsInCart);
        var foundItems = Utils.splitList(itemsInCart, rowSize);

        return new CartViewDto(foundItems, total);
    }

    private Long countTotal(List<ItemResponseDto> itemsInCart) {
        return itemsInCart
                .stream()
                .mapToLong(item -> item.getCount() * item.getPrice())
                .sum();
    }
}
