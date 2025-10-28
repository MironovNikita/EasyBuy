package com.shop.easybuy.service.cart;

import com.shop.easybuy.client.api.payment.PaymentApi;
import com.shop.easybuy.client.model.payment.BalanceRs;
import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.common.security.SecurityUserContextHandler;
import com.shop.easybuy.entity.cart.CartItem;
import com.shop.easybuy.entity.cart.CartViewDto;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.repository.cart.CartRepository;
import com.shop.easybuy.repository.item.ItemRepository;
import com.shop.easybuy.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

    @Value("${items.row.size}")
    private int rowSize;

    private final CartRepository cartRepository;

    private final ItemRepository itemRepository;

    private final PaymentApi paymentApi;

    private final SecurityUserContextHandler securityUserContextHandler;

    @Override
    @Transactional
    public Mono<Void> changeQuantityByUserId(Long itemId, ActionEnum action, Long userId) {
        return securityUserContextHandler.checkUserIdOrThrow(userId)
                .then(switch (action) {
                    case PLUS -> cartRepository.findCartItemByItemIdAndUserId(itemId, userId)
                            .defaultIfEmpty(new CartItem(itemId, 0, userId))
                            .flatMap(found -> {
                                found.setQuantity(found.getQuantity() + 1);
                                logCart(itemId, found.getQuantity(), userId);
                                return cartRepository.addItemToCart(found);
                            })
                            .then();

                    case MINUS -> cartRepository.findCartItemByItemIdAndUserId(itemId, userId)
                            .switchIfEmpty(Mono.defer(() -> {
                                log.warn("Товар с указанным ID {} не был найден для пользователя с ID {}. Действие {} пропущено.", itemId, action, userId);
                                return Mono.empty();
                            }))
                            .flatMap(found -> {
                                var quantity = found.getQuantity();
                                if (quantity > 1) {
                                    found.setQuantity(quantity - 1);
                                    logCart(itemId, found.getQuantity(), userId);
                                    return cartRepository.addItemToCart(found);
                                } else {
                                    logCart(itemId, 0, userId);
                                    return cartRepository.deleteCartItemByItemIdAndUserId(itemId, userId);
                                }
                            })
                            .then();

                    case DELETE -> cartRepository.deleteCartItemByItemIdAndUserId(itemId, userId)
                            .doOnSuccess(aVoid -> logCart(itemId, 0, userId))
                            .then();
                });
    }

    private void logCart(Long itemId, Integer quantity, Long userId) {
        if (quantity > 0)
            log.info("В корзине обновлено количество товара с ID {} для пользователя с ID {}. Текущее количество: {}.", itemId, userId, quantity);
        else log.info("Товар с ID {} был удалён из корзины.", itemId);
    }

    @Override
    public Mono<CartViewDto> getAllItemsByUserId(Long userId) {

        return securityUserContextHandler.checkUserIdOrThrow(userId)
                .then(itemRepository.findAllInCartByUserId(userId)
                        .collectList()
                        .flatMap(items -> {
                            var total = countTotal(items);
                            var foundItems = Utils.splitList(items, rowSize);

                            return paymentApi.getBalance(userId)
                                    .map(BalanceRs::getBalance)
                                    .map(currentBalance -> {
                                        boolean canPay = currentBalance >= total;
                                        log.info("В корзине пользователя с ID {} найдено {} товаров.", userId, items.size());
                                        return new CartViewDto(foundItems, total, canPay, true, currentBalance);
                                    })
                                    .onErrorResume(e -> {
                                        log.warn("Платёжный сервис недоступен: {}", e.getMessage());
                                        return Mono.just(new CartViewDto(foundItems, total, false, false, -1L));
                                    });
                        }));
    }

    @Override
    @Transactional
    public Mono<Void> clearUserCartById(Long userId) {
        return securityUserContextHandler.checkUserIdOrThrow(userId)
                .then(cartRepository.clearUserCartById(userId)
                        .doOnSuccess(clear -> log.info("Корзина пользователя с ID {} была очищена.", userId))
                        .then());
    }

    private Long countTotal(List<ItemRsDto> itemsInCart) {
        return itemsInCart
                .stream()
                .mapToLong(item -> item.count() * item.price())
                .sum();
    }
}
