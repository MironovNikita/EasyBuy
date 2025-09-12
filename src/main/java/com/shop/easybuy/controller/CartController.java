package com.shop.easybuy.controller;

import com.shop.easybuy.common.ActionEnum;
import com.shop.easybuy.entity.cart.CartViewDto;
import com.shop.easybuy.entity.item.ItemResponseDto;
import com.shop.easybuy.service.cart.CartServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;

@Validated
@Controller
@RequestMapping
@RequiredArgsConstructor
public class CartController {

    //TODO Заменить на интерфейс
    private final CartServiceImpl cartService;

    @GetMapping("/cart/items")
    public String itemCartList(Model model) {

        CartViewDto cartView = cartService.getAllItems();

        model.addAttribute("items", cartView.getFoundItems());
        model.addAttribute("empty", cartView.getFoundItems().isEmpty());
        model.addAttribute("total", cartView.getTotalCount());
        return "cart";
    }

    @PostMapping("/cart/items/{id}")
    public String changeQuantity(@PathVariable("id") Long id, ActionEnum action) {

        cartService.changeQuantity(id, action);
        return "redirect:/cart/items";
    }
}
