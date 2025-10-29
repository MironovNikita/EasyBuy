package com.shop.easybuy.service.user;

import com.shop.easybuy.client.api.payment.PaymentApi;
import com.shop.easybuy.client.model.payment.BalanceSetRq;
import com.shop.easybuy.common.exception.RegistrationFailedException;
import com.shop.easybuy.common.mapper.UserMapper;
import com.shop.easybuy.entity.user.UserCreateDto;
import com.shop.easybuy.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    private final PaymentApi paymentApi;

    @Value("${balance.initial.value}")
    private Long initialValue;

    @Transactional
    public Mono<Void> register(UserCreateDto userCreateDto) {
        return userRepository.save(userMapper.toUser(userCreateDto))
                .flatMap(user -> paymentApi.setBalance(new BalanceSetRq()
                                .userId(user.getId())
                                .balance(initialValue))
                        .doOnSuccess(status -> log.info("Баланс для пользователя с email {} был установлен: {}", userCreateDto.getEmail(), status))
                        .thenReturn(user)
                )
                .doOnSuccess(user -> log.info("Пользователь c email {} был успешно зарегистрирован.", userCreateDto.getEmail()))
                .onErrorResume(e -> {
                    log.error("Ошибка установки баланса: {}", e.getMessage());
                    return Mono.error(new RegistrationFailedException(e.getMessage()));
                })
                .then();
    }
}
