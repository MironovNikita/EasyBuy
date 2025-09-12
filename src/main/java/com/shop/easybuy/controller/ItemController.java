package com.shop.easybuy.controller;

import com.shop.easybuy.common.ActionEnum;
import com.shop.easybuy.common.SortEnum;
import com.shop.easybuy.common.PageResult;
import com.shop.easybuy.repository.ItemRepository;
import com.shop.easybuy.service.cart.CartServiceImpl;
import com.shop.easybuy.service.item.ItemServiceImpl;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Controller
@RequestMapping
@RequiredArgsConstructor
public class ItemController {

    //TODO Поменять на интерфейс
    private final ItemServiceImpl itemService;

    //TODO Поменять на интерфейс
    private final CartServiceImpl cartService;

    @GetMapping("/")
    public String mainRedirect() {
        return "redirect:/main/items";
    }

    @GetMapping("/main/items")
    public String mainPage(
            @RequestParam(value = "search", required = false, defaultValue = "")
            @Size(max = 20, message = "Количество символов в строке поиска не должно превышать 20.") String search,
            @RequestParam(value = "sort", required = false, defaultValue = "NO") SortEnum sort,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            Model model) {

        var result = itemService.getAllByParams(search, PageRequest.of(pageNumber, pageSize, sort.getSort()));

        model.addAttribute("items", result.foundItems());
        model.addAttribute("search", search);
        model.addAttribute("paging", result.page());
        model.addAttribute("sort", sort.name());

        return "main";
    }

    @PostMapping("/main/items/{id}")
    public String changeQuantityMainPage(@PathVariable("id") Long id, ActionEnum action) {

        cartService.changeQuantity(id, action);
        return "redirect:/main/items";
    }

    @GetMapping("/items/{id}")
    public String itemPage(@PathVariable("id") Long id, Model model) {

        var foundItem = itemService.findItemById(id);
        model.addAttribute("item", foundItem);

        return "item";
    }

    @PostMapping("/items/{id}")
    public String changeQuantityItemPage(@PathVariable("id") Long id, ActionEnum action) {

        cartService.changeQuantity(id, action);
        return "redirect:/items/" + id;
    }
}