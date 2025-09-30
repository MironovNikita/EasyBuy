package com.shop.easybuy.repository;

import com.shop.easybuy.repository.cart.CartRepository;
import com.shop.easybuy.repository.item.ItemRepository;
import com.shop.easybuy.repository.order.OrderItemRepository;
import com.shop.easybuy.repository.order.OrderRepository;
import com.shop.easybuy.testDB.AbstractTestDatabaseInitialization;
import com.shop.easybuy.testDB.LiquibaseTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Flux;

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
@ContextConfiguration(classes = {LiquibaseTestConfig.class})
public abstract class AbstractRepositoryTest extends AbstractTestDatabaseInitialization {

    protected static final Set<String> ALLOWED_TABLES = Set.of("cart", "order_items", "orders");

    @BeforeEach
    void clearTables(@Autowired DatabaseClient client) {
        Flux.fromIterable(ALLOWED_TABLES)
                .flatMap(t -> client.sql("TRUNCATE TABLE " + t + " RESTART IDENTITY CASCADE").then())
                .blockLast();
    }
}
