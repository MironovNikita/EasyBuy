package com.shop.easybuy.repository;

import com.shop.easybuy.entity.cart.CartItem;
import com.shop.easybuy.entity.user.User;
import com.shop.easybuy.repository.cart.CartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.shop.easybuy.DataCreator.createUser;
import static com.shop.easybuy.DataInserter.insertIntoUserTable;
import static org.assertj.core.api.Assertions.assertThat;

public class CartRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Test
    @DisplayName("Проверка добавления товара в корзину")
    void shouldAddItemToCart() {
        Long userId = 1L;
        Long itemId = 1L;
        User user = createUser(userId);
        insertIntoUserTable(databaseClient, user).block();

        Mono<CartItem> testFlow = cartRepository.count()
                .doOnNext(count -> assertThat(count).isZero())
                .then(cartRepository.addItemToCart(new CartItem(itemId, 10, userId)))
                .flatMap(q -> cartRepository.findCartItemByItemIdAndUserId(itemId, userId));

        StepVerifier.create(testFlow)
                .assertNext(cartItem -> {
                    assertThat(cartItem.getItemId()).isEqualTo(1L);
                    assertThat(cartItem.getQuantity()).isEqualTo(10);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Проверка очистки корзины")
    void shouldClearCart() {
        Long userId = 1L;
        User user = createUser(userId);
        insertIntoUserTable(databaseClient, user).block();

        StepVerifier.create(
                        cartRepository.addItemToCart(new CartItem(1L, 10, userId))
                                .then(cartRepository.addItemToCart(new CartItem(2L, 3, userId)))
                                .thenMany(cartRepository.count())
                )
                .expectNext(2L)
                .verifyComplete();

        StepVerifier.create(cartRepository.clearUserCartById(userId)
                        .thenMany(cartRepository.count()
                        ))
                .expectNext(0L)
                .verifyComplete();
    }
}
