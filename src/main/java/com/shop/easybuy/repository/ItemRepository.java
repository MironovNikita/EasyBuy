package com.shop.easybuy.repository;

import com.shop.easybuy.entity.item.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.title ILIKE %:search% or i.description LIKE %:search%")
    List<Item> findAllByTitleOrDescription(@Param("search") String search);

    @Query("SELECT i FROM Item i WHERE i.title ILIKE %:search% or i.description LIKE %:search%")
    Page<Item> findAllByTitleOrDescription(@Param("search") String search, PageRequest pageRequest);

    //boolean hasNextPage(String search, int limit, int offset);
}
