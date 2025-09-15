package com.shop.easybuy.controller;

import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.utils.Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CartControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    @Sql(statements = {
            "INSERT INTO cart(item_id, quantity, added_at) VALUES(1, 3, '2025-09-14 21:56:39.047928')",
            "INSERT INTO cart(item_id, quantity, added_at) VALUES(2, 5, '2025-09-14 21:57:39.047928')"
    })
    @DisplayName("Отображение товаров в корзине")
    void shouldReturnEmptyListIfCartIsNotEmpty() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/cart/items"))
                .andExpect(view().name("cart"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("emptyList"))
                .andExpect(model().attributeExists("total"))
                .andReturn();

        Map<String, Object> result = Objects.requireNonNull(Objects.requireNonNull(mvcResult.getModelAndView()).getModel());
        @SuppressWarnings("unchecked")
        var items = (List<List<ItemRsDto>>) result.get("items");
        var itemsInCartList = Utils.mergeList(items);
        var total = (Long) result.get("total");
        var emptyList = (Boolean) result.get("emptyList");

        assertFalse(emptyList);
        assertEquals(18500L, total);
        assertEquals(2, itemsInCartList.size());
        assertTrue(itemsInCartList.stream().map(ItemRsDto::getId).toList().containsAll(List.of(1L, 2L)));
    }

    @Test
    @DisplayName("Отображение товаров в корзине, если корзина пуста")
    void shouldReturnEmptyListIfCartIsEmpty() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/cart/items"))
                .andExpect(view().name("cart"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("emptyList"))
                .andExpect(model().attributeExists("total"))
                .andReturn();

        Map<String, Object> result = Objects.requireNonNull(Objects.requireNonNull(mvcResult.getModelAndView()).getModel());
        @SuppressWarnings("unchecked")
        var items = (List<List<ItemRsDto>>) result.get("items");
        var itemsInCartList = Utils.mergeList(items);
        var total = (Long) result.get("total");
        var emptyList = (Boolean) result.get("emptyList");

        assertTrue(emptyList);
        assertEquals(0L, total);
        assertEquals(0, itemsInCartList.size());
    }

    @Test
    @Sql(statements = {
            "INSERT INTO cart(item_id, quantity, added_at) VALUES(1, 3, '2025-09-14 21:56:39.047928')",
            "INSERT INTO cart(item_id, quantity, added_at) VALUES(2, 5, '2025-09-14 21:57:39.047928')"
    })
    @DisplayName("Изменение количества товара в корзине: PLUS")
    void shouldPlusQuantityOfItemInCart() throws Exception {
        Long itemId = 1L;

        mockMvc.perform(post("/cart/items/%d".formatted(itemId))
                        .param("action", ActionEnum.PLUS.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"))
                .andReturn();

        var changedItem = itemService.findItemById(itemId);

        assertEquals(changedItem.getId(), itemId);
        assertEquals(changedItem.getCount(), 4);
    }

    @Test
    @Sql(statements = {
            "INSERT INTO cart(item_id, quantity, added_at) VALUES(1, 3, '2025-09-14 21:56:39.047928')",
            "INSERT INTO cart(item_id, quantity, added_at) VALUES(2, 5, '2025-09-14 21:57:39.047928')"
    })
    @DisplayName("Изменение количества товара в корзине: MINUS")
    void shouldMinusQuantityOfItemInCart() throws Exception {
        Long itemId = 1L;

        mockMvc.perform(post("/cart/items/%d".formatted(itemId))
                        .param("action", ActionEnum.MINUS.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"))
                .andReturn();

        var changedItem = itemService.findItemById(itemId);

        assertEquals(changedItem.getId(), itemId);
        assertEquals(changedItem.getCount(), 2);
    }

    @Test
    @Sql(statements = {
            "INSERT INTO cart(item_id, quantity, added_at) VALUES(1, 3, '2025-09-14 21:56:39.047928')",
            "INSERT INTO cart(item_id, quantity, added_at) VALUES(2, 5, '2025-09-14 21:57:39.047928')"
    })
    @DisplayName("Изменение количества товара в корзине: DELETE")
    void shouldDeleteItemFromCart() throws Exception {
        Long itemId = 1L;

        mockMvc.perform(post("/cart/items/%d".formatted(itemId))
                        .param("action", ActionEnum.DELETE.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"))
                .andReturn();

        var changedItem = itemService.findItemById(itemId);

        assertEquals(changedItem.getId(), itemId);
        assertEquals(changedItem.getCount(), 0);
    }
}
