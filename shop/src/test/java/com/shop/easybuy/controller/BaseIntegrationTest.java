package com.shop.easybuy.controller;

import com.shop.easybuy.repository.cart.CartRepository;
import com.shop.easybuy.repository.item.ItemRepository;
import com.shop.easybuy.service.order.OrderService;
import com.shop.easybuy.testDB.AbstractTestDatabaseInitialization;
import com.shop.easybuy.testRedis.RedisServiceMockConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.Set;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@Import(RedisServiceMockConfig.class)
public abstract class BaseIntegrationTest extends AbstractTestDatabaseInitialization {

    private static final Set<String> ALLOWED_TABLES = Set.of("cart", "order_items", "orders");

    @Autowired
    protected WebTestClient webClient;

    @Autowired
    protected DatabaseClient databaseClient;

    @BeforeEach
    protected void cleanUp() {
        Flux.fromIterable(ALLOWED_TABLES)
                .flatMap(t -> databaseClient.sql("TRUNCATE TABLE " + t + " RESTART IDENTITY CASCADE").then())
                .blockLast();
    }

    @Autowired
    protected CartRepository cartRepository;

    @Autowired
    protected ItemRepository itemRepository;

    @Autowired
    protected OrderService orderService;
}
