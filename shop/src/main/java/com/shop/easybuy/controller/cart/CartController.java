package com.shop.easybuy.controller.cart;

import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.common.security.SecurityUserContextHandler;
import com.shop.easybuy.service.cart.CartService;
import com.shop.easybuy.utils.ValidationUtils;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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

    private final SecurityUserContextHandler securityUserContextHandler;

    private final AccessDeniedException ACCESS_DENIED = new AccessDeniedException("Ошибка авторизации. Необходимо войти в учётную запись.");

    @GetMapping("/cart/items")
    public Mono<String> itemCartList(Model model) {

        return securityUserContextHandler.getCurrentUserId()
                .flatMap(userId -> cartService.getAllItemsByUserId(userId)
                        .map(result -> {
                            model.addAttribute("items", result.getFoundItems());
                            model.addAttribute("emptyList", result.getFoundItems().isEmpty());
                            model.addAttribute("total", result.getTotalCount());
                            model.addAttribute("canPay", result.getCanPay());
                            model.addAttribute("paymentServiceAvailable", result.getPaymentServiceAvailable());
                            return "cart";
                        }))
                .switchIfEmpty(Mono.defer(() -> Mono.error(ACCESS_DENIED)));
    }

    @PostMapping("/cart/items/{id}")
    public Mono<String> changeQuantity(@PathVariable("id")
                                       @Positive(message = "ID товара должно быть положительным числом.") Long id,
                                       ServerWebExchange exchange) {

        return securityUserContextHandler.getCurrentUserId()
                .flatMap(userId -> exchange.getFormData()
                        .flatMap(formData -> {
                            ActionEnum action = ValidationUtils.validateAction(formData.getFirst("action"));
                            return cartService.changeQuantityByUserId(id, action, userId)
                                    .then(Mono.just("redirect:/cart/items"));
                        }))
                .switchIfEmpty(Mono.defer(() -> Mono.error(ACCESS_DENIED)));
    }
}
