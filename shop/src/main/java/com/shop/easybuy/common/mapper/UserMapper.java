package com.shop.easybuy.common.mapper;

import com.shop.easybuy.common.security.SecureBase64Converter;
import com.shop.easybuy.entity.user.User;
import com.shop.easybuy.entity.user.UserCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    private final SecureBase64Converter secureBase64Converter;

    public User toUser(UserCreateDto userCreateDto) {
        return new User()
                .setEmail(secureBase64Converter.encrypt(userCreateDto.getEmail()))
                .setPassword(passwordEncoder.encode(userCreateDto.getPassword()))
                .setName(userCreateDto.getName())
                .setSurname(userCreateDto.getSurname())
                .setPhone(secureBase64Converter.encrypt(userCreateDto.getPhone()));
    }
}
