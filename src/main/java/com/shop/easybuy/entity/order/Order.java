package com.shop.easybuy.entity.order;

import com.shop.easybuy.entity.item.Item;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "item_id")
    @OneToMany
    private List<Item> items;
}
