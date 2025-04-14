package com.demo.finance.domain.utils;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class EnvLoaderTest {

    @Test
    @DisplayName("Load .env file - valid format - returns key-value map")
    void testLoadEnv_validFile_returnsKeyValueMap(@TempDir Path tempDir) throws IOException {
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
    void testLoadEnv_malformedLine_throwsRuntimeException(@TempDir Path tempDir) throws IOException {
        File envFile = tempDir.resolve(".env").toFile();
        try (FileWriter writer = new FileWriter(envFile)) {
            writer.write("DB_URL\n");  // Malformed line (missing '=')
        }

        assertThatThrownBy(() -> EnvLoader.loadEnv(envFile.getAbsolutePath()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Malformed line in .env file");
    }

    @Test
    @DisplayName("Load .env file - duplicate keys - throws RuntimeException")
    void testLoadEnv_duplicateKeys_throwsRuntimeException(@TempDir Path tempDir) throws IOException {
        File envFile = tempDir.resolve(".env").toFile();
        try (FileWriter writer = new FileWriter(envFile)) {
            writer.write("DB_URL=jdbc:postgresql://localhost:5432/testdb\n");
            writer.write("DB_URL=jdbc:mysql://localhost:3306/testdb\n");  // Duplicate key
        }

        assertThatThrownBy(() -> EnvLoader.loadEnv(envFile.getAbsolutePath()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Duplicate key found in .env file");
    }

    @Test
    @DisplayName("Load .env file - skips comments and empty lines")
    void testLoadEnv_skipsCommentsAndEmptyLines(@TempDir Path tempDir) throws IOException {
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
    }
}