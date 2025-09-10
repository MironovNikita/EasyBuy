package com.shop.easybuy.entity.item;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ItemResponseDto {

    private Long id;

    private String title;

    private String description;

    private String imagePath;

    private Integer count;

    private Long price;
}
