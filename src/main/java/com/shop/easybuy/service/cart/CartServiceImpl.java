package com.shop.easybuy.service.cart;

import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.entity.cart.CartItem;
import com.shop.easybuy.entity.cart.CartViewDto;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.repository.CartRepository;
import com.shop.easybuy.repository.ItemRepository;
import com.shop.easybuy.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

    private static final int rowSize = 5;

    private final CartRepository cartRepository;

    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public void changeQuantity(Long itemId, ActionEnum action) {
        switch (action) {
            case PLUS -> {
                CartItem cartItem = cartRepository.findById(itemId).orElse(new CartItem(itemId, 0));
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                cartRepository.save(cartItem);
                logCart(itemId, cartItem.getQuantity());
            }
            case MINUS -> cartRepository.findById(itemId).ifPresent(cartItem -> {
                var quantity = cartItem.getQuantity();
                if (quantity > 0 && quantity != 1) {
                    cartItem.setQuantity(quantity - 1);
                    cartRepository.save(cartItem);
                    logCart(itemId, cartItem.getQuantity());
                } else {
                    cartRepository.deleteById(itemId);
                    logCart(itemId, 0);
                }
            });
            case DELETE -> {
                cartRepository.deleteById(itemId);
                logCart(itemId, 0);
            }
        }
    }

    private void logCart(Long itemId, Integer quantity) {
        if (quantity > 0) log.info("В корзине обновлено количество товара с ID {}. Текущее количество: {}.", itemId, quantity);
        else log.info("Товар с ID {} был удалён из корзины.", itemId);
    }

    @Override
    public CartViewDto getAllItems() {

        var itemsInCart = itemRepository.findAllInCart();
        var total = countTotal(itemsInCart);
        var foundItems = Utils.splitList(itemsInCart, rowSize);

        log.info("В корзине найдено {} товаров.", itemsInCart.size());

        return new CartViewDto(foundItems, total);
    }

    @Override
    @Transactional
    public void clearCart() {
        cartRepository.clearCart();
        log.info("Корзина была очищена.");
    }

    private Long countTotal(List<ItemRsDto> itemsInCart) {
        return itemsInCart
                .stream()
                .mapToLong(item -> item.getCount() * item.getPrice())
                .sum();
    }
}
