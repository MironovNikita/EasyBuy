package com.shop.easybuy.service.user;

import com.shop.easybuy.entity.user.UserCreateDto;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<Void> register(UserCreateDto userCreateDto);
}
