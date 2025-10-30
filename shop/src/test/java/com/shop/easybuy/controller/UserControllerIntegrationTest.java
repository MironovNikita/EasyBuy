package com.shop.easybuy.controller;

import com.shop.easybuy.entity.user.UserCreateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.BodyInserters;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Проверка редиректа на страницу register")
    void shouldOpenRegisterPage() {

        webClient.get()
                .uri("/register")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Регистрация"));
                    assertTrue(body.contains("Зарегистрироваться"));
                    assertTrue(body.contains("Уже есть аккаунт?"));
                });
    }

    @Test
    @DisplayName("Проверка регистрации пользователя")
    void shouldRegisterUser() {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setEmail("test@test.ru");
        userCreateDto.setName("test name");
        userCreateDto.setSurname("Test surname");
        userCreateDto.setPassword("Password");
        userCreateDto.setPhone("88888888888");

        webClient.post()
                .uri("/register")
                .body(BodyInserters.fromFormData("name", "Test name")
                        .with("surname", "Test surname")
                        .with("email", "test@test.ru")
                        .with("phone", "88888888888")
                        .with("password", "Test password"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/login");
    }
}
