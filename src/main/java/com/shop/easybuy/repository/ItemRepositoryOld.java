package com.shop.easybuy.repository;

public interface ItemRepositoryOld {

    //@Query("""
    //        SELECT new com.shop.easybuy.entity.item.ItemRsDto(
    //            i.id,
    //            i.title,
    //            i.description,
    //            i.imagePath,
    //            COALESCE(c.quantity, 0),
    //            i.price
    //        )
    //        FROM Item i
    //        LEFT JOIN CartItem c ON i.id = c.itemId
    //        WHERE i.title ILIKE %:search% or i.description LIKE %:search%
    //        """)
    //Page<ItemRsDto> findAllByTitleOrDescription(@Param("search") String search, Pageable pageable);
//
    //@Query("""
    //        SELECT new com.shop.easybuy.entity.item.ItemRsDto(
    //            i.id,
    //            i.title,
    //            i.description,
    //            i.imagePath,
    //            COALESCE(c.quantity, 0),
    //            i.price
    //        )
    //        FROM Item i
    //        LEFT JOIN CartItem c ON i.id = c.itemId
    //        WHERE i.id = c.itemId
    //        ORDER BY c.addedAt ASC
    //        """)
    //List<ItemRsDto> findAllInCart();
//
    //@Query("""
    //        SELECT new com.shop.easybuy.entity.item.ItemRsDto(
    //        i.id,
    //        i.title,
    //        i.description,
    //        i.imagePath,
    //        COALESCE(c.quantity, 0),
    //        i.price
    //        )
    //        FROM Item i
    //        LEFT JOIN CartItem c ON i.id = c.itemId
    //        WHERE i.id = :id
    //        """
    //)
    //Optional<ItemRsDto> findItemById(Long id);
}
