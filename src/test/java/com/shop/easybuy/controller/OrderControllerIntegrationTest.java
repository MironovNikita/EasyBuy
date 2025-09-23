package com.shop.easybuy.controller;

import com.shop.easybuy.common.exception.CartEmptyException;
import com.shop.easybuy.common.exception.ObjectNotFoundException;
import com.shop.easybuy.entity.order.Order;
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

public class OrderControllerIntegrationTest extends BaseIntegrationTest {
/**
    @Test
    @Sql(statements = {
            "INSERT INTO cart(item_id, quantity, added_at) VALUES(1, 3, '2025-09-14 21:56:39.047928')",
            "INSERT INTO cart(item_id, quantity, added_at) VALUES(2, 5, '2025-09-14 21:57:39.047928')"
    })
    @DisplayName("Оформление покупки товаров в корзине")
    void shouldCreateOrder() throws Exception {
        long orderId = 1L;

        MvcResult mvcResult = mockMvc.perform(post("/buy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/%d".formatted(orderId)))
                .andReturn();

        Map<String, Object> flashMap = Objects.requireNonNull(mvcResult.getFlashMap());
        Order order = (Order) flashMap.get("order");

        assertTrue((Boolean) flashMap.get("newOrder"));
        assertEquals(orderId, order.getId());
        assertEquals(18500, order.getTotal());
        assertEquals(2, order.getItems().size());
    }

    @Test
    @DisplayName("Ошибка при оформлении покупки с пустой корзиной")
    void shouldThrowCartEmptyExceptionsIfCartIsEmpty() throws Exception {
        mockMvc.perform(post("/buy"))
                .andExpect(status().is4xxClientError());

        assertThrows(CartEmptyException.class, () -> orderService.buyItemsInCart());
    }

    @Test
    @DisplayName("Показать все заказы, если их ещё не было")
    void shouldShowOrdersIfOrdersIsEmpty() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/orders"))
                .andExpect(view().name("orders"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("orders"))
                .andReturn();

        Map<String, Object> result = Objects.requireNonNull(Objects.requireNonNull(mvcResult.getModelAndView()).getModel());
        @SuppressWarnings("unchecked")
        List<Order> orders = (List<Order>) result.get("orders");

        assertEquals(mvcResult.getModelAndView().getViewName(), "orders");
        assertEquals(mvcResult.getModelAndView().getModel(), result);
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    @Test
    @Sql(statements = {
            "INSERT INTO orders(id, total, created_at) VALUES(1, 5000, '2025-09-14 21:56:39.047928')",
            "INSERT INTO order_items(id, order_id, item_id, count) VALUES(1, 1, 1, 1)",
            "INSERT INTO orders(id, total, created_at) VALUES(2, 10000, '2025-09-15 11:56:39.047928')",
            "INSERT INTO order_items(id, order_id, item_id, count) VALUES(2, 2, 1, 2)"
    })
    @DisplayName("Показать все заказы")
    void shouldShowOrders() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/orders"))
                .andExpect(view().name("orders"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("orders"))
                .andReturn();

        @SuppressWarnings("unchecked")
        List<Order> orders = (List<Order>) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("orders");

        assertNotNull(orders);
        assertEquals(2, orders.size());
        var totals = orders.stream().map(Order::getTotal).toList();
        assertTrue(totals.contains(5000L));
        assertTrue(totals.contains(10000L));
        assertTrue(orders.stream().noneMatch(o -> o.getItems().isEmpty()));
    }

    @Test
    @Sql(statements = {
            "INSERT INTO orders(id, total, created_at) VALUES(1, 5000, '2025-09-14 21:56:39.047928')",
            "INSERT INTO order_items(id, order_id, item_id, count) VALUES(1, 1, 1, 1)"
    })
    @DisplayName("Получение заказа по его ID")
    void shouldFindOrderById() throws Exception {
        long orderId = 1L;

        MvcResult mvcResult = mockMvc.perform(get("/orders/%d".formatted(orderId)))
                .andExpect(view().name("order"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("order"))
                .andReturn();

        Order order = (Order) Objects.requireNonNull(Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("order"));

        assertNotNull(order);
        assertEquals(orderId, order.getId());
        assertEquals(order.getTotal(), 5000L);
        assertEquals(1, order.getItems().size());
    }

    @Test
    @DisplayName("Выброс исключения, если заказ не найден")
    void shouldThrowObjectNotFoundExceptionIfOrderNotExists() throws Exception {
        long nonExistingOrderId = 1L;

        mockMvc.perform(get("/orders/%d".formatted(nonExistingOrderId)))
                .andExpect(status().is4xxClientError());

        assertThrows(ObjectNotFoundException.class, () -> orderService.findById(nonExistingOrderId));
    }
    */
}
