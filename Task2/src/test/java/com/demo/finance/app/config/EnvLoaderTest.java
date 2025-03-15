package com.demo.finance.app.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class EnvLoaderTest {

    private static final Logger log = Logger.getLogger(EnvLoaderTest.class.getName());

    @Test
    @DisplayName("Load .env file - valid format - returns key-value map")
    void testLoadEnv_validFile_returnsKeyValueMap(@TempDir Path tempDir) {
        try {
            File envFile = tempDir.resolve(".env").toFile();
            try (FileWriter writer = new FileWriter(envFile)) {
                writer.write("DB_URL=jdbc:postgresql://localhost:5432/testdb\n");
                writer.write("DB_USERNAME=testuser\n");
                writer.write("DB_PASSWORD=testpass\n");
            }

            Map<String, String> envVars = EnvLoader.loadEnv(envFile.getAbsolutePath());

            assertThat(envVars).containsEntry("DB_URL", "jdbc:postgresql://localhost:5432/testdb")
                    .containsEntry("DB_USERNAME", "testuser")
                    .containsEntry("DB_PASSWORD", "testpass")
                    .hasSize(3);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to load valid .env file: " + e.getMessage() + e);
            fail("Exception occurred while loading valid .env file: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Load .env file - file not found - throws RuntimeException")
    void testLoadEnv_fileNotFound_throwsRuntimeException() {
        String nonExistentFile = "non_existent.env";

        assertThatThrownBy(() -> EnvLoader.loadEnv(nonExistentFile))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to find or read .env file");
    }

    @Test
    @DisplayName("Load .env file - malformed line - throws RuntimeException")
    void testLoadEnv_malformedLine_throwsRuntimeException(@TempDir Path tempDir) {
        try {
            File envFile = tempDir.resolve(".env").toFile();
            try (FileWriter writer = new FileWriter(envFile)) {
                writer.write("DB_URL\n");  // Malformed line (missing '=')
            }

            assertThatThrownBy(() -> EnvLoader.loadEnv(envFile.getAbsolutePath()))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Malformed line in .env file");
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to create .env file with malformed line: " + e.getMessage() + e);
            fail("Exception occurred while creating .env file with malformed line: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Load .env file - duplicate keys - throws RuntimeException")
    void testLoadEnv_duplicateKeys_throwsRuntimeException(@TempDir Path tempDir) {
        try {
            File envFile = tempDir.resolve(".env").toFile();
            try (FileWriter writer = new FileWriter(envFile)) {
                writer.write("DB_URL=jdbc:postgresql://localhost:5432/testdb\n");
                writer.write("DB_URL=jdbc:mysql://localhost:3306/testdb\n");  // Duplicate key
            }

            assertThatThrownBy(() -> EnvLoader.loadEnv(envFile.getAbsolutePath()))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Duplicate key found in .env file");
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to create .env file with duplicate keys: " + e.getMessage() + e);
            fail("Exception occurred while creating .env file with duplicate keys: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Load .env file - skips comments and empty lines")
    void testLoadEnv_skipsCommentsAndEmptyLines(@TempDir Path tempDir) {
        try {
            File envFile = tempDir.resolve(".env").toFile();
            try (FileWriter writer = new FileWriter(envFile)) {
                writer.write("# This is a comment\n");
                writer.write("\n");
                writer.write("DB_URL=jdbc:postgresql://localhost:5432/testdb\n");
                writer.write("# Another comment\n");
                writer.write("DB_USERNAME=testuser\n");
            }

            Map<String, String> envVars = EnvLoader.loadEnv(envFile.getAbsolutePath());

            assertThat(envVars).containsEntry("DB_URL", "jdbc:postgresql://localhost:5432/testdb")
                    .containsEntry("DB_USERNAME", "testuser")
                    .hasSize(2);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to create .env file with comments and empty lines: "
                    + e.getMessage() + e);
            fail("Exception occurred while creating .env file with comments and empty lines: "
                    + e.getMessage());
        }
    }
}