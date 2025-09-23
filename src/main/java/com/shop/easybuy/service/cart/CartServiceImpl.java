package com.shop.easybuy.service.cart;

import com.shop.easybuy.common.entity.ActionEnum;
import com.shop.easybuy.common.exception.ObjectNotFoundException;
import com.shop.easybuy.entity.cart.CartItem;
import com.shop.easybuy.entity.cart.CartViewDto;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.repository.CartRepository;
import com.shop.easybuy.repository.ItemRepository;
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
//TODO Навести порядок в классе
public class CartServiceImpl implements CartService {

    private static final int rowSize = 5;

    private final CartRepository cartRepository;

    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public Mono<Void> changeQuantity(Long itemId, ActionEnum action) {
        return switch (action) {
            case PLUS -> cartRepository.findById(itemId)
                    .defaultIfEmpty(new CartItem(itemId, 0))
                    .flatMap(found -> {
                        found.setQuantity(found.getQuantity() + 1);
                        logCart(itemId, found.getQuantity());
                        return cartRepository.save(found);
                    })
                    .then();

            case MINUS -> cartRepository.findById(itemId)
                    .switchIfEmpty(Mono.defer(() -> {
                        log.error("Товар с указанным ID {} не был найден.", itemId);
                        return Mono.error(new ObjectNotFoundException("Товар", itemId));
                    }))
                    .flatMap(found -> {
                        var quantity = found.getQuantity();
                        if (quantity > 1) {
                            found.setQuantity(quantity - 1);
                            logCart(itemId, found.getQuantity());
                            return cartRepository.save(found);
                        } else {
                            logCart(itemId, 0);
                            return cartRepository.deleteById(itemId);
                        }
                    })
                    .then();

            case DELETE -> cartRepository.deleteById(itemId)
                    .doOnSuccess(aVoid -> logCart(itemId, 0))
                    .then();
        };
    }

    private void logCart(Long itemId, Integer quantity) {
        if (quantity > 0) log.info("В корзине обновлено количество товара с ID {}. Текущее количество: {}.", itemId, quantity);
        else log.info("Товар с ID {} был удалён из корзины.", itemId);
    }

    @Override
    public Mono<CartViewDto> getAllItems() {

        return itemRepository.findAllInCart()
                .collectList()
                .map(items -> {
                    var total = countTotal(items);
                    var foundItems = Utils.splitList(items, rowSize);

                    log.info("В корзине найдено {} товаров.", items.size());
                    return new CartViewDto(foundItems, total);
                });
    }

    @Override
    @Transactional
    public Mono<Void> clearCart() {
        return cartRepository.clearCart()
                .doOnSuccess(count -> log.info("Корзина была очищена. Удалено {} товаров.", count))
                .then();
    }

    private Long countTotal(List<ItemRsDto> itemsInCart) {
        return itemsInCart
                .stream()
                .mapToLong(item -> item.count() * item.price())
                .sum();
    }
}
