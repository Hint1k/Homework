package com.demo.finance.app.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DatabaseConfigTest {

    private static final Logger log = Logger.getLogger(DatabaseConfigTest.class.getName());
    private DatabaseConfig databaseConfig;

    @BeforeEach
    void setUp() {
        try {
            System.setProperty("ENV_PATH", "src/test/resources/.env");

            System.clearProperty("DB_URL");
            System.clearProperty("DB_USERNAME");
            System.clearProperty("DB_PASSWORD");

            System.setProperty("DB_URL", "jdbc:postgresql://localhost:5432/testdb");
            System.setProperty("DB_USERNAME", "testuser");
            System.setProperty("DB_PASSWORD", "testpass");

            databaseConfig = DatabaseConfig.getInstance();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error occurred during test setup: " + e.getMessage(), e);
            fail("Test setup failed due to an unexpected exception.");
        }
    }

    @Test
    @DisplayName("Get DB URL - Success scenario")
    void testGetDbUrl_Success() {
        String dbUrl = databaseConfig.getDbUrl();
        assertThat(dbUrl).isEqualTo("jdbc:postgresql://localhost:5432/testdb");
    }

    @Test
    @DisplayName("Get DB URL - Missing URL throws exception")
    void testGetDbUrl_MissingUrl_ThrowsException() {
        try {
            System.clearProperty("DB_URL");

            assertThatThrownBy(() -> databaseConfig.getDbUrl()).isInstanceOf(RuntimeException.class)
                    .hasMessage("DB_URL is not configured.");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error occurred while testing missing DB_URL: " + e.getMessage(), e);
            fail("Test failed due to an unexpected exception while testing missing DB_URL.");
        }
    }

    @Test
    @DisplayName("Get DB Username - Success scenario")
    void testGetDbUsername_Success() {
        String dbUsername = databaseConfig.getDbUsername();
        assertThat(dbUsername).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Get DB Username - Missing username throws exception")
    void testGetDbUsername_MissingUsername_ThrowsException() {
        try {
            System.clearProperty("DB_USERNAME");

            assertThatThrownBy(() -> databaseConfig.getDbUsername()).isInstanceOf(RuntimeException.class)
                    .hasMessage("DB_USERNAME is not configured.");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error occurred while testing missing DB_USERNAME: " + e.getMessage(), e);
            fail("Test failed due to an unexpected exception while testing missing DB_USERNAME.");
        }
    }

    @Test
    @DisplayName("Get DB Password - Success scenario")
    void testGetDbPassword_Success() {
        String dbPassword = databaseConfig.getDbPassword();
        assertThat(dbPassword).isEqualTo("testpass");
    }

    @Test
    @DisplayName("Get DB Password - Missing password throws exception")
    void testGetDbPassword_MissingPassword_ThrowsException() {
        try {
            System.clearProperty("DB_PASSWORD");

            assertThatThrownBy(() -> databaseConfig.getDbPassword()).isInstanceOf(RuntimeException.class)
                    .hasMessage("DB_PASSWORD is not configured.");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error occurred while testing missing DB_PASSWORD: " + e.getMessage(), e);
            fail("Test failed due to an unexpected exception while testing missing DB_PASSWORD.");
        }
    }

    @Test
    @DisplayName("Override with system properties - Success scenario")
    void testOverrideWithSystemProperties_Success() {
        try {
            System.setProperty("DB_URL", "jdbc:postgresql://localhost:5432/overridden");
            System.setProperty("DB_USERNAME", "overriddenUser");
            System.setProperty("DB_PASSWORD", "overriddenPass");

            databaseConfig = DatabaseConfig.getInstance();

            assertThat(databaseConfig.getDbUrl()).isEqualTo("jdbc:postgresql://localhost:5432/overridden");
            assertThat(databaseConfig.getDbUsername()).isEqualTo("overriddenUser");
            assertThat(databaseConfig.getDbPassword()).isEqualTo("overriddenPass");

            System.clearProperty("DB_URL");
            System.clearProperty("DB_USERNAME");
            System.clearProperty("DB_PASSWORD");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error occurred while testing override with system properties: "
                    + e.getMessage(), e);
            fail("Test failed due to an unexpected exception while testing override "
                    + "with system properties.");
        }
    }

    @Test
    @DisplayName("Override with system properties - Missing system properties uses default values")
    void testOverrideWithSystemProperties_MissingSystemProperties_UsesDefaultValues() {
        try {
            System.clearProperty("DB_URL");
            System.clearProperty("DB_USERNAME");
            System.clearProperty("DB_PASSWORD");

            databaseConfig = DatabaseConfig.getInstance();

            assertThatThrownBy(() -> databaseConfig.getDbUrl()).isInstanceOf(RuntimeException.class)
                    .hasMessage("DB_URL is not configured.");
            assertThatThrownBy(() -> databaseConfig.getDbUsername()).isInstanceOf(RuntimeException.class)
                    .hasMessage("DB_USERNAME is not configured.");
            assertThatThrownBy(() -> databaseConfig.getDbPassword()).isInstanceOf(RuntimeException.class)
                    .hasMessage("DB_PASSWORD is not configured.");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error occurred while testing missing system properties "
                    + "fallback to default values: " + e.getMessage(), e);
            fail("Test failed due to an unexpected exception while testing fallback to default values.");
        }
    }
}