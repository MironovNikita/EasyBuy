package com.shop.easybuy.entity.item;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "items")
@Data
public class Item {

    @Id
    private Long id;

    private String title;

    private String description;

    private String image;

    private Long price;
}
