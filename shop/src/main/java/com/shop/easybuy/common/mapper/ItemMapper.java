package com.shop.easybuy.common.mapper;

import com.shop.easybuy.entity.cache.CachedItem;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.repository.cart.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    private final CartRepository cartRepository;

    public Mono<ItemRsDto> toItemRsDtoMono(CachedItem cachedItem, Long userId) {

        return cartRepository.findItemQuantityInCartByItemIdAndUserId(cachedItem.id(), userId)
                .onErrorResume(e -> Mono.just(0))
                .defaultIfEmpty(0)
                .map(quantity -> new ItemRsDto(
                        cachedItem.id(),
                        cachedItem.title(),
                        cachedItem.description(),
                        cachedItem.image(),
                        quantity.longValue(),
                        cachedItem.price())
                );
    }

    public CachedItem toCachedItemMono(ItemRsDto itemRsDto) {
        return new CachedItem(
                itemRsDto.id(),
                itemRsDto.title(),
                itemRsDto.description(),
                itemRsDto.image(),
                itemRsDto.price()
        );
    }
}
