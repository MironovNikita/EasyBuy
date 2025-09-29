package com.shop.easybuy.service;

import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.entity.cart.CartItem;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.repository.cart.CartRepository;
import com.shop.easybuy.repository.item.ItemRepository;
import com.shop.easybuy.service.cart.CartServiceImpl;
import com.shop.easybuy.utils.Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static com.shop.easybuy.DataCreator.createItemRsDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    @DisplayName("Изменение количества товара в корзине: PLUS")
    void shouldChangeItemQuantityPlus() {
        Long itemId = 1L;
        CartItem cartItem = new CartItem(itemId, 10);

        when(cartRepository.findCartItemByItemId(itemId)).thenReturn(Mono.just(cartItem));
        when(cartRepository.addItemToCart(cartItem)).thenReturn(Mono.just(itemId));

        StepVerifier.create(cartService.changeQuantity(itemId, ActionEnum.PLUS))
                .verifyComplete();

        verify(cartRepository).addItemToCart(argThat(saved -> saved.getQuantity() == 11));
        verify(cartRepository, never()).deleteById(itemId);
    }

    @Test
    @DisplayName("Изменение количества товара в корзине: MINUS")
    void shouldChangeItemQuantityMinus() {
        Long itemId = 1L;
        CartItem cartItem = new CartItem(itemId, 10);

        when(cartRepository.findCartItemByItemId(itemId)).thenReturn(Mono.just(cartItem));
        when(cartRepository.addItemToCart(cartItem)).thenReturn(Mono.just(itemId));

        StepVerifier.create(cartService.changeQuantity(itemId, ActionEnum.MINUS))
                .verifyComplete();

        verify(cartRepository).addItemToCart(argThat(saved -> saved.getQuantity() == 9));
        verify(cartRepository, never()).deleteById(itemId);
    }

    @Test
    @DisplayName("Изменение количества товара в корзине: DELETE")
    void shouldChangeItemQuantityDelete() {
        Long itemId = 1L;
        CartItem cartItem = new CartItem(itemId, 10);

        when(cartRepository.deleteCartItemByItemId(itemId)).thenReturn(Mono.empty());

        StepVerifier.create(cartService.changeQuantity(itemId, ActionEnum.DELETE))
                        .verifyComplete();

        verify(cartRepository).deleteCartItemByItemId(itemId);
        verify(cartRepository, never()).addItemToCart(cartItem);
    }

    @Test
    @DisplayName("Получение всех товаров из корзины")
    void shouldReturnAllCartItems() {
        Long itemId1 = 1L;
        Long itemId2 = 2L;
        ItemRsDto itemRsDto1 = createItemRsDto(itemId1);
        ItemRsDto itemRsDto2 = createItemRsDto(itemId2);

        List<ItemRsDto> items = List.of(itemRsDto1, itemRsDto2);

        when(itemRepository.findAllInCart()).thenReturn(Flux.fromIterable(items));

        StepVerifier.create(cartService.getAllItems())
                        .assertNext(found -> {
                            assertEquals(Utils.mergeList(found.getFoundItems()).size(), items.size());
                            assertEquals(found.getTotalCount(), 20000L);
                        })
                .verifyComplete();

        verify(itemRepository).findAllInCart();
    }

    @Test
    @DisplayName("Очистка всей корзины")
    void shouldClearCart() {

        when(cartRepository.clearCart()).thenReturn(Mono.empty());

        StepVerifier.create(cartService.clearCart())
                        .verifyComplete();

        verify(cartRepository).clearCart();
        verifyNoInteractions(itemRepository);
    }
}
