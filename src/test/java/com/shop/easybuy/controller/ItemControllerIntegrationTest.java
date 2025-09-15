package com.shop.easybuy.controller;

import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.common.entity.SortEnum;
import com.shop.easybuy.common.exception.ObjectNotFoundException;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.utils.Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ItemControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Редирект на главную страницу")
    void shouldRedirectToMainPage() throws Exception {

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));
    }

    @Test
    @DisplayName("Главная страница без сортировки и строки поиска")
    void shouldShowMainPageWithoutSortAndSearch() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/main/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("sort"))
                .andReturn();

        Map<String, Object> result = Objects.requireNonNull(Objects.requireNonNull(mvcResult.getModelAndView()).getModel());
        @SuppressWarnings("unchecked")
        var items = (List<List<ItemRsDto>>) result.get("items");
        var foundItems = Utils.mergeList(items);
        var search = result.get("search");
        @SuppressWarnings("unchecked")
        var paging = (Page<ItemRsDto>) result.get("paging");
        var sort = result.get("sort");

        assertEquals(foundItems.size(), 6);
        assertTrue(foundItems.stream().map(ItemRsDto::getId).toList().containsAll(List.of(1L, 2L, 3L, 4L, 5L, 6L)));
        assertEquals(search, "");
        assertEquals(paging.getTotalPages(), 1);
        assertEquals(paging.getSize(), 10);
        assertEquals(paging.getTotalElements(), 6);
        assertEquals(sort, "NO");
    }

    @Test
    @DisplayName("Главная страница с сортировкой и строкой поиска")
    void shouldShowMainPageWithSortAndSearch() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/main/items")
                        .param("sort", SortEnum.ALPHA.name())
                        .param("search", "й")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("sort"))
                .andReturn();

        Map<String, Object> result = Objects.requireNonNull(Objects.requireNonNull(mvcResult.getModelAndView()).getModel());
        @SuppressWarnings("unchecked")
        var items = (List<List<ItemRsDto>>) result.get("items");
        var foundItems = Utils.mergeList(items);
        var search = result.get("search");
        @SuppressWarnings("unchecked")
        var paging = (Page<ItemRsDto>) result.get("paging");
        var sort = result.get("sort");

        var foundItemsIds = foundItems.stream().map(ItemRsDto::getId).toList();

        assertEquals(foundItems.size(), 3);
        assertTrue(foundItemsIds.containsAll(List.of(5L, 1L, 6L)));
        assertThat(foundItemsIds, contains(5L, 1L, 6L));
        assertEquals(search, "й");
        assertEquals(paging.getTotalPages(), 1);
        assertEquals(paging.getSize(), 5);
        assertEquals(paging.getTotalElements(), 3);
        assertEquals(sort, "ALPHA");
    }

    @Test
    @DisplayName("Изменение количества товара в корзине на главной странице: PLUS")
    void shouldChangeQuantityOnMainPagePlus() throws Exception {
        Long itemId = 1L;

        mockMvc.perform(post("/main/items/%d".formatted(itemId))
                        .param("action", ActionEnum.PLUS.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items?search=&sort=NO&pageSize=10&pageNumber=0"))
                .andExpect(view().name("redirect:/main/items"))
                .andReturn();

        ItemRsDto changedItem = itemService.findItemById(itemId);

        assertEquals(changedItem.getId(), itemId);
        assertEquals(changedItem.getCount(), 1);
    }

    @Test
    @Sql(statements = "INSERT INTO cart(item_id, quantity, added_at) VALUES(1, 3, '2025-09-14 21:56:39.047928')")
    @DisplayName("Изменение количества товара в корзине на главной странице: MINUS")
    void shouldChangeQuantityOnMainPageMinus() throws Exception {
        Long itemId = 1L;

        mockMvc.perform(post("/main/items/%d".formatted(itemId))
                        .param("action", ActionEnum.MINUS.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items?search=&sort=NO&pageSize=10&pageNumber=0"))
                .andExpect(view().name("redirect:/main/items"))
                .andReturn();

        ItemRsDto changedItem = itemService.findItemById(itemId);

        assertEquals(changedItem.getId(), itemId);
        assertEquals(changedItem.getCount(), 2);
    }

    @Test
    @Sql(statements = "INSERT INTO cart(item_id, quantity, added_at) VALUES(1, 3, '2025-09-14 21:56:39.047928')")
    @DisplayName("Изменение количества товара в корзине на главной странице: DELETE")
    void shouldChangeQuantityOnMainPageDelete() throws Exception {
        Long itemId = 1L;

        mockMvc.perform(post("/main/items/%d".formatted(itemId))
                        .param("action", ActionEnum.DELETE.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items?search=&sort=NO&pageSize=10&pageNumber=0"))
                .andExpect(view().name("redirect:/main/items"))
                .andReturn();

        ItemRsDto changedItem = itemService.findItemById(itemId);

        assertEquals(changedItem.getId(), itemId);
        assertEquals(changedItem.getCount(), 0);
    }

    @Test
    @DisplayName("Отображение страницы товара")
    void shouldShowItemPageIfExists() throws Exception {

        Long itemId = 1L;

        MvcResult mvcResult = mockMvc.perform(get("/items/%d".formatted(itemId)))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"))
                .andReturn();

        Map<String, Object> result = Objects.requireNonNull(Objects.requireNonNull(mvcResult.getModelAndView()).getModel());
        var foundItem = (ItemRsDto) result.get("item");

        assertEquals(foundItem.getId(), itemId);
        assertEquals(foundItem.getCount(), 0);
        assertEquals(foundItem.getPrice(), 5000L);
    }

    @Test
    @DisplayName("Ошибка поиска товара по несуществующему ID")
    void shouldThrowObjectNotFoundExceptionIfNonExistentItemId() throws Exception {
        Long nonExistentItemId = 9999L;

        mockMvc.perform(get("/items/%d".formatted(nonExistentItemId)))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error"));

        assertThrows(ObjectNotFoundException.class, () -> itemService.findItemById(nonExistentItemId));
    }

    @Test
    @DisplayName("Изменение количества товара на странице товара: PLUS")
    void shouldChangeQuantityOnItemPagePlus() throws Exception {

        Long itemId = 1L;

        mockMvc.perform(post("/items/%d".formatted(itemId))
                        .param("action", ActionEnum.PLUS.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/items/%d".formatted(itemId)))
                .andReturn();

        ItemRsDto changedItem = itemService.findItemById(itemId);

        assertEquals(changedItem.getId(), itemId);
        assertEquals(changedItem.getCount(), 1);
    }

    @Test
    @Sql(statements = "INSERT INTO cart(item_id, quantity, added_at) VALUES(1, 3, '2025-09-14 21:56:39.047928')")
    @DisplayName("Изменение количества товара на странице товара: MINUS")
    void shouldChangeQuantityOnItemPageMinus() throws Exception {

        Long itemId = 1L;

        mockMvc.perform(post("/items/%d".formatted(itemId))
                        .param("action", ActionEnum.MINUS.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/items/%d".formatted(itemId)))
                .andReturn();

        ItemRsDto changedItem = itemService.findItemById(itemId);

        assertEquals(changedItem.getId(), itemId);
        assertEquals(changedItem.getCount(), 2);
    }

}
