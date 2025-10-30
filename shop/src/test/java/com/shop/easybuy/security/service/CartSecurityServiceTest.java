package com.shop.easybuy.security.service;

import com.shop.easybuy.client.api.payment.PaymentApi;
import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.common.security.SecurityUserContextHandler;
import com.shop.easybuy.repository.cart.CartRepository;
import com.shop.easybuy.repository.item.ItemRepository;
import com.shop.easybuy.service.cart.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartSecurityServiceTest {

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
    @DisplayName("Проверка отказа в изменении количества товара, если ID текущего пользователя отличается от запросного")
    void shouldChangeQuantityDeniedIfUserIdIsDifferent() {
        Long userId = 50L;
        Long itemId = 1L;

        doAnswer(invocation -> Mono.error(new AccessDeniedException(
                "Доступ запрещён. Войдите под другим аккаунтом для доступа к ресурсу"
        ))).when(securityUserContextHandler).checkUserIdOrThrow(anyLong());

        StepVerifier.create(cartService.changeQuantityByUserId(itemId, ActionEnum.PLUS, userId))
                .expectErrorMatches(throwable -> throwable instanceof AccessDeniedException &&
                        throwable.getMessage().contains("Войдите под другим аккаунтом"))
                .verify();

        verify(cartRepository, never()).findCartItemByItemIdAndUserId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Проверка отказа получения всех товаров в корзине, если ID текущего пользователя отличается от запросного")
    void shouldGetCartItemsDeniedIfUserIdIsDifferent() {
        Long userId = 50L;

        doAnswer(invocation -> Mono.error(new AccessDeniedException(
                "Доступ запрещён. Войдите под другим аккаунтом для доступа к ресурсу"
        ))).when(securityUserContextHandler).checkUserIdOrThrow(anyLong());

        StepVerifier.create(cartService.getAllItemsByUserId(userId))
                .expectErrorMatches(throwable -> throwable instanceof AccessDeniedException &&
                        throwable.getMessage().contains("Войдите под другим аккаунтом"))
                .verify();

        verify(itemRepository, never()).findAllInCartByUserId(anyLong());
        verify(paymentApi, never()).getBalance(anyLong());
    }

    @Test
    @DisplayName("Проверка отказа очистки корзины,  если ID текущего пользователя отличается от запросного")
    void shouldClearCartDeniedIfUserIdIsDifferent() {
        Long userId = 50L;

        doAnswer(invocation -> Mono.error(new AccessDeniedException(
                "Доступ запрещён. Войдите под другим аккаунтом для доступа к ресурсу"
        ))).when(securityUserContextHandler).checkUserIdOrThrow(anyLong());

        StepVerifier.create(cartService.clearUserCartById(userId))
                .expectErrorMatches(throwable -> throwable instanceof AccessDeniedException &&
                        throwable.getMessage().contains("Войдите под другим аккаунтом"))
                .verify();

        verify(cartRepository, never()).clearUserCartById(anyLong());
    }

}
