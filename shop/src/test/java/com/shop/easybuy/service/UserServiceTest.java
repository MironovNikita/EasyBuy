package com.shop.easybuy.service;

import com.shop.easybuy.client.api.payment.PaymentApi;
import com.shop.easybuy.common.exception.RegistrationFailedException;
import com.shop.easybuy.common.mapper.UserMapper;
import com.shop.easybuy.entity.user.User;
import com.shop.easybuy.entity.user.UserCreateDto;
import com.shop.easybuy.repository.user.UserRepository;
import com.shop.easybuy.service.user.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.shop.easybuy.DataCreator.createUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentApi paymentApi;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Проверка успешной регистрации пользователя")
    void shouldRegisterUserSuccessfully() {
        Long userId = 1L;
        User user = createUser(userId);
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setEmail(user.getEmail());
        userCreateDto.setPassword(user.getPassword());
        userCreateDto.setName(user.getName());
        userCreateDto.setSurname(user.getSurname());
        userCreateDto.setPhone(user.getPhone());

        when(userRepository.save(user)).thenReturn(Mono.just(user));
        when(userMapper.toUser(any())).thenReturn(user);
        when(paymentApi.setBalance(any())).thenReturn(Mono.empty());

        StepVerifier.create(userService.register(userCreateDto))
                .verifyComplete();

        verify(userRepository).save(user);
        verify(userMapper).toUser(userCreateDto);
        verify(paymentApi).setBalance(any());
    }

    @Test
    @DisplayName("Проверка выброса ошибки при регистрации пользователя, если ошибка в установке баланса")
    void shouldThrowRegistrationException() {
        Long userId = 1L;
        User user = createUser(userId);
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setEmail(user.getEmail());
        userCreateDto.setPassword(user.getPassword());
        userCreateDto.setName(user.getName());
        userCreateDto.setSurname(user.getSurname());
        userCreateDto.setPhone(user.getPhone());

        when(userRepository.save(user)).thenReturn(Mono.just(user));
        when(userMapper.toUser(any())).thenReturn(user);
        when(paymentApi.setBalance(any())).thenReturn(Mono.error(new RuntimeException("Some exception")));

        StepVerifier.create(userService.register(userCreateDto))
                .expectErrorMatches(throwable -> throwable instanceof RegistrationFailedException
                        && throwable.getMessage().equals("Ошибка регистрации пользователя: Some exception"))
                .verify();

        verify(userRepository).save(user);
        verify(userMapper).toUser(userCreateDto);
        verify(paymentApi).setBalance(any());
    }


}
