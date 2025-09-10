package com.shop.easybuy.repository;

import com.shop.easybuy.entity.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.title LIKE %:search% or i.description LIKE %:search%")
    List<Item> findAllByTitleOrDescription(@Param("search") String search);
}
