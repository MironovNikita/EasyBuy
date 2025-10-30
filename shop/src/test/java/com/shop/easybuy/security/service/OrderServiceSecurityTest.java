package com.shop.easybuy.security.service;

import com.shop.easybuy.client.api.payment.PaymentApi;
import com.shop.easybuy.common.security.SecurityUserContextHandler;
import com.shop.easybuy.repository.order.OrderItemRepository;
import com.shop.easybuy.repository.order.OrderRepository;
import com.shop.easybuy.service.cart.CartService;
import com.shop.easybuy.service.order.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceSecurityTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartService cartService;

    @Mock
    private PaymentApi paymentApi;

    @Mock
    private SecurityUserContextHandler securityUserContextHandler;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("Проверка отказа в оформлении заказа, если ID текущего пользователя отличается от запросного")
    void shouldCheckOrderDeniedIfUserIdIsDifferent() {
        Long userId = 50L;

        doAnswer(invocation -> Mono.error(new AccessDeniedException(
                "Доступ запрещён. Войдите под другим аккаунтом для доступа к ресурсу"
        ))).when(securityUserContextHandler).checkUserIdOrThrow(anyLong());

        StepVerifier.create(orderService.buyItemsInCartByUserId(userId))
                .expectErrorMatches(throwable -> throwable instanceof AccessDeniedException &&
                        throwable.getMessage().contains("Войдите под другим аккаунтом"))
                .verify();

        verify(cartService, never()).getAllItemsByUserId(anyLong());
        verify(paymentApi, never()).payWithHttpInfo(any());
        verify(cartService, never()).clearUserCartById(anyLong());
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Проверка отказа в поиске заказа, если ID текущего пользователя отличается от запросного")
    void shouldFindOrderDeniedIfUserIdIsDifferent() {
        Long userId = 50L;
        Long orderId = 1L;

        doAnswer(invocation -> Mono.error(new AccessDeniedException(
                "Доступ запрещён. Войдите под другим аккаунтом для доступа к ресурсу"
        ))).when(securityUserContextHandler).checkUserIdOrThrow(anyLong());

        StepVerifier.create(orderService.findByIdAndUserId(orderId, userId))
                .expectErrorMatches(throwable -> throwable instanceof AccessDeniedException &&
                        throwable.getMessage().contains("Войдите под другим аккаунтом"))
                .verify();

        verify(orderRepository, never()).findByOrderIdAndUserId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Проверка отказа в поиске заказов, если ID текущего пользователя отличается от запросного")
    void shouldFindOrdersDeniedIfUserIdIsDifferent() {
        Long userId = 50L;

        doAnswer(invocation -> Mono.error(new AccessDeniedException(
                "Доступ запрещён. Войдите под другим аккаунтом для доступа к ресурсу"
        ))).when(securityUserContextHandler).checkUserIdOrThrow(anyLong());

        StepVerifier.create(orderService.findAllByUserId(userId))
                .expectErrorMatches(throwable -> throwable instanceof AccessDeniedException &&
                        throwable.getMessage().contains("Войдите под другим аккаунтом"))
                .verify();

        verify(orderRepository, never()).findAllOrdersByUserId(anyLong());
    }
}
