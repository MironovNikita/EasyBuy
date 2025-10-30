package com.shop.easybuy.security.controller;

import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.entity.cart.CartViewDto;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.utils.Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.shop.easybuy.DataCreator.createItemRsDto;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

public class CartControllerSecurityTest extends CommonSecurityTest {

    @Test
    @WithAnonymousUser
    @DisplayName("Доступ к корзине запрещён для анонимного пользователя")
    void shouldNotShowCartItemsIfNotAuthenticated() {
        webClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/login");
    }

    @Test
    @WithMockUser
    @DisplayName("Доступ к корзине разрешён для аутентифицированного пользователя")
    void shouldShowCartItemsIfAuthenticated() {
        Long userId = 50L;
        Long total = 1000L;
        Long currentBalance = 20000L;
        ItemRsDto itemRsDto = createItemRsDto(1L);

        List<List<ItemRsDto>> foundItems = Utils.splitList(List.of(itemRsDto), 5);
        CartViewDto cartViewDto = new CartViewDto(foundItems, total, true, true, currentBalance);

        when(securityUserContextHandler.getCurrentUserId()).thenReturn(Mono.just(userId));
        when(cartService.getAllItemsByUserId(userId)).thenReturn(Mono.just(cartViewDto));

        webClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> assertThat(result.getResponseBody()).contains("cart"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Доступ к изменению количества товара запрещён для анонимного пользователя")
    void shouldNotChangeQuantityInCartIfNotAuthenticated() {
        Long itemId = 1L;
        webClient.post()
                .uri("/cart/items/{id}", itemId)
                .body(BodyInserters.fromFormData("action", "PLUS"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/login");
    }

    @Test
    @WithMockUser
    @DisplayName("Доступ к изменению количества товара разрешён для аутентифицированного пользователя")
    void shouldAcceptChangeQuantityInCartIfAuthenticated() {
        Long itemId = 1L;
        Long userId = 50L;

        when(securityUserContextHandler.getCurrentUserId()).thenReturn(Mono.just(userId));
        when(cartService.changeQuantityByUserId(itemId, ActionEnum.PLUS, userId)).thenReturn(Mono.empty());

        webClient.post()
                .uri("/cart/items/{id}", itemId)
                .body(BodyInserters.fromFormData("action", "PLUS"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");
    }
}
