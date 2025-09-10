package com.shop.easybuy.entity.item;

import jakarta.persistence.*;
import lombok.Data;

//InputStream imageStream = getClass().getClassLoader().getResourceAsStream("db/item.images/item1.jpg");

@Entity
@Table(name = "items")
@Data
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Column(name = "image_path")
    private String imagePath;

    private Long price;
}
