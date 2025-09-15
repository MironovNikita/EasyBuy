package com.shop.easybuy.controller.cart;

import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.entity.cart.CartViewDto;
import com.shop.easybuy.service.cart.CartService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public String changeQuantity(@PathVariable("id")
                                 @Positive(message = "ID товара должно быть положительным числом.") Long id,
                                 @RequestParam @NotNull(message = "Изменение количества товара не может быть пустым.") ActionEnum action) {

        cartService.changeQuantity(id, action);
        return "redirect:/cart/items";
    }
}
