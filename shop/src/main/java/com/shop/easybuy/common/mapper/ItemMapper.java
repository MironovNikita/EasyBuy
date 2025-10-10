package com.shop.easybuy.common.mapper;

import com.shop.easybuy.client.model.cache.CachedItem;
import com.shop.easybuy.entity.item.ItemRsDto;
import com.shop.easybuy.repository.cart.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    private final CartRepository cartRepository;

    public Mono<ItemRsDto> toItemRsDtoMono(CachedItem cachedItem) {

        return cartRepository.findItemQuantityInCartByItemId(cachedItem.getId())
                .onErrorResume(e -> Mono.just(0))
                .defaultIfEmpty(0)
                .map(quantity -> new ItemRsDto(
                        cachedItem.getId(),
                        cachedItem.getTitle(),
                        cachedItem.getDescription(),
                        cachedItem.getImage(),
                        quantity.longValue(),
                        cachedItem.getPrice())
                );
    }

    public CachedItem toCachedItemMono(ItemRsDto itemRsDto) {
        CachedItem cachedItem = new CachedItem();
        cachedItem.setId(itemRsDto.id());
        cachedItem.setTitle(itemRsDto.title());
        cachedItem.setDescription(itemRsDto.description());
        cachedItem.setImage(itemRsDto.image());
        cachedItem.setPrice(itemRsDto.price());
        return cachedItem;
    }
}
