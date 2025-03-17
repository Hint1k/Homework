package com.demo.finance.app.config;

import com.demo.finance.exception.DatabaseConnectionException;
import com.demo.finance.out.repository.AbstractContainerBaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DataSourceManagerTest extends AbstractContainerBaseTest {

    private static final Logger log = Logger.getLogger(DataSourceManagerTest.class.getName());

    @Test
    @DisplayName("Establish database connection successfully")
    void testGetConnection_Success() {
        assertThatCode(() -> {
            Connection connection = DataSourceManager.getConnection();
            assertThat(connection).isNotNull();
            connection.close();
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Fail to establish database connection with invalid credentials")
    void testGetConnection_InvalidCredentials() {
        overrideDatabaseConfig();

        assertThatCode(DataSourceManager::getConnection).isInstanceOf(DatabaseConnectionException.class)
                .hasMessageContaining("Failed to establish a database connection");
    }

    private void overrideDatabaseConfig() {
        try {
            System.setProperty("DB_URL", "jdbc:postgresql://invalid-host:5432/testdb");
            System.setProperty("DB_USERNAME", "invalid_user");
            System.setProperty("DB_PASSWORD", "invalid_password");

            log.info("Overridden database configuration with invalid credentials.");
        } catch (Exception e) {
            String logMessage = "Failed to override database configuration: " + e.getMessage();
            String failMessage = "Exception occurred while overriding database configuration: " + e.getMessage();
            log.log(Level.SEVERE, logMessage);
            fail(failMessage);
        }
    }
}