package com.shop.easybuy.service.user;

import com.shop.easybuy.common.mapper.UserMapper;
import com.shop.easybuy.entity.user.UserCreateDto;
import com.shop.easybuy.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

//TODO Сделать интерфейс
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    public Mono<Void> register(UserCreateDto userCreateDto) {
        return userRepository.save(userMapper.toUser(userCreateDto))
                .doOnSuccess(l -> log.info("Пользователь с email {} был успешно зарегистрирован", userCreateDto.getEmail()))
                .then();
    }
}
