package com.shop.easybuy.controller.order;

import com.shop.easybuy.common.security.SecurityUserContextHandler;
import com.shop.easybuy.service.order.OrderService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Validated
@Controller
@RequestMapping
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private final SecurityUserContextHandler securityUserContextHandler;

    private final AccessDeniedException ACCESS_DENIED = new AccessDeniedException("Ошибка авторизации. Необходимо войти в учётную запись.");

    @PostMapping("/buy")
    public Mono<String> buy() {

        return securityUserContextHandler.getCurrentUserId()
                .flatMap(userId -> orderService.buyItemsInCartByUserId(userId)
                        .map(orderRsDto -> "redirect:" + UriComponentsBuilder.fromPath("/orders/{id}")
                                .queryParam("newOrder", true)
                                .buildAndExpand(orderRsDto.getId())
                                .toUriString()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(ACCESS_DENIED)));
    }

    @GetMapping("/orders")
    public Mono<String> showOrders(Model model) {
        return securityUserContextHandler.getCurrentUserId()
                .flatMap(userId -> orderService.findAllByUserId(userId)
                        .collectList()
                        .map(foundOrders -> {
                            model.addAttribute("orders", foundOrders);
                            return "orders";
                        }))
                .switchIfEmpty(Mono.defer(() -> Mono.error(ACCESS_DENIED)));
    }

    @GetMapping("/orders/{id}")
    public Mono<String> showOrder(@PathVariable("id")
                                  @Positive(message = "ID заказа должно быть положительным числом.") Long id,
                                  @RequestParam(name = "newOrder", required = false) Boolean newOrder,
                                  Model model) {
        return securityUserContextHandler.getCurrentUserId()
                .flatMap(userId -> orderService.findByIdAndUserId(id, userId)
                        .map(orderRsDto -> {
                            model.addAttribute("order", orderRsDto);

                            if (Boolean.TRUE.equals(newOrder)) model.addAttribute("newOrder", newOrder);

                            return "order";
                        }))
                .switchIfEmpty(Mono.defer(() -> Mono.error(ACCESS_DENIED)));
    }
}