package com.shop.easybuy.repository;

import com.shop.easybuy.entity.cart.CartItem;
import com.shop.easybuy.repository.cart.CartRepository;
import com.shop.easybuy.testDB.AbstractRepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

public class CartRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Test
    @DisplayName("Проверка добавления товара в корзину")
    void shouldAddItemToCart() {

        StepVerifier.create(cartRepository.count())
                .expectNext(0L)
                .verifyComplete();

        StepVerifier.create(cartRepository.addItemToCart(new CartItem(1L, 10)))
                .assertNext(savedId -> {
                    assertThat(savedId).isNotNull();

                    StepVerifier.create(cartRepository.count())
                            .expectNextCount(1L)
                            .verifyComplete();

                    StepVerifier.create(cartRepository.findById(savedId))
                            .assertNext(cartItem -> {
                                assertThat(cartItem).isNotNull();
                                assertThat(cartItem.getItemId()).isEqualTo(1L);
                                assertThat(cartItem.getQuantity()).isEqualTo(10);
                            })
                            .verifyComplete();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Проверка очистки корзины")
    void shouldClearCart() {

        StepVerifier.create(
                        cartRepository.addItemToCart(new CartItem(1L, 10))
                                .then(cartRepository.addItemToCart(new CartItem(2L, 3)))
                                .thenMany(cartRepository.count())
                )
                .expectNext(2L)
                .verifyComplete();

        StepVerifier.create(cartRepository.clearCart()
                        .thenMany(cartRepository.count()
                        ))
                .expectNext(0L)
                .verifyComplete();
    }
}
