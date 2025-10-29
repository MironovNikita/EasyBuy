package com.shop.easybuy.controller;

import com.shop.easybuy.container.AbstractTestRedisInitialization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@Import(TestSecurityConfig.class)
public abstract class BaseIntegrationTest extends AbstractTestRedisInitialization {

    @Autowired
    protected WebTestClient webClient;

    @Autowired
    protected ReactiveStringRedisTemplate redisTemplate;

    @BeforeEach
    public void setUpBalance() {
        redisTemplate
                .opsForValue()
                .set("balance:" + 1, "20000")
                .block();
    }

    @AfterEach
    public void clearUpBalance() {
        redisTemplate
                .opsForValue()
                .delete("balance:" + 1)
                .block();
    }
}
