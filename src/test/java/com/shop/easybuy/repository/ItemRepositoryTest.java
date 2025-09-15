package com.shop.easybuy.repository;

import com.shop.easybuy.annotation.JpaTestConfig;
import com.shop.easybuy.common.exception.ObjectNotFoundException;
import com.shop.easybuy.entity.cart.CartItem;
import com.shop.easybuy.entity.item.Item;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.testDB.AbstractTestDatabase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.shop.easybuy.DataCreator.createItem;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JpaTestConfig
@ActiveProfiles("test")
public class ItemRepositoryTest extends AbstractTestDatabase {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Test
    @DisplayName("Поиск товара по имени или описанию")
    void shouldFindItemByTitleOrDescription() {

        String search = "Test";
        Item item = createItem();
        itemRepository.save(item);

        List<ItemRsDto> foundItems = itemRepository.findAllByTitleOrDescription(search, Pageable.unpaged()).getContent();
        ItemRsDto foundItem = foundItems.getFirst();

        assertEquals(item.getTitle(), foundItem.getTitle());
        assertEquals(item.getDescription(), foundItem.getDescription());
        assertEquals(item.getImagePath(), foundItem.getImagePath());
        assertEquals(item.getPrice(), foundItem.getPrice());
    }

    @Test
    @DisplayName("Поиск всех товаров в корзине")
    void shouldFindAllItemsInCart() {
        int count1 = 1;
        int count2 = 3;
        Item item1 = createItem();
        Item item2 = createItem();
        System.out.println(item1.getId());
        itemRepository.save(item1);
        itemRepository.save(item2);
        CartItem cartItem1 = new CartItem(item1.getId(), count1);
        CartItem cartItem2 = new CartItem(item2.getId(), count2);
        cartRepository.save(cartItem1);
        cartRepository.save(cartItem2);

        List<ItemRsDto> foundItemsInCart = itemRepository.findAllInCart();
        ItemRsDto itemRsDto1 = foundItemsInCart.stream()
                .filter(dto -> dto.getId().equals(item1.getId()))
                .findFirst()
                .orElseThrow();

        ItemRsDto itemRsDto2 = foundItemsInCart.stream()
                .filter(dto -> dto.getId().equals(item2.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals(2, foundItemsInCart.size());
        assertEquals(itemRsDto1.getId(), item1.getId());
        assertEquals(itemRsDto1.getTitle(), item1.getTitle());
        assertEquals(itemRsDto1.getDescription(), item1.getDescription());
        assertEquals(itemRsDto1.getImagePath(), item1.getImagePath());
        assertEquals(itemRsDto1.getPrice(), item1.getPrice());
        assertEquals(itemRsDto1.getCount(), count1);
        assertEquals(itemRsDto2.getId(), item2.getId());
        assertEquals(itemRsDto2.getTitle(), item2.getTitle());
        assertEquals(itemRsDto2.getDescription(), item2.getDescription());
        assertEquals(itemRsDto2.getImagePath(), item2.getImagePath());
        assertEquals(itemRsDto2.getPrice(), item2.getPrice());
        assertEquals(itemRsDto2.getPrice(), item2.getPrice());
        assertEquals(itemRsDto2.getCount(), count2);
    }

    @Test
    @DisplayName("Поиск товара по ID")
    void shouldFindItemById() {
        Item item = createItem();
        itemRepository.save(item);

        ItemRsDto foundItem = itemRepository.findItemById(item.getId()).orElseThrow(() -> new ObjectNotFoundException("Товар", item.getId()));

        assertEquals(item.getTitle(), foundItem.getTitle());
        assertEquals(item.getDescription(), foundItem.getDescription());
        assertEquals(item.getImagePath(), foundItem.getImagePath());
        assertEquals(item.getPrice(), foundItem.getPrice());
    }
}
