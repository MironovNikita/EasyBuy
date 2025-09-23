package com.shop.easybuy.controller.item;

import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.common.entity.SortEnum;
import com.shop.easybuy.service.cart.CartService;
import com.shop.easybuy.service.item.ItemService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Validated
@Controller
@RequestMapping
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    private final CartService cartService;

    @GetMapping("/")
    public Mono<String> mainRedirect() {
        return Mono.just("redirect:/main/items");
    }

    @GetMapping("/main/items")
    public Mono<String> mainPage(
            @RequestParam(value = "search", required = false, defaultValue = "")
            @Size(max = 20, message = "Количество символов в строке поиска не должно превышать 20.") String search,
            @RequestParam(value = "sort", required = false, defaultValue = "NO") SortEnum sort,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            Model model) {

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort.getSort());
        return itemService.getAllByParams(search, pageRequest)
                .map(result -> {
                    model.addAttribute("items", result.foundItems());
                    model.addAttribute("search", search);
                    model.addAttribute("paging", result.page());
                    model.addAttribute("sort", sort.name());

                    return "main";
                });
    }

    @PostMapping("/main/items/{id}")
    public String changeQuantityMainPage(@PathVariable("id")
                                         @Positive(message = "ID товара должно быть положительным числом.") Long id,
                                         @RequestParam @NotNull(message = "Изменение количества товара не может быть пустым.") ActionEnum action,
                                         @RequestParam(value = "search", required = false, defaultValue = "")
                                         @Size(max = 20, message = "Количество символов в строке поиска не должно превышать 20.") String search,
                                         @RequestParam(value = "sort", required = false, defaultValue = "NO") SortEnum sort,
                                         @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                         @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                         RedirectAttributes redirectAttributes) {

        cartService.changeQuantity(id, action);

        redirectAttributes.addAttribute("search", search);
        redirectAttributes.addAttribute("sort", sort.name());
        redirectAttributes.addAttribute("pageSize", pageSize);
        redirectAttributes.addAttribute("pageNumber", pageNumber);

        return "redirect:/main/items";
    }

    @GetMapping("/items/{id}")
    public Mono<String> itemPage(@PathVariable("id")
                                 @Positive(message = "ID товара должно быть положительным числом.") Long id,
                                 Model model) {

        return itemService.findItemById(id)
                .map(result -> {
                    model.addAttribute("item", result);

                    return "item";
                });
    }

    @PostMapping("/items/{id}")
    public String changeQuantityItemPage(@PathVariable("id")
                                         @Positive(message = "ID товара должно быть положительным числом.") Long id,
                                         @RequestParam @NotNull(message = "Изменение количества товара не может быть пустым.") ActionEnum action) {

        cartService.changeQuantity(id, action);
        return "redirect:/items/" + id;
    }
}