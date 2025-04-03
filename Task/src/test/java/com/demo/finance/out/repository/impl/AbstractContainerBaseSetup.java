package com.demo.finance.out.repository.impl;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.fail;

public abstract class AbstractContainerBaseSetup {

    private static final String LIQUIBASE_CHANGELOG = "db/changelog/changelog-test.xml";

    private static class SingletonContainer {
        @SuppressWarnings("resource")
        private static final PostgreSQLContainer<?> INSTANCE =
                new PostgreSQLContainer<>(DockerImageName.parse("postgres:16")).withDatabaseName("testdb")
                        .withUsername("testuser").withPassword("testpass").withReuse(true)
                        .waitingFor(Wait.forListeningPort());

        static {
            INSTANCE.start();
        }
    }

    @Container
    protected static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = SingletonContainer.INSTANCE;

    @BeforeAll
    static void setupDatabase() {
        try {
            System.setProperty("ENV_PATH", "src/test/resources/.env");
            System.setProperty("YML_PATH", "src/test/resources/application.yml");
            System.setProperty("DB_URL", String.format("jdbc:postgresql://localhost:%d/testdb",
                    POSTGRESQL_CONTAINER.getFirstMappedPort()
            ));
            try (Connection conn = DriverManager.getConnection(POSTGRESQL_CONTAINER.getJdbcUrl(),
                    POSTGRESQL_CONTAINER.getUsername(), POSTGRESQL_CONTAINER.getPassword())) {
                Database database = DatabaseFactory.getInstance()
                        .findCorrectDatabaseImplementation(new JdbcConnection(conn));
                Liquibase liquibase = new Liquibase(LIQUIBASE_CHANGELOG, new ClassLoaderResourceAccessor(), database);
                liquibase.update(new Contexts(), new LabelExpression());
            }
        } catch (SQLException | LiquibaseException e) {
            fail("Database setup failed: " + e.getMessage());
        }
    }

    @BeforeEach
    void cleanDatabase() {
        try (Connection conn = DriverManager.getConnection(POSTGRESQL_CONTAINER.getJdbcUrl(),
                POSTGRESQL_CONTAINER.getUsername(), POSTGRESQL_CONTAINER.getPassword());
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP SCHEMA public CASCADE");
            stmt.execute("CREATE SCHEMA public");
        } catch (SQLException e) {
            fail("Database cleanup failed: " + e.getMessage());
        }
    }
}