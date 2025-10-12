package com.shop.easybuy.service;

import com.shop.easybuy.client.api.cache.CacheApi;
import com.shop.easybuy.client.model.cache.CacheSavedRs;
import com.shop.easybuy.client.model.cache.CachedItem;
import com.shop.easybuy.common.entity.SortEnum;
import com.shop.easybuy.common.exception.ObjectNotFoundException;
import com.shop.easybuy.common.mapper.ItemMapper;
import com.shop.easybuy.common.mapper.SortMapper;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.repository.item.ItemRepository;
import com.shop.easybuy.service.item.ItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static com.shop.easybuy.DataCreator.createItemRsDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CacheApi cacheApi;

    @Mock
    private SortMapper sortMapper;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    void setUpRowSize() {
        ReflectionTestUtils.setField(itemService, "rowSize", 2);
    }

    @Test
    @DisplayName("Получение списка товаров без параметров поиска")
    void shouldGetAllItemsWithoutParams() {
        String search = "";
        ItemRsDto itemRsDto1 = createItemRsDto(1L);
        ItemRsDto itemRsDto2 = createItemRsDto(2L);
        SortEnum sortEnum = SortEnum.NONE;
        CachedItem cachedItem = mock(CachedItem.class);

        Pageable pageable = PageRequest.of(1, 5, sortEnum.getSort());
        List<ItemRsDto> foundItems = List.of(itemRsDto1, itemRsDto2);

        when(sortMapper.toSortEnum(any())).thenReturn(com.shop.easybuy.client.model.cache.SortEnum.NONE);
        when(cacheApi.getMainItemsByParams(anyString(), any(), anyInt(), anyInt())).thenReturn(Flux.empty());
        when(itemRepository.findAllByTitleOrDescription(search, pageable.getPageSize(), pageable.getOffset(), pageable.getSort()))
                .thenReturn(Flux.fromIterable(foundItems));
        when(itemMapper.toCachedItemMono(any())).thenReturn(cachedItem);
        when(cacheApi.cacheMainItems(anyList(), anyString(), any(), anyInt(), anyInt())).thenReturn(Mono.empty());
        when(itemRepository.countItemsBySearch(search)).thenReturn(Mono.just(2L));

        StepVerifier.create(itemService.getAllByParams(search, pageable, sortEnum))
                .assertNext(result -> {
                    assertEquals(foundItems.size(), result.foundItems().getFirst().size());
                    assertEquals(5, result.page().getSize());
                })
                .verifyComplete();

        verify(sortMapper).toSortEnum(any());
        verify(cacheApi).getMainItemsByParams(anyString(), any(), anyInt(), anyInt());
        verify(itemRepository).findAllByTitleOrDescription(search, pageable.getPageSize(), pageable.getOffset(), pageable.getSort());
        verify(itemMapper, times(2)).toCachedItemMono(any());
        verify(itemRepository).countItemsBySearch(search);
    }

    @Test
    @DisplayName("Получение списка товаров по параметрам поиска")
    void shouldGetAllItemsByParams() {
        String search = "М";
        ItemRsDto itemRsDto1 = createItemRsDto(1L);
        SortEnum sortEnum = SortEnum.NONE;
        CachedItem cachedItem = mock(CachedItem.class);

        Pageable pageable = PageRequest.of(1, 5, sortEnum.getSort());
        List<ItemRsDto> foundItems = List.of(itemRsDto1);

        when(sortMapper.toSortEnum(any())).thenReturn(com.shop.easybuy.client.model.cache.SortEnum.NONE);
        when(cacheApi.getMainItemsByParams(anyString(), any(), anyInt(), anyInt())).thenReturn(Flux.empty());
        when(itemRepository.findAllByTitleOrDescription(search, pageable.getPageSize(), pageable.getOffset(), pageable.getSort()))
                .thenReturn(Flux.fromIterable(foundItems));
        when(itemMapper.toCachedItemMono(any())).thenReturn(cachedItem);
        when(cacheApi.cacheMainItems(anyList(), anyString(), any(), anyInt(), anyInt())).thenReturn(Mono.empty());
        when(itemRepository.countItemsBySearch(search)).thenReturn(Mono.just(2L));

        StepVerifier.create(itemService.getAllByParams(search, pageable, sortEnum))
                .assertNext(result -> {
                    assertEquals(foundItems.size(), result.foundItems().getFirst().size());
                    assertEquals(5, result.page().getSize());
                })
                .verifyComplete();

        verify(sortMapper).toSortEnum(any());
        verify(cacheApi).getMainItemsByParams(anyString(), any(), anyInt(), anyInt());
        verify(itemRepository).findAllByTitleOrDescription(search, pageable.getPageSize(), pageable.getOffset(), pageable.getSort());
        verify(itemMapper).toCachedItemMono(any());
        verify(itemRepository).countItemsBySearch(search);
    }

    @Test
    @DisplayName("Успешный поиск товара по его ID")
    void shouldFindItemById() {
        Long itemId = 1L;
        ItemRsDto item = createItemRsDto(itemId);

        when(cacheApi.getItemById(itemId)).thenReturn(Mono.empty());
        when(itemRepository.findItemById(itemId)).thenReturn(Mono.just(item));
        when(cacheApi.cacheItem(anyLong(), any())).thenReturn(Mono.just(new CacheSavedRs().saved(true)));

        StepVerifier.create(itemService.findItemById(itemId))
                .assertNext(result -> {
                    assertEquals(itemId, result.id());
                    assertEquals(item.title(), result.title());
                    assertEquals(item.description(), result.description());
                    assertEquals(item.count(), result.count());
                    assertEquals(item.price(), result.price());
                })
                .verifyComplete();

        verify(cacheApi).getItemById(itemId);
        verify(itemRepository).findItemById(itemId);
        verify(cacheApi).cacheItem(anyLong(), any());
    }

    @Test
    @DisplayName("Выброс исключения при поиске товара по его ID, если товар отсутствует")
    void shouldThrowObjectNotFoundException() {
        Long itemId = 1L;

        when(cacheApi.getItemById(itemId)).thenReturn(Mono.empty());
        when(itemRepository.findItemById(itemId)).thenReturn(Mono.error(new ObjectNotFoundException("Товар", itemId)));

        StepVerifier.create(itemService.findItemById(itemId))
                .expectErrorMatches(throwable ->
                        throwable instanceof ObjectNotFoundException &&
                                throwable.getMessage().contains(itemId.toString()))
                .verify();

        verify(itemRepository).findItemById(itemId);
    }
}
