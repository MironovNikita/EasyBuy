package com.shop.easybuy.service.item;

import com.shop.easybuy.common.PageResult;
import com.shop.easybuy.entity.item.ItemResponseDto;
import com.shop.easybuy.repository.ItemRepository;
import com.shop.easybuy.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final int rowSize = 5;

    //TODO Поменять на интерфейс + @Override
    private final ItemRepository itemRepository;

    //@Override
    public PageResult<ItemResponseDto> getAllByParams(String search, Pageable pageable) {
        Page<ItemResponseDto> page = itemRepository.findAllByTitleOrDescription(search, pageable);

        List<List<ItemResponseDto>> itemsToShow = Utils.splitList(page.getContent(), rowSize);

        return new PageResult<>(page, itemsToShow);
    }

    public ItemResponseDto findItemById(Long id) {
        return itemRepository.findItemById(id).orElseThrow(); //TODO Добавить исключение!
    }
}
