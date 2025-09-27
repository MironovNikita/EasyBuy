package com.shop.easybuy.testDB;

import com.shop.easybuy.repository.cart.CartRepository;
import com.shop.easybuy.repository.item.ItemRepository;
import com.shop.easybuy.repository.order.OrderItemRepository;
import com.shop.easybuy.repository.order.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import reactor.core.publisher.Mono;

import java.util.Set;

@DataR2dbcTest(
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        ItemRepository.class,
                        OrderRepository.class,
                        CartRepository.class,
                        OrderItemRepository.class
                }
        )
)
@ActiveProfiles("test")
@ContextConfiguration(classes = {TestLiquibaseConfig.class})
public abstract class AbstractRepositoryTest {

    private static final Set<String> ALLOWED_TABLES = Set.of("cart", "order_items", "orders");

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", CommonPostgresContainer::getR2dbcUrl);
        registry.add("spring.r2dbc.username", CommonPostgresContainer::getUsername);
        registry.add("spring.r2dbc.password", CommonPostgresContainer::getPassword);

        registry.add("spring.datasource.url", CommonPostgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", CommonPostgresContainer::getUsername);
        registry.add("spring.datasource.password", CommonPostgresContainer::getPassword);

        registry.add("spring.liquibase.enabled", () -> true);
    }

    @BeforeEach
    void clearTables(@Autowired DatabaseClient client) {
        Mono.when(
                ALLOWED_TABLES.stream()
                        .map(t -> Mono.defer(() -> client.sql("TRUNCATE TABLE " + t + " RESTART IDENTITY CASCADE").then()))
                        .toList()
        ).block();
    }
}
