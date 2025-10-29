package com.shop.easybuy.repository;

import com.shop.easybuy.entity.user.User;
import com.shop.easybuy.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static com.shop.easybuy.DataCreator.createUser;
import static com.shop.easybuy.DataInserter.insertIntoUserTable;
import static org.assertj.core.api.Assertions.assertThat;

public class UserRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Проверка поиска пользователя по email")
    void shouldFindUserByEmailSuccessfully() {
        Long userId = 1L;
        User user = createUser(userId);
        insertIntoUserTable(databaseClient, user).block();

        StepVerifier.create(userRepository.findUserByEmail(user.getEmail()))
                .assertNext(result -> {
                    assertThat(user.getId().equals(result.getId()));
                    assertThat(user.getEmail().equals(result.getEmail()));
                    assertThat(user.getName().equals(result.getName()));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Проверка отсутствия пользователя по email")
    void shouldRegisterUserSuccessfully() {
        Long userId = 1L;
        User user = createUser(userId);

        StepVerifier.create(userRepository.findUserByEmail(user.getEmail()))
                .expectNextCount(0)
                .verifyComplete();
    }
}
