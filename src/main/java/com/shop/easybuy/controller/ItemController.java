package com.shop.easybuy.controller;

import com.shop.easybuy.entity.item.ItemResponseDto;
import com.shop.easybuy.entity.paging.Paging;
import com.shop.easybuy.service.item.ItemServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Validated
@Controller
@RequestMapping
@RequiredArgsConstructor
public class ItemController {

    //TODO Поменять на интерфейс
    private final ItemServiceImpl itemService;

    @GetMapping("/")
    public String mainRedirect() {
        return "redirect:/main/items";
    }

    @GetMapping("/main/items")
    public String mainPage(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
            Model model) {

        List<List<ItemResponseDto>> items = itemService.getAllByParams(search);

        //boolean hasNext = postService.hasNextPage(search, pageSize, pageNumber);
        boolean hasNext = false;
        boolean hasPrevious = pageNumber > 1;

        model.addAttribute("items", items);
        model.addAttribute("search", search);

        Paging paging = new Paging(pageNumber, pageSize, hasNext, hasPrevious);
        //Paging paging = new Paging(pageNumber, pageSize, false, false);
        model.addAttribute("paging", paging);

        return "main";
    }
}
