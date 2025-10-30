package com.shop.easybuy.service;

import com.shop.easybuy.client.api.payment.PaymentApi;
import com.shop.easybuy.client.model.payment.BalanceRs;
import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.common.security.SecurityUserContextHandler;
import com.shop.easybuy.entity.cart.CartItem;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.repository.cart.CartRepository;
import com.shop.easybuy.repository.item.ItemRepository;
import com.shop.easybuy.service.cart.CartServiceImpl;
import com.shop.easybuy.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
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

    @Mock
    private PaymentApi paymentApi;

    @Mock
    SecurityUserContextHandler securityUserContextHandler;

    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setUpRowSize() {
        ReflectionTestUtils.setField(cartService, "rowSize", 2);
    }

    @Test
    @DisplayName("Изменение количества товара в корзине: PLUS")
    void shouldChangeItemQuantityPlus() {
        Long itemId = 1L;
        Long userId = 1L;
        CartItem cartItem = new CartItem(itemId, 10, userId);

        when(securityUserContextHandler.checkUserIdOrThrow(userId)).thenReturn(Mono.just(true));
        when(cartRepository.findCartItemByItemIdAndUserId(itemId, userId)).thenReturn(Mono.just(cartItem));
        when(cartRepository.addItemToCart(cartItem)).thenReturn(Mono.just(itemId));

        StepVerifier.create(cartService.changeQuantityByUserId(itemId, ActionEnum.PLUS, userId))
                .verifyComplete();

        verify(securityUserContextHandler).checkUserIdOrThrow(userId);
        verify(cartRepository).addItemToCart(argThat(saved -> saved.getQuantity() == 11));
        verify(cartRepository, never()).deleteById(itemId);
    }

    @Test
    @DisplayName("Изменение количества товара в корзине: MINUS")
    void shouldChangeItemQuantityMinus() {
        Long itemId = 1L;
        Long userId = 1L;
        CartItem cartItem = new CartItem(itemId, 10, userId);

        when(securityUserContextHandler.checkUserIdOrThrow(userId)).thenReturn(Mono.just(true));
        when(cartRepository.findCartItemByItemIdAndUserId(itemId, userId)).thenReturn(Mono.just(cartItem));
        when(cartRepository.addItemToCart(cartItem)).thenReturn(Mono.just(itemId));

        StepVerifier.create(cartService.changeQuantityByUserId(itemId, ActionEnum.MINUS, userId))
                .verifyComplete();

        verify(securityUserContextHandler).checkUserIdOrThrow(userId);
        verify(cartRepository).addItemToCart(argThat(saved -> saved.getQuantity() == 9));
        verify(cartRepository, never()).deleteById(itemId);
    }

    @Test
    @DisplayName("Изменение количества товара в корзине: DELETE")
    void shouldChangeItemQuantityDelete() {
        Long itemId = 1L;
        Long userId = 1L;
        CartItem cartItem = new CartItem(itemId, 10, userId);

        when(securityUserContextHandler.checkUserIdOrThrow(userId)).thenReturn(Mono.just(true));
        when(cartRepository.deleteCartItemByItemIdAndUserId(itemId, userId)).thenReturn(Mono.empty());

        StepVerifier.create(cartService.changeQuantityByUserId(itemId, ActionEnum.DELETE, userId))
                .verifyComplete();

        verify(securityUserContextHandler).checkUserIdOrThrow(userId);
        verify(cartRepository).deleteCartItemByItemIdAndUserId(itemId, userId);
        verify(cartRepository, never()).addItemToCart(cartItem);
    }

    @Test
    @DisplayName("Получение всех товаров из корзины")
    void shouldReturnAllCartItems() {
        Long itemId1 = 1L;
        Long itemId2 = 2L;
        Long userId = 1L;
        ItemRsDto itemRsDto1 = createItemRsDto(itemId1);
        ItemRsDto itemRsDto2 = createItemRsDto(itemId2);

        List<ItemRsDto> items = List.of(itemRsDto1, itemRsDto2);

        when(securityUserContextHandler.checkUserIdOrThrow(userId)).thenReturn(Mono.just(true));
        when(itemRepository.findAllInCartByUserId(userId)).thenReturn(Flux.fromIterable(items));
        when(paymentApi.getBalance(userId)).thenReturn(Mono.just(new BalanceRs().balance(15000L)));

        StepVerifier.create(cartService.getAllItemsByUserId(userId))
                .assertNext(found -> {
                    assertEquals(Utils.mergeList(found.getFoundItems()).size(), items.size());
                    assertEquals(found.getTotalCount(), 20000L);
                })
                .verifyComplete();

        verify(securityUserContextHandler).checkUserIdOrThrow(userId);
        verify(itemRepository).findAllInCartByUserId(userId);
        verify(paymentApi).getBalance(userId);
    }

    @Test
    @DisplayName("Очистка всей корзины")
    void shouldClearCart() {
        Long userId = 1L;

        when(securityUserContextHandler.checkUserIdOrThrow(userId)).thenReturn(Mono.just(true));
        when(cartRepository.clearUserCartById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(cartService.clearUserCartById(userId))
                .verifyComplete();

        verify(securityUserContextHandler).checkUserIdOrThrow(userId);
        verify(cartRepository).clearUserCartById(userId);
        verifyNoInteractions(itemRepository);
    }
}
