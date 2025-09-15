package com.shop.easybuy.repository;

import com.shop.easybuy.annotation.JpaTestConfig;
import com.shop.easybuy.entity.cart.CartItem;
import com.shop.easybuy.entity.item.Item;
import com.shop.easybuy.testDB.AbstractTestDatabase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static com.shop.easybuy.DataCreator.createItem;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JpaTestConfig
@ActiveProfiles("test")
public class CartRepositoryTest extends AbstractTestDatabase {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("INT: Проверка очистки корзины")
    void shouldClearCart() {
        Item item1 = createItem();
        Item item2 = createItem();
        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);
        CartItem cartItem1 = new CartItem(item1.getId(), 1);
        CartItem cartItem2 = new CartItem(item2.getId(), 3);
        cartRepository.save(cartItem1);
        cartRepository.save(cartItem2);

        assertEquals(2, cartRepository.count());

        cartRepository.clearCart();

        assertEquals(0, cartRepository.count());
    }
}
