package com.shop.easybuy.controller.order;

import com.shop.easybuy.service.order.OrderService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/buy")
    public Mono<String> buy() {

        return orderService.buyItemsInCart()
                .map(orderRsDto -> "redirect:" + UriComponentsBuilder.fromPath("/orders/{id}")
                        .queryParam("newOrder", true)
                        .buildAndExpand(orderRsDto.getId())
                        .toUriString());
    }

    @GetMapping("/orders")
    public Mono<String> showOrders(Model model) {
        return orderService.findAll()
                .collectList()
                .map(foundOrders -> {
                    model.addAttribute("orders", foundOrders);
                    return "orders";
                });
    }

    @GetMapping("/orders/{id}")
    public Mono<String> showOrder(@PathVariable("id")
                                  @Positive(message = "ID товара должно быть положительным числом.") Long id,
                                  @RequestParam(name = "newOrder", required = false) Boolean newOrder,
                                  Model model) {
        return orderService.findById(id)
                .map(orderRsDto -> {
                    model.addAttribute("order", orderRsDto);

                    if (Boolean.TRUE.equals(newOrder)) model.addAttribute("newOrder", newOrder);

                    return "order";
                });
    }
}