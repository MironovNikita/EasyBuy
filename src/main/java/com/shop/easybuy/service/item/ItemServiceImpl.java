package com.shop.easybuy.service.item;

import com.shop.easybuy.entity.item.ItemResponseDto;
import com.shop.easybuy.mapper.ItemMapper;
import com.shop.easybuy.repository.ItemRepository;
import com.shop.easybuy.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final int rowSize = 5;

    //TODO ПОменять на интерфейс
    private final ItemRepository itemRepository;

    private final ItemMapper itemMapper;

    public List<List<ItemResponseDto>> getAllByParams(String search) {
        List<ItemResponseDto> foundItems = itemRepository.findAllByTitleOrDescription(search)
                .stream()
                .map(itemMapper::convertItemForRs)
                .toList();

        return Utils.splitList(foundItems, rowSize);
    }
}
