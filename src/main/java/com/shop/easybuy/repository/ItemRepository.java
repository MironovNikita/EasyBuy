package com.shop.easybuy.repository;

import com.shop.easybuy.entity.item.Item;
import com.shop.easybuy.entity.item.ItemRsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("""
            SELECT new com.shop.easybuy.entity.item.ItemRsDto(
                i.id,
                i.title,
                i.description,
                i.imagePath,
                COALESCE(c.quantity, 0),
                i.price
            )
            FROM Item i
            LEFT JOIN CartItem c ON i.id = c.itemId
            WHERE i.title ILIKE %:search% or i.description LIKE %:search%
            """)
    Page<ItemRsDto> findAllByTitleOrDescription(@Param("search") String search, Pageable pageable);

    @Query("""
            SELECT new com.shop.easybuy.entity.item.ItemRsDto(
                i.id,
                i.title,
                i.description,
                i.imagePath,
                COALESCE(c.quantity, 0),
                i.price
            )
            FROM Item i
            LEFT JOIN CartItem c ON i.id = c.itemId
            WHERE i.id = c.itemId
            ORDER BY c.addedAt ASC
            """)
    List<ItemRsDto> findAllInCart();

    @Query("""
            SELECT new com.shop.easybuy.entity.item.ItemRsDto(
            i.id,
            i.title,
            i.description,
            i.imagePath,
            COALESCE(c.quantity, 0),
            i.price
            )
            FROM Item i
            LEFT JOIN CartItem c ON i.id = c.itemId
            WHERE i.id = :id
            """
    )
    Optional<ItemRsDto> findItemById(Long id);
}
