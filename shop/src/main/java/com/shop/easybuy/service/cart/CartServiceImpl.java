package com.shop.easybuy.service.cart;

import com.shop.easybuy.client.api.PaymentApi;
import com.shop.easybuy.client.model.BalanceRs;
import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.entity.cart.CartItem;
import com.shop.easybuy.entity.cart.CartViewDto;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.repository.cart.CartRepository;
import com.shop.easybuy.repository.item.ItemRepository;
import com.shop.easybuy.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

    private static final int rowSize = 5;

    private final CartRepository cartRepository;

    private final ItemRepository itemRepository;

    private final PaymentApi paymentApi;

    @Override
    @Transactional
    public Mono<Void> changeQuantity(Long itemId, ActionEnum action) {
        return switch (action) {
            case PLUS -> cartRepository.findCartItemByItemId(itemId)
                    .defaultIfEmpty(new CartItem(itemId, 0))
                    .flatMap(found -> {
                        found.setQuantity(found.getQuantity() + 1);
                        logCart(itemId, found.getQuantity());
                        return cartRepository.addItemToCart(found);
                    })
                    .then();

            case MINUS -> cartRepository.findCartItemByItemId(itemId)
                    .switchIfEmpty(Mono.defer(() -> {
                        log.warn("Товар с указанным ID {} не был найден. Действие {} пропущено.", itemId, action);
                        return Mono.empty();
                    }))
                    .flatMap(found -> {
                        var quantity = found.getQuantity();
                        if (quantity > 1) {
                            found.setQuantity(quantity - 1);
                            logCart(itemId, found.getQuantity());
                            return cartRepository.addItemToCart(found);
                        } else {
                            logCart(itemId, 0);
                            return cartRepository.deleteCartItemByItemId(itemId);
                        }
                    })
                    .then();

            case DELETE -> cartRepository.deleteCartItemByItemId(itemId)
                    .doOnSuccess(aVoid -> logCart(itemId, 0))
                    .then();
        };
    }

    private void logCart(Long itemId, Integer quantity) {
        if (quantity > 0)
            log.info("В корзине обновлено количество товара с ID {}. Текущее количество: {}.", itemId, quantity);
        else log.info("Товар с ID {} был удалён из корзины.", itemId);
    }

    @Override
    public Mono<CartViewDto> getAllItems() {

        return itemRepository.findAllInCart()
                .collectList()
                .flatMap(items -> {
                    var total = countTotal(items);
                    var foundItems = Utils.splitList(items, rowSize);

                    return paymentApi.getBalance()
                            .map(BalanceRs::getBalance)
                            .map(currentBalance -> {
                                boolean canPay = currentBalance >= total;
                                log.info("В корзине найдено {} товаров.", items.size());
                                return new CartViewDto(foundItems, total, canPay, true);
                            })
                            .onErrorResume(e -> {
                                log.warn("Платёжный сервис недоступен: {}", e.getMessage());
                                return Mono.just(new CartViewDto(foundItems, total, false, false));
                            });
                });
    }

    @Override
    @Transactional
    public Mono<Void> clearCart() {
        return cartRepository.clearCart()
                .doOnSuccess(clear -> log.info("Корзина была очищена."))
                .then();
    }

    private Long countTotal(List<ItemRsDto> itemsInCart) {
        return itemsInCart
                .stream()
                .mapToLong(item -> item.count() * item.price())
                .sum();
    }
}
