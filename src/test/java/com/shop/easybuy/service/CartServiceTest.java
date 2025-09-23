package com.shop.easybuy.service;

import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.common.exception.ObjectNotFoundException;
import com.shop.easybuy.entity.cart.CartItem;
import com.shop.easybuy.entity.cart.CartViewDto;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.repository.CartRepositoryOld;
import com.shop.easybuy.repository.ItemRepositoryOld;
import com.shop.easybuy.service.cart.CartServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.shop.easybuy.DataCreator.createItemRsDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
/**
    @Mock
    private CartRepositoryOld cartRepositoryOld;

    @Mock
    private ItemRepositoryOld itemRepositoryOld;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    @DisplayName("Изменение количества товара в корзине: PLUS")
    void shouldChangeItemQuantityPlus() {
        Long itemId = 1L;
        CartItem cartItem = new CartItem(itemId, 10);

        when(cartRepositoryOld.findById(itemId)).thenReturn(Optional.of(cartItem));

        cartService.changeQuantity(itemId, ActionEnum.PLUS);

        CartItem changedCartItem = cartRepositoryOld.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Товар", itemId));

        assertEquals(changedCartItem.getQuantity(), 11);
        verify(cartRepositoryOld).save(cartItem);
        verify(cartRepositoryOld, never()).deleteById(itemId);
    }

    @Test
    @DisplayName("Изменение количества товара в корзине: MINUS")
    void shouldChangeItemQuantityMinus() {
        Long itemId = 1L;
        CartItem cartItem = new CartItem(itemId, 10);

        when(cartRepositoryOld.findById(itemId)).thenReturn(Optional.of(cartItem));

        cartService.changeQuantity(itemId, ActionEnum.MINUS);

        CartItem changedCartItem = cartRepositoryOld.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Товар", itemId));

        assertEquals(changedCartItem.getQuantity(), 9);
        verify(cartRepositoryOld).save(cartItem);
        verify(cartRepositoryOld, never()).deleteById(itemId);
    }

    @Test
    @DisplayName("Изменение количества товара в корзине: DELETE")
    void shouldChangeItemQuantityDelete() {
        Long itemId = 1L;
        CartItem cartItem = new CartItem(itemId, 10);

        cartService.changeQuantity(itemId, ActionEnum.DELETE);

        verify(cartRepositoryOld).deleteById(itemId);
        verify(cartRepositoryOld, never()).save(cartItem);
    }

    @Test
    @DisplayName("Получение всех товаров из корзины")
    void shouldReturnAllCartItems() {
        Long itemId1 = 1L;
        Long itemId2 = 2L;
        ItemRsDto itemRsDto1 = createItemRsDto();
        itemRsDto1.setId(itemId1);
        ItemRsDto itemRsDto2 = createItemRsDto();
        itemRsDto2.setId(itemId2);

        List<ItemRsDto> items = List.of(itemRsDto1, itemRsDto2);

        when(itemRepositoryOld.findAllInCart()).thenReturn(items);

        CartViewDto cartView = cartService.getAllItems();
        List<List<ItemRsDto>> splitItems = cartView.getFoundItems();

        assertEquals(20000L, cartView.getTotalCount());
        assertEquals(2, splitItems.getFirst().size());
    }

    @Test
    @DisplayName("Очистка всей корзины")
    void shouldClearCart() {
        cartService.clearCart();

        verify(cartRepositoryOld).clearCart();
        verifyNoInteractions(itemRepositoryOld);
    }
    */
}
