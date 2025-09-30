package com.shop.easybuy.repository;

import com.shop.easybuy.entity.cart.CartItem;
import com.shop.easybuy.repository.cart.CartRepository;
import com.shop.easybuy.repository.item.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Test
    @DisplayName("Поиск товаров по строке search")
    void shouldFindAllByTitleOrDescription() {

        String search = "йогурт";

        StepVerifier.create(itemRepository.findAllByTitleOrDescription(search, 10, 0, Sort.unsorted()))
                .assertNext(item -> {
                    assertThat(item.id()).isEqualTo(5L);
                    assertThat(item.title().toLowerCase()).contains(search);
                    assertThat(item.description().toLowerCase()).contains(search);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Поиск товаров по строке search - ничего не найдено")
    void shouldFindNothingByTitleOrDescription() {
        String nonExistingSearch = "Ручка";

        StepVerifier.create(itemRepository.findAllByTitleOrDescription(nonExistingSearch, 10, 0, Sort.unsorted()))
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Подсчёт количества товаров по search")
    void shouldCountItemsBySearch() {

        String search = "йогурт";

        StepVerifier.create(itemRepository.countItemsBySearch(search))
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Подсчёт количества товаров по search - ничего не найдено")
    void shouldCountZeroItemsBySearch() {

        String search = "Ручка";

        StepVerifier.create(itemRepository.countItemsBySearch(search))
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Поиск всех товаров в корзине")
    void shouldFindAllItemsInCart() {

        StepVerifier.create(cartRepository.addItemToCart(new CartItem(1L, 10))
                        .then(cartRepository.addItemToCart(new CartItem(2L, 20)))
                        .thenMany(itemRepository.findAllInCart())

                )
                .expectNextMatches(itemRsDto -> itemRsDto.id().equals(1L) && itemRsDto.count().equals(10L))
                .expectNextMatches(itemRsDto -> itemRsDto.id().equals(2L) && itemRsDto.count().equals(20L))
                .verifyComplete();
    }

    @Test
    @DisplayName("Поиск всех товаров в корзине - товаров нет")
    void shouldFindNoneItemsInCart() {

        StepVerifier.create(itemRepository.findAllInCart())
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    @DisplayName("Поиск товара по ID")
    void shouldFindItemById() {
        StepVerifier.create(itemRepository.findItemById(5L))
                .assertNext(item -> {
                    assertThat(item.id().equals(5L));
                    assertThat(item.title().equals("Колбаса"));
                    assertThat(item.price().equals(1201L));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Поиск товара по ID - товар не найден")
    void shouldNotFindItemById() {
        StepVerifier.create(itemRepository.findItemById(9999L))
                .expectNextCount(0L)
                .verifyComplete();
    }
}
