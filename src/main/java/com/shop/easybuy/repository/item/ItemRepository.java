package com.shop.easybuy.repository.item;

import com.shop.easybuy.entity.item.Item;
import com.shop.easybuy.entity.item.ItemRsDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemRepository extends R2dbcRepository<Item, Long>, ItemRepositoryCustom {

    /*@Query("""
            SELECT i.id,
                    i.title,
                    i.description,
                    i.image,
                    COALESCE(c.quantity, 0) AS count,
                    i.price
            FROM items i
            LEFT JOIN cart c ON i.id = c.item_id
            WHERE i.title ILIKE CONCAT('%', :search, '%') or i.description ILIKE CONCAT('%', :search, '%')
            LIMIT :limit OFFSET :offset
            """)
    Flux<ItemRsDto> findAllByTitleOrDescription(@Param("search") String search, Integer limit, Long offset);*/

    @Query("""
            SELECT COUNT(*)
            FROM items i
            WHERE i.title ILIKE CONCAT('%', :search, '%') or i.description ILIKE CONCAT('%', :search, '%')
            """)
    Mono<Long> countItemsBySearch(@Param("search") String search);

    @Query("""
            SELECT i.id,
                    i.title,
                    i.description,
                    i.image,
                    COALESCE(c.quantity, 0) AS count,
                    i.price
                FROM items i
                LEFT JOIN cart c ON i.id = c.item_id
                WHERE i.id = c.item_id
                ORDER BY i.id
            """)
    Flux<ItemRsDto> findAllInCart();

    @Query("""
            SELECT i.id,
                    i.title,
                    i.description,
                    i.image,
                    COALESCE(c.quantity, 0) AS count,
                    i.price
            FROM items i
            LEFT JOIN cart c ON i.id = c.item_id
            WHERE i.id = :id
            """)
    Mono<ItemRsDto> findItemById(@Param("id") Long id);
}
