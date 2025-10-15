package com.shop.easybuy.controller.cart;

import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.service.cart.CartService;
import com.shop.easybuy.utils.ValidationUtils;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
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
                    model.addAttribute("canPay", result.getCanPay());
                    model.addAttribute("paymentServiceAvailable", result.getPaymentServiceAvailable());
                    return "cart";
                });
    }

    @PostMapping("/cart/items/{id}")
    public Mono<String> changeQuantity(@PathVariable("id")
                                       @Positive(message = "ID товара должно быть положительным числом.") Long id,
                                       ServerWebExchange exchange) {

        return exchange.getFormData()
                .flatMap(formData -> {
                    ActionEnum action = ValidationUtils.validateAction(formData.getFirst("action"));
                    return cartService.changeQuantity(id, action)
                            .then(Mono.just("redirect:/cart/items"));
                });

    }
}
