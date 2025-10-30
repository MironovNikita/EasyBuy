package com.shop.easybuy.controller;

import com.shop.easybuy.integrationSettings.MockSecurityConfig;
import com.shop.easybuy.integrationSettings.PaymentServiceMockConfig;
import com.shop.easybuy.repository.cart.CartRepository;
import com.shop.easybuy.repository.item.ItemRepository;
import com.shop.easybuy.service.order.OrderService;
import com.shop.easybuy.testDB.AbstractTestDatabaseInitialization;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.Set;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@Import({PaymentServiceMockConfig.class, MockSecurityConfig.class})
public abstract class BaseIntegrationTest extends AbstractTestDatabaseInitialization {

    private static final Set<String> ALLOWED_TABLES = Set.of("cart", "order_items", "orders", "users");

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

    @MockitoBean
    protected ReactiveClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    protected ReactiveOAuth2AuthorizedClientService authorizedClientService;
}
