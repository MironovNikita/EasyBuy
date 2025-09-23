package com.shop.easybuy.repository;

import com.shop.easybuy.entity.item.Item;
import com.shop.easybuy.entity.item.ItemRsDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemRepository extends R2dbcRepository<Item, Long> {

    @Query("""
            SELECT i.id,
                    i.title,
                    i.description,
                    i.image_path AS imagePath,
                    COALESCE(c.quantity, 0) AS count,
                    i.price,
                    c.added_at as addedAt
            FROM items i
            LEFT JOIN cart c ON i.id = c.item_id
            WHERE i.title ILIKE :search or i.description LIKE :search
            LIMIT :limit OFFSET :offset
            """)
    Flux<ItemRsDto> findAllByTitleOrDescription(@Param("search") String search, Integer limit, Long offset);

    @Query("""
            SELECT COUNT(*)
            FROM items i
            WHERE i.title ILIKE :search or i.description LIKE :search
            """)
    Mono<Long> countItemsBySearch(@Param("search") String search);

    @Query("""
            SELECT i.id,
                    i.title,
                    i.description,
                    i.image_path AS imagePath,
                    COALESCE(c.quantity, 0) AS count,
                    i.price,
                    c.added_at AS addedAt
                FROM items i
                LEFT JOIN cart c ON i.id = c.item_id
                ORDER BY c.added_at
            """)
    Flux<ItemRsDto> findAllInCart();

    @Query("""
            SELECT i.id,
                    i.title,
                    i.description,
                    i.image_path AS imagePath,
                    COALESCE(c.quantity, 0) AS count,
                    i.price
            FROM items i
            LEFT JOIN cart c ON i.id = c.item_id
            WHERE i.id = :id
            """)
    Mono<ItemRsDto> findItemById(@Param("id") Long id);
}
