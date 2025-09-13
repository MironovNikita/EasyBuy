package com.shop.easybuy.controller.order;

import com.shop.easybuy.entity.order.Order;
import com.shop.easybuy.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Validated
@Controller
@RequestMapping
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/buy")
    public String buy(RedirectAttributes model) {

        Order order = orderService.buyItemsInCart();
        model.addAttribute("id", order.getId());
        model.addFlashAttribute("newOrder", true);
        model.addAttribute("order", order);

        return "redirect:/orders/{id}";
    }

    @GetMapping("/orders")
    public String showOrders(Model model) {
        List<Order> foundOrders = orderService.findAll();
        model.addAttribute("orders", foundOrders);
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String showOrder(@PathVariable("id") Long id, Model model) {
        Order order = orderService.findById(id);
        model.addAttribute("order", order);
        return "order";
    }
}
