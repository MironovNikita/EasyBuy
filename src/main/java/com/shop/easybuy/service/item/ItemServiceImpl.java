package com.shop.easybuy.service.item;

import com.shop.easybuy.common.entity.PageResult;
import com.shop.easybuy.common.exception.ObjectNotFoundException;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.repository.ItemRepository;
import com.shop.easybuy.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private static final int rowSize = 5;

    private final ItemRepository itemRepository;

    @Override
    public PageResult<ItemRsDto> getAllByParams(String search, Pageable pageable) {

        Page<ItemRsDto> page = itemRepository.findAllByTitleOrDescription(search, pageable);
        List<List<ItemRsDto>> itemsToShow = Utils.splitList(page.getContent(), rowSize);

        log.info("По строке поиска \"{}\" было выведено {} товаров на главную страницу.", search, page.getTotalElements());

        return new PageResult<>(page, itemsToShow);
    }

    @Override
    public ItemRsDto findItemById(Long id) {

        var foundItem = itemRepository.findItemById(id).orElseThrow(() -> {
            log.error("Товар с указанным ID {} не был найден.", id);
            return new ObjectNotFoundException("Товар", id);
        });
        log.info("Запрошенный по ID {} товар \"{}\" был найден.", id, foundItem.getTitle());

        return foundItem;
    }
}
