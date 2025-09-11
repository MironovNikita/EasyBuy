package com.shop.easybuy.controller;

import com.shop.easybuy.common.ActionEnum;
import com.shop.easybuy.common.SortEnum;
import com.shop.easybuy.entity.item.Item;
import com.shop.easybuy.entity.item.ItemPageResult;
import com.shop.easybuy.mapper.ItemMapper;
import com.shop.easybuy.service.cart.CartServiceImpl;
import com.shop.easybuy.service.item.ItemServiceImpl;
import com.shop.easybuy.utils.Utils;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

        ItemPageResult result = itemService.getAllByParams(search, PageRequest.of(pageNumber, pageSize, sort.getSort()));

        model.addAttribute("items", result.foundItems());
        model.addAttribute("search", search);
        model.addAttribute("paging", result.page());
        model.addAttribute("sort", sort.name());

        return "main";
    }

    @PostMapping("/main/items/{id}")
    public String changeQuantity(@PathVariable("id") Long id, ActionEnum action) {

        //TODO Добавить таблицу корзина в БД со ссылкой на товар и численным значением количества (от 0 до бесконечности)
        // Соответственно, когда будем делать добавление, то делать JOIN из таблицы Cart для получения количества товара

        cartService.changeQuantity(id, action);
        return "redirect:/main/items";
    }
}
