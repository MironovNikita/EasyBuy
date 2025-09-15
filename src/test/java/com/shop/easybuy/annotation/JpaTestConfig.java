package com.shop.easybuy.annotation;

import com.shop.easybuy.repository.CartRepository;
import com.shop.easybuy.repository.ItemRepository;
import com.shop.easybuy.repository.OrderRepository;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DataJpaTest(
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {CartRepository.class, ItemRepository.class, OrderRepository.class})
)
@Transactional
@Rollback
@SqlGroup({
        @Sql(
                statements = {
                        "TRUNCATE TABLE order_items RESTART IDENTITY CASCADE",
                        "TRUNCATE TABLE orders RESTART IDENTITY CASCADE",
                        "TRUNCATE TABLE cart RESTART IDENTITY CASCADE"
                }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
        )
})
public @interface JpaTestConfig {
}
