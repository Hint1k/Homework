package com.demo.finance.app.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.anyString;

@ExtendWith(MockitoExtension.class)
class DatabaseConfigTest {

    private DatabaseConfig databaseConfig;
    private Map<String, String> envVars;

    @BeforeEach
    void setUp() throws Exception {
        System.setProperty("ENV_PATH", "src/test/resources/.env");

        envVars = new HashMap<>();
        envVars.put("DB_URL", "jdbc:postgresql://localhost:5432/testdb");
        envVars.put("DB_USERNAME", "testuser");
        envVars.put("DB_PASSWORD", "testpass");

        try (MockedStatic<EnvLoader> mockedEnvLoader = Mockito.mockStatic(EnvLoader.class)) {
            mockedEnvLoader.when(() -> EnvLoader.loadEnv(anyString())).thenReturn(envVars);

            databaseConfig = DatabaseConfig.getInstance();
        }
        resetSingletonInstance();
    }

    @Test
    @DisplayName("Get DB URL - Success scenario")
    void testGetDbUrl_Success() {
        String dbUrl = databaseConfig.getDbUrl();
        assertThat(dbUrl).isEqualTo("jdbc:postgresql://localhost:5432/testdb");
    }

    @Test
    @DisplayName("Get DB URL - Missing URL throws exception")
    void testGetDbUrl_MissingUrl_ThrowsException() throws Exception {
        envVars.remove("DB_URL");
        resetSingletonInstance();

        assertThatThrownBy(() -> databaseConfig.getDbUrl())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB_URL is not configured in the .env file.");
    }

    @Test
    @DisplayName("Get DB Username - Success scenario")
    void testGetDbUsername_Success() {
        String dbUsername = databaseConfig.getDbUsername();
        assertThat(dbUsername).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Get DB Username - Missing username throws exception")
    void testGetDbUsername_MissingUsername_ThrowsException() throws Exception {
        envVars.remove("DB_USERNAME");
        resetSingletonInstance();

        assertThatThrownBy(() -> databaseConfig.getDbUsername())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB_USERNAME is not configured in the .env file.");
    }

    @Test
    @DisplayName("Get DB Password - Success scenario")
    void testGetDbPassword_Success() {
        String dbPassword = databaseConfig.getDbPassword();
        assertThat(dbPassword).isEqualTo("testpass");
    }

    @Test
    @DisplayName("Get DB Password - Missing password throws exception")
    void testGetDbPassword_MissingPassword_ThrowsException() throws Exception {
        envVars.remove("DB_PASSWORD");
        resetSingletonInstance();

        assertThatThrownBy(() -> databaseConfig.getDbPassword())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB_PASSWORD is not configured in the .env file.");
    }

    @Test
    @DisplayName("Override with system properties - Success scenario")
    void testOverrideWithSystemProperties_Success() throws Exception {
        System.setProperty("DB_URL", "jdbc:postgresql://localhost:5432/overridden");
        System.setProperty("DB_USERNAME", "overriddenUser");
        System.setProperty("DB_PASSWORD", "overriddenPass");

        callOverrideWithSystemProperties(databaseConfig);

        assertThat(databaseConfig.getDbUrl()).isEqualTo("jdbc:postgresql://localhost:5432/overridden");
        assertThat(databaseConfig.getDbUsername()).isEqualTo("overriddenUser");
        assertThat(databaseConfig.getDbPassword()).isEqualTo("overriddenPass");

        System.clearProperty("DB_URL");
        System.clearProperty("DB_USERNAME");
        System.clearProperty("DB_PASSWORD");
    }

    @Test
    @DisplayName("Override with system properties - Missing system properties uses .env values")
    void testOverrideWithSystemProperties_MissingSystemProperties_UsesEnvValues() throws Exception {
        System.clearProperty("DB_URL");
        System.clearProperty("DB_USERNAME");
        System.clearProperty("DB_PASSWORD");

        resetSingletonInstance();
        databaseConfig = DatabaseConfig.getInstance();

        assertThat(databaseConfig.getDbUrl()).isEqualTo("jdbc:postgresql://localhost:5432/testdb");
        assertThat(databaseConfig.getDbUsername()).isEqualTo("testuser");
        assertThat(databaseConfig.getDbPassword()).isEqualTo("testpass");
    }

    private void resetSingletonInstance() throws Exception {
        Field envVarsField = DatabaseConfig.class.getDeclaredField("envVars");
        envVarsField.setAccessible(true);
        envVarsField.set(databaseConfig, new HashMap<>(envVars));
    }

    private void callOverrideWithSystemProperties(DatabaseConfig databaseConfig) throws Exception {
        Method overrideMethod = DatabaseConfig.class.getDeclaredMethod("overrideWithSystemProperties");
        overrideMethod.setAccessible(true);
        overrideMethod.invoke(databaseConfig);
    }
}