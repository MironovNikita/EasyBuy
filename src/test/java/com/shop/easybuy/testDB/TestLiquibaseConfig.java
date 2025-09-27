package com.shop.easybuy.testDB;

import jakarta.annotation.PostConstruct;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.boot.test.context.TestConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;

@TestConfiguration
public class TestLiquibaseConfig {

    @PostConstruct
    public void runLiquibase() throws Exception {
        try (Connection conn = DriverManager.getConnection(
                CommonPostgresContainer.getJdbcUrl(),
                CommonPostgresContainer.getUsername(),
                CommonPostgresContainer.getPassword()
        )) {
            Liquibase liquibase = new Liquibase(
                    "db/changelog/liquibase/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    new JdbcConnection(conn)
            );
            liquibase.update(new Contexts(), new LabelExpression());
        }
    }
}
