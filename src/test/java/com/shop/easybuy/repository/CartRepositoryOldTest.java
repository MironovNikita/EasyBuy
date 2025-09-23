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
public class CartRepositoryOldTest extends AbstractTestDatabase {
/*
    @Autowired
    private CartRepositoryOld cartRepositoryOld;

    @Autowired
    private ItemRepositoryOld itemRepositoryOld;

    @Test
    @DisplayName("Проверка очистки корзины")
    void shouldClearCart() {
        Item item1 = createItem();
        Item item2 = createItem();
        item1 = itemRepositoryOld.save(item1);
        item2 = itemRepositoryOld.save(item2);
        CartItem cartItem1 = new CartItem(item1.getId(), 1);
        CartItem cartItem2 = new CartItem(item2.getId(), 3);
        cartRepositoryOld.save(cartItem1);
        cartRepositoryOld.save(cartItem2);

        assertEquals(2, cartRepositoryOld.count());

        cartRepositoryOld.clearCart();

        assertEquals(0, cartRepositoryOld.count());
    }
    */
}
