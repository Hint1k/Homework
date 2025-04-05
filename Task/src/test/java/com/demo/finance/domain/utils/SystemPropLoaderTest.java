package com.demo.finance.domain.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class SystemPropLoaderTest {

    private static final Logger log = Logger.getLogger(SystemPropLoaderTest.class.getName());

    @AfterEach
    void resetSystemProperties() {
        System.clearProperty("DB_URL");
        System.clearProperty("DB_USERNAME");
        System.clearProperty("DB_PASSWORD");
        System.clearProperty("app.db.url");
    }

    @Test
    @DisplayName("Load and set properties - valid .env file - sets system properties")
    void testLoadAndSetProperties_validFile_setsSystemProperties(@TempDir Path tempDir) {
        try {
            File envFile = tempDir.resolve(".env").toFile();
            try (FileWriter writer = new FileWriter(envFile)) {
                writer.write("DB_USERNAME=testuser\n");
                writer.write("DB_PASSWORD=testpass\n");
            }

            Set<String> requiredProperties = Set.of("DB_USERNAME", "DB_PASSWORD");
            SystemPropLoader.loadAndSetProperties(envFile.getAbsolutePath(), requiredProperties);

            assertThat(System.getProperty("DB_USERNAME")).isEqualTo("testuser");
            assertThat(System.getProperty("DB_PASSWORD")).isEqualTo("testpass");

        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to create test files: " + e.getMessage(), e);
            fail("Exception occurred while creating test files: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Load and set properties - missing required property - throws RuntimeException")
    void testLoadAndSetProperties_missingRequiredProperty_throwsRuntimeException(@TempDir Path tempDir) {
        try {
            File envFile = tempDir.resolve(".env").toFile();
            try (FileWriter writer = new FileWriter(envFile)) {
                writer.write("DB_USERNAME=testuser\n");
                // Missing DB_PASSWORD
                writer.write("app.db.url=jdbc:postgresql://localhost:5432/testdb\n");
            }

            Set<String> requiredProperties = Set.of("DB_USERNAME", "DB_PASSWORD", "app.db.url");

            assertThatThrownBy(() -> SystemPropLoader.loadAndSetProperties(
                    envFile.getAbsolutePath(),
                    requiredProperties
            ))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(
                            "Validation failed: Required property 'DB_PASSWORD' is missing or empty.");

        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to create test files: " + e.getMessage(), e);
            fail("Exception occurred while creating test files: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Load and set properties - skips setting already existing system property")
    void testLoadAndSetProperties_skipsAlreadyExistingSystemProperty(@TempDir Path tempDir) {
        try {
            File envFile = tempDir.resolve(".env").toFile();
            try (FileWriter writer = new FileWriter(envFile)) {
                writer.write("DB_USERNAME=testuser\n");
                writer.write("DB_PASSWORD=testpass\n");
                writer.write("app.db.url=jdbc:postgresql://localhost:5432/testdb\n");
            }

            System.setProperty("DB_URL", "jdbc:mysql://localhost:3306/testdb");
            Set<String> requiredProperties = Set.of("DB_USERNAME", "DB_PASSWORD");
            SystemPropLoader.loadAndSetProperties(envFile.getAbsolutePath(), requiredProperties);

            assertThat(System.getProperty("DB_URL")).isEqualTo("jdbc:mysql://localhost:3306/testdb");
            assertThat(System.getProperty("DB_USERNAME")).isEqualTo("testuser");
            assertThat(System.getProperty("DB_PASSWORD")).isEqualTo("testpass");

        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to create test files: " + e.getMessage(), e);
            fail("Exception occurred while creating test files: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Load and set properties - file not found - throws RuntimeException")
    void testLoadAndSetProperties_fileNotFound_throwsRuntimeException() {
        String nonExistentEnvFile = "non_existent.env";
        Set<String> requiredProperties = Set.of("DB_USERNAME", "DB_PASSWORD");

        assertThatThrownBy(() -> SystemPropLoader.loadAndSetProperties(
                nonExistentEnvFile,
                requiredProperties
        ))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to find or read .env file");
    }
}