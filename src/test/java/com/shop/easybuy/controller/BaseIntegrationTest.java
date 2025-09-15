package com.shop.easybuy.controller;

import com.shop.easybuy.service.item.ItemService;
import com.shop.easybuy.service.order.OrderService;
import com.shop.easybuy.testDB.AbstractTestDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Rollback
@SqlGroup({
        @Sql(
                statements = {
                        "TRUNCATE TABLE order_items RESTART IDENTITY CASCADE",
                        "TRUNCATE TABLE orders RESTART IDENTITY CASCADE",
                        "TRUNCATE TABLE cart RESTART IDENTITY CASCADE"
                }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
        )
})
public abstract class BaseIntegrationTest extends AbstractTestDatabase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected OrderService orderService;

    @Autowired
    protected ItemService itemService;
}
