package com.shop.easybuy.entity.user;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain=true)
@Table(name = "users")
public class User {

    @Id
    private Long id;

    private String email;

    private String password;

    private String name;

    private String surname;

    private String phone;
}
