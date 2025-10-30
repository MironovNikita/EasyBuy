package com.shop.easybuy.security.controller;

import com.shop.easybuy.entity.user.UserCreateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserControllerSecurityTest extends CommonSecurityTest {

    @Test
    @WithAnonymousUser
    @DisplayName("Проверка доступа всех пользователей к странице логина")
    void shouldGetAccessToLoginPage() {
        webClient
                .get()
                .uri("/login")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Проверка доступа всех пользователей к странице регистрации")
    void shouldGetAccessToRegisterPage() {
        webClient
                .get()
                .uri("/register")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Проверка доступа всех пользователей к регистрации")
    void shouldPostRegistration() {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("Test");
        dto.setSurname("User");
        dto.setEmail("test@example.com");
        dto.setPassword("Password123");
        dto.setPhone("88888888888");

        when(userService.register(dto)).thenReturn(Mono.empty());

        webClient
                .post()
                .uri("/register")
                .body(BodyInserters
                        .fromFormData("name", "Test")
                        .with("surname", "User")
                        .with("email", "test@example.com")
                        .with("password", "Password123")
                        .with("phone", "88888888888"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/login");

        verify(userService).register(any(UserCreateDto.class));
    }

}
