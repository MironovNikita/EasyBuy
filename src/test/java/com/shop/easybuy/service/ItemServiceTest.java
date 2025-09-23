package com.shop.easybuy.service;

import com.shop.easybuy.common.entity.PageResult;
import com.shop.easybuy.common.exception.ObjectNotFoundException;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.repository.ItemRepositoryOld;
import com.shop.easybuy.service.item.ItemServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static com.shop.easybuy.DataCreator.createItemRsDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
/**
    @Mock
    private ItemRepositoryOld itemRepositoryOld;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    @DisplayName("Получение списка товаров без параметров поиска")
    void shouldGetAllItemsWithoutParams() {
        String search = "";
        ItemRsDto itemRsDto1 = createItemRsDto();
        itemRsDto1.setId(1L);
        ItemRsDto itemRsDto2 = createItemRsDto();
        itemRsDto2.setId(2L);

        Pageable pageable = PageRequest.of(1, 5, Sort.unsorted());
        List<ItemRsDto> foundItems = List.of(itemRsDto1, itemRsDto2);
        Page<ItemRsDto> mockPage = new PageImpl<>(foundItems, pageable, foundItems.size());

        when(itemRepositoryOld.findAllByTitleOrDescription(search, pageable)).thenReturn(mockPage);

        PageResult<ItemRsDto> currentResult = itemService.getAllByParams(search, pageable);

        assertEquals(5, currentResult.page().getSize());
        assertEquals(1, currentResult.foundItems().size());
        assertEquals(2, currentResult.foundItems().getFirst().size());
        verify(itemRepositoryOld).findAllByTitleOrDescription(search, pageable);
    }

    @Test
    @DisplayName("Получение списка товаров по параметрам поиска")
    void shouldGetAllItemsByParams() {
        String search = "М";
        ItemRsDto itemRsDto1 = createItemRsDto();
        itemRsDto1.setId(1L);

        Pageable pageable = PageRequest.of(1, 5, Sort.unsorted());
        List<ItemRsDto> foundItems = List.of(itemRsDto1);
        Page<ItemRsDto> mockPage = new PageImpl<>(foundItems, pageable, foundItems.size());

        when(itemRepositoryOld.findAllByTitleOrDescription(search, pageable)).thenReturn(mockPage);

        PageResult<ItemRsDto> currentResult = itemService.getAllByParams(search, pageable);

        assertEquals(5, currentResult.page().getSize());
        assertEquals(1, currentResult.foundItems().size());
        assertEquals(1, currentResult.foundItems().getFirst().size());
        verify(itemRepositoryOld).findAllByTitleOrDescription(search, pageable);
    }

    @Test
    @DisplayName("Успешный поиск товара по его ID")
    void shouldFindItemById() {
        Long itemId = 1L;
        ItemRsDto item = createItemRsDto();
        item.setId(itemId);

        when(itemRepositoryOld.findItemById(itemId)).thenReturn(Optional.of(item));

        ItemRsDto foundItem = itemService.findItemById(itemId);

        assertEquals(item.getId(), foundItem.getId());
        assertEquals(item.getTitle(), foundItem.getTitle());
        assertEquals(item.getDescription(), foundItem.getDescription());
        assertEquals(item.getPrice(), foundItem.getPrice());
        verify(itemRepositoryOld).findItemById(itemId);
    }

    @Test
    @DisplayName("Выброс исключения при поиске товара по его ID, если товар отсутствует")
    void shouldThrowObjectNotFoundException() {
        Long itemId = 1L;

        when(itemRepositoryOld.findItemById(itemId)).thenThrow(ObjectNotFoundException.class);

        assertThrows(ObjectNotFoundException.class, () -> itemService.findItemById(itemId));
        verify(itemRepositoryOld).findItemById(itemId);
    }
    */
}
