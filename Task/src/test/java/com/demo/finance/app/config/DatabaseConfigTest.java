package com.demo.finance.app.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class DatabaseConfigTest {

    private DatabaseConfig databaseConfig;

    @BeforeEach
    void setUp() {
        try {
            databaseConfig = new DatabaseConfig();
            ReflectionTestUtils.setField(databaseConfig, "injectedUrl",
                    "jdbc:postgresql://localhost:5432/testdb");
            System.setProperty("ENV_PATH", "src/test/resources/.env");
            System.setProperty("DB_USERNAME", "testuser");
            System.setProperty("DB_PASSWORD", "testpass");
            databaseConfig.init();
        } catch (Exception e) {
            fail("Test setup failed due to an unexpected exception.", e);
        }
    }

    @Test
    @DisplayName("Get DB URL - Success scenario")
    void testGetDbUrl_Success() {
        String dbUrl = databaseConfig.getDbUrl();
        assertThat(dbUrl).isEqualTo("jdbc:postgresql://localhost:5432/testdb");
    }

    @Test
    @DisplayName("Get DB URL - Missing injectedUrl and DB_URL throws exception")
    void testGetDbUrl_MissingUrl_ThrowsException() {
        System.clearProperty("DB_URL");
        ReflectionTestUtils.setField(databaseConfig, "injectedUrl", null);
        assertThatThrownBy(databaseConfig::init)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB_URL is not configured.");
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
        System.clearProperty("DB_USERNAME");
        assertThatThrownBy(() -> databaseConfig.getDbUsername())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB_USERNAME is not configured.");
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
        System.clearProperty("DB_PASSWORD");
        assertThatThrownBy(() -> databaseConfig.getDbPassword())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB_PASSWORD is not configured.");
    }

    @Test
    @DisplayName("Override with system properties - Success scenario")
    void testOverrideWithSystemProperties_Success() {
        System.setProperty("DB_USERNAME", "overriddenUser");
        System.setProperty("DB_PASSWORD", "overriddenPass");

        assertThat(databaseConfig.getDbUsername()).isEqualTo("overriddenUser");
        assertThat(databaseConfig.getDbPassword()).isEqualTo("overriddenPass");
    }

    @Test
    @DisplayName("Load and validate properties - Missing .env file throws exception")
    void testLoadAndValidateProperties_MissingEnvFile_ThrowsException() {
        System.setProperty("ENV_PATH", "nonexistent.env");
        assertThatThrownBy(databaseConfig::init)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to find or read .env file");
    }
}