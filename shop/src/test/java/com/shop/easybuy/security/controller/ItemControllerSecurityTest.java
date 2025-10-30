package com.shop.easybuy.security.controller;

import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.common.entity.PageResult;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.utils.Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.shop.easybuy.DataCreator.createItemRsDto;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ItemControllerSecurityTest extends CommonSecurityTest {

    @Test
    @WithAnonymousUser
    @DisplayName("Проверка общей доступности главного редиректа")
    void shouldRedirectMainPageAnyway() {
        webClient.get()
                .uri("/")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/login");
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Проверка общей доступности главной страницы")
    void shouldShowMainPageWithItems() {
        Long itemId = 1L;
        long totalCount = 5000L;
        PageRequest pageRequest = PageRequest.of(1, 5, Sort.unsorted());
        var list = Utils.splitList(List.of(createItemRsDto(itemId)), 5);
        Page<ItemRsDto> page = new PageImpl<>(Utils.mergeList(list), pageRequest, totalCount);
        PageResult<ItemRsDto> pageResult = new PageResult<>(page, list);
        when(itemService.getAllByParams(anyString(), any(), any())).thenReturn(Mono.just(pageResult));

        webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/main/items")
                        .queryParam("search", "")
                        .queryParam("sort", "NONE")
                        .queryParam("pageSize", 5)
                        .queryParam("pageNumber", 0)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> assertThat(result.getResponseBody()).contains("main"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Проверка отсутствия доступа к изменению числа товаров в корзине на главной странице")
    void shouldNotChangeQuantityOnMainPageIfUserNotAuthenticated() {
        Long itemId = 1L;

        webClient.post()
                .uri("/main/items/{id}", itemId)
                .body(BodyInserters.fromFormData("action", "PLUS"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/login");
    }

    @Test
    @WithMockUser
    @DisplayName("Проверка доступа к изменению числа товаров в корзине на главной странице")
    void shouldChangeQuantityOnMainPageIfUserAuthenticated() {
        Long itemId = 1L;
        Long userId = 50L;

        when(securityUserContextHandler.getCurrentUserId()).thenReturn(Mono.just(userId));
        when(cartService.changeQuantityByUserId(itemId, ActionEnum.PLUS, userId)).thenReturn(Mono.empty());

        webClient.post()
                .uri("/main/items/{id}", itemId)
                .body(BodyInserters.fromFormData("action", "PLUS")
                        .with("search", "")
                        .with("sort", "NONE")
                        .with("pageNumber", "0")
                        .with("pageSize", "10"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main/items?search=&sort=NONE&pageNumber=0&pageSize=10");
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Проверка доступа к отображению товара для всех пользователей")
    void shouldGetItemIfNotAuthenticated() {
        Long itemId = 1L;

        when(itemService.findItemById(itemId)).thenReturn(Mono.just(createItemRsDto(itemId)));

        webClient.get()
                .uri("/items/{id}", itemId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(result -> assertThat(result.getResponseBody()).contains("item"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Проверка отсутствия доступа к изменению количества товара у неавторизованного пользователя")
    void shouldNotChangeItemQuantityIfNotAuthenticated() {
        Long itemId = 1L;

        webClient.post()
                .uri("/items/{id}", itemId)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/login");
    }

    @Test
    @WithMockUser
    @DisplayName("Проверка отсутствия доступа к изменению количества товара у неавторизованного пользователя")
    void shouldChangeItemQuantityIfAuthenticated() {
        Long itemId = 1L;
        Long userId = 50L;

        when(securityUserContextHandler.getCurrentUserId()).thenReturn(Mono.just(userId));
        when(cartService.changeQuantityByUserId(itemId, ActionEnum.PLUS, userId)).thenReturn(Mono.empty());

        webClient.post()
                .uri("/items/{id}", itemId)
                .body(BodyInserters.fromFormData("action", "PLUS"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/items/" + itemId);
    }
}
