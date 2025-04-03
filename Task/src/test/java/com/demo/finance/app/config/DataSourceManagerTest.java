package com.demo.finance.app.config;

import com.demo.finance.exception.DatabaseConnectionException;
import com.demo.finance.out.repository.impl.AbstractContainerBaseSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DataSourceManagerTest extends AbstractContainerBaseSetup {

    private DataSourceManager dataSourceManager;

    @BeforeEach
    void setUp() {
        System.setProperty("DB_URL", POSTGRESQL_CONTAINER.getJdbcUrl());
        System.setProperty("DB_USERNAME", POSTGRESQL_CONTAINER.getUsername());
        System.setProperty("DB_PASSWORD", POSTGRESQL_CONTAINER.getPassword());
        dataSourceManager = new DataSourceManager(new DatabaseConfig());
    }

    @AfterEach
    void resetProperties() {
        System.clearProperty("DB_URL");
        System.clearProperty("DB_USERNAME");
        System.clearProperty("DB_PASSWORD");
    }

    @Test
    @DisplayName("Establish database connection successfully")
    void testGetConnection_Success() {
        assertThatCode(() -> {
            Connection connection = dataSourceManager.getConnection();
            assertThat(connection).isNotNull();
            connection.close();
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Fail to establish database connection with invalid credentials")
    void testGetConnection_InvalidCredentials() {
        overrideDatabaseConfig();

        assertThatThrownBy(() -> dataSourceManager.getConnection())
                .isInstanceOf(DatabaseConnectionException.class)
                .hasMessageContaining("Failed to establish a database connection")
                .hasMessageContaining("jdbc:postgresql://invalid-host:5432/testdb");
    }

    @Test
    @DisplayName("Connection is valid when established")
    void testConnectionIsValid() {
        try (Connection connection = dataSourceManager.getConnection()) {
            assertThat(connection.isValid(5)).isTrue();
        } catch (SQLException e) {
           fail("An unexpected SQLException occurred." + e.getMessage());
        }
    }

    private void overrideDatabaseConfig() {
        try {
            System.setProperty("DB_URL", "jdbc:postgresql://invalid-host:5432/testdb");
            System.setProperty("DB_USERNAME", "invalid_user");
            System.setProperty("DB_PASSWORD", "invalid_password");
        } catch (Exception e) {
            fail("Failed to override database configuration." + e.getMessage());
        }
    }
}