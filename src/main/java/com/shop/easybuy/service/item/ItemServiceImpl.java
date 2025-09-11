package com.shop.easybuy.service.item;

import com.shop.easybuy.common.ActionEnum;
import com.shop.easybuy.entity.item.Item;
import com.shop.easybuy.entity.item.ItemPageResult;
import com.shop.easybuy.entity.item.ItemResponseDto;
import com.shop.easybuy.mapper.ItemMapper;
import com.shop.easybuy.repository.ItemRepository;
import com.shop.easybuy.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    private final ItemMapper itemMapper;

    //@Override
    public ItemPageResult getAllByParams(String search, PageRequest pageRequest) {
        Page<Item> page = itemRepository.findAllByTitleOrDescription(search, pageRequest);
        List<List<ItemResponseDto>> foundItems = Utils.splitList(page.getContent()
                .stream()
                .map(itemMapper::convertItemForRs)
                .toList(), rowSize);

        return new ItemPageResult(page, foundItems);
    }
}
