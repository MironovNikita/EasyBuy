package com.shop.easybuy.controller.cart;

import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.service.cart.CartService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Validated
@Controller
@RequestMapping
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/cart/items")
    public Mono<String> itemCartList(Model model) {

        return cartService.getAllItems()
                .map(result -> {
                    model.addAttribute("items", result.getFoundItems());
                    model.addAttribute("emptyList", result.getFoundItems().isEmpty());
                    model.addAttribute("total", result.getTotalCount());
                    return "cart";
                });
    }

    @PostMapping("/cart/items/{id}")
    public Mono<String> changeQuantity(@PathVariable("id")
                                       @Positive(message = "ID товара должно быть положительным числом.") Long id,
                                       @RequestParam @NotNull(message = "Изменение количества товара не может быть пустым.") ActionEnum action) {

        return cartService.changeQuantity(id, action)
                .then(Mono.just("redirect:/cart/items"));
    }
}
