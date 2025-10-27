package com.shop.easybuy.entity.user;

import com.shop.easybuy.common.validation.Create;
import com.shop.easybuy.common.validation.Phone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateDto {

    @Email(groups = Create.class, message = "Переданный email не соответствует формату.")
    @NotBlank(groups = Create.class, message = "Поле email должно быть заполнено.")
    @Size(groups = Create.class, min = 5, max = 50, message = "Размер email должен составлять от 5 до 50 символов")
    private String email;

    @NotBlank(groups = Create.class, message = "Поле пароля должно быть заполнено.")
    @Size(groups = Create.class, min = 5, max = 50, message = "Размер пароля должен составлять от 5 до 50 символов")
    private String password;

    @NotBlank(groups = Create.class, message = "Поле имени должно быть заполнено.")
    @Size(groups = Create.class, min = 2, max = 30, message = "Размер имени должен составлять от 2 до 30 символов")
    private String name;

    @NotBlank(groups = Create.class, message = "Поле фамилии должно быть заполнено.")
    @Size(groups = Create.class, min = 2, max = 30, message = "Размер фамилии должен составлять от 2 до 30 символов")
    private String surname;

    @Phone(groups = Create.class)
    @NotBlank(groups = Create.class, message = "Поле номера телефона должно быть заполнено.")
    @Size(groups = Create.class, min = 11, max = 11, message = "Размер номера телефона должен составлять 11 символов")
    private String phone;
}
