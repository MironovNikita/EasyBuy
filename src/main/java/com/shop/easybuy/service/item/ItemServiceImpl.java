package com.shop.easybuy.service.item;

import com.shop.easybuy.common.PageResult;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.repository.ItemRepository;
import com.shop.easybuy.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    //TODO ЛОГИ!
    private static final int rowSize = 5;

    private final ItemRepository itemRepository;

    @Override
    public PageResult<ItemRsDto> getAllByParams(String search, Pageable pageable) {
        Page<ItemRsDto> page = itemRepository.findAllByTitleOrDescription(search, pageable);

        List<List<ItemRsDto>> itemsToShow = Utils.splitList(page.getContent(), rowSize);

        return new PageResult<>(page, itemsToShow);
    }

    @Override
    public ItemRsDto findItemById(Long id) {
        return itemRepository.findItemById(id).orElseThrow(); //TODO Добавить исключение!
    }
}
