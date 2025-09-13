package com.shop.easybuy.controller.cart;

import com.shop.easybuy.common.ActionEnum;
import com.shop.easybuy.entity.cart.CartViewDto;
import com.shop.easybuy.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@Controller
@RequestMapping
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/cart/items")
    public String itemCartList(Model model) {

        CartViewDto cartView = cartService.getAllItems();

        model.addAttribute("items", cartView.getFoundItems());
        model.addAttribute("emptyList", cartView.getFoundItems().isEmpty());
        model.addAttribute("total", cartView.getTotalCount());
        return "cart";
    }

    @PostMapping("/cart/items/{id}")
    public String changeQuantity(@PathVariable("id") Long id, ActionEnum action) {

        cartService.changeQuantity(id, action);
        return "redirect:/cart/items";
    }
}
