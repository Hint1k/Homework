package com.demo.finance.out.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.fail;

@Testcontainers
public abstract class AbstractContainerBaseTest {

    private static final Logger log = Logger.getLogger(AbstractContainerBaseTest.class.getName());

    @Container
    protected static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER;

    static {
        POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:16")
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpass");
        POSTGRESQL_CONTAINER.start();
    }

    @BeforeAll
    static void setupDatabase() {
        try {
            System.setProperty("ENV_PATH", "src/test/resources/.env");
            System.setProperty("DB_URL", POSTGRESQL_CONTAINER.getJdbcUrl());
            System.setProperty("DB_USERNAME", POSTGRESQL_CONTAINER.getUsername());
            System.setProperty("DB_PASSWORD", POSTGRESQL_CONTAINER.getPassword());

            try (Connection conn = DriverManager.getConnection(
                    POSTGRESQL_CONTAINER.getJdbcUrl(),
                    POSTGRESQL_CONTAINER.getUsername(),
                    POSTGRESQL_CONTAINER.getPassword());
                 Statement stmt = conn.createStatement()) {

                stmt.execute("CREATE SCHEMA IF NOT EXISTS finance");

                // Create tables for all entities
                stmt.execute("CREATE TABLE IF NOT EXISTS finance.budgets (" +
                        "budget_id SERIAL PRIMARY KEY, " +
                        "user_id BIGINT NOT NULL, " +
                        "monthly_limit DECIMAL(19,2) NOT NULL, " +
                        "current_expenses DECIMAL(19,2) NOT NULL" +
                        ");");

                stmt.execute("CREATE TABLE IF NOT EXISTS finance.goals (" +
                        "goal_id SERIAL PRIMARY KEY, " +
                        "user_id BIGINT NOT NULL, " +
                        "goal_name VARCHAR(255) NOT NULL, " +
                        "target_amount DECIMAL(19,2) NOT NULL, " +
                        "saved_amount DECIMAL(19,2) NOT NULL, " +
                        "duration INT NOT NULL, " +
                        "start_time DATE NOT NULL" +
                        ");");

                stmt.execute("CREATE TABLE IF NOT EXISTS finance.transactions (" +
                        "transaction_id SERIAL PRIMARY KEY, " +
                        "user_id BIGINT NOT NULL, " +
                        "amount DECIMAL(19,2) NOT NULL, " +
                        "category VARCHAR(255) NOT NULL, " +
                        "date DATE NOT NULL, " +
                        "description VARCHAR(255), " +
                        "type VARCHAR(50) NOT NULL" +
                        ");");

                stmt.execute("CREATE TABLE IF NOT EXISTS finance.users (" +
                        "user_id SERIAL PRIMARY KEY, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "email VARCHAR(255) UNIQUE NOT NULL, " +
                        "password VARCHAR(255) NOT NULL, " +
                        "blocked BOOLEAN NOT NULL DEFAULT FALSE, " +
                        "role VARCHAR(50) NOT NULL" +
                        ");");
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Failed to set up the database: " + e.getMessage(), e);
            fail("Database setup failed: " + e.getMessage());
        }
    }

    @BeforeEach
    void cleanDatabase() {
        try {
            try (Connection conn = DriverManager.getConnection(
                    POSTGRESQL_CONTAINER.getJdbcUrl(),
                    POSTGRESQL_CONTAINER.getUsername(),
                    POSTGRESQL_CONTAINER.getPassword());
                 Statement stmt = conn.createStatement()) {

                stmt.execute("TRUNCATE TABLE finance.goals, finance.transactions, "
                        + "finance.users, finance.budgets CASCADE");
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Failed to clean up the database: " + e.getMessage(), e);
            fail("Database cleanup failed: " + e.getMessage());
        }
    }

    @AfterAll
    static void stopContainer() {
        POSTGRESQL_CONTAINER.stop();
    }
}