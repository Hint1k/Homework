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
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class YmlLoaderTest {

    private static final Logger log = Logger.getLogger(YmlLoaderTest.class.getName());

    @Test
    @DisplayName("Load YAML file - valid format - returns key-value map")
    void testLoadYml_validFile_returnsKeyValueMap(@TempDir Path tempDir) {
        try {
            File ymlFile = tempDir.resolve("application.yml").toFile();
            try (FileWriter writer = new FileWriter(ymlFile)) {
                writer.write("app:\n");
                writer.write("  db:\n");
                writer.write("    url: jdbc:postgresql://localhost:5432/testdb\n");
                writer.write("    username: testuser\n");
                writer.write("    password: testpass\n");
            }

            Map<String, String> ymlVars = YmlLoader.loadYml(ymlFile.getAbsolutePath());

            assertThat(ymlVars)
                    .containsEntry("app.db.url", "jdbc:postgresql://localhost:5432/testdb")
                    .containsEntry("app.db.username", "testuser")
                    .containsEntry("app.db.password", "testpass")
                    .hasSize(3);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to load valid YAML file: " + e.getMessage(), e);
            fail("Exception occurred while loading valid YAML file: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Load YAML file - file not found - throws RuntimeException")
    void testLoadYml_fileNotFound_throwsRuntimeException() {
        String nonExistentFile = "non_existent.yml";

        assertThatThrownBy(() -> YmlLoader.loadYml(nonExistentFile))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to find or read YAML file");
    }

    @Test
    @DisplayName("Load YAML file - empty values - throws RuntimeException")
    void testLoadYml_emptyValues_throwsRuntimeException(@TempDir Path tempDir) {
        try {
            File ymlFile = tempDir.resolve("empty.yml").toFile();
            try (FileWriter writer = new FileWriter(ymlFile)) {
                writer.write("app:\n");
                writer.write("  db:\n");
                writer.write("    url: \n");  // Empty value
            }

            assertThatThrownBy(() -> YmlLoader.loadYml(ymlFile.getAbsolutePath()))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Malformed key-value pair in YAML file");
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to create YAML file with empty values: " + e.getMessage(), e);
            fail("Exception occurred while creating YAML file with empty values: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Load YAML file - nested structure - flattens keys correctly")
    void testLoadYml_nestedStructure_flattensKeys(@TempDir Path tempDir) {
        try {
            File ymlFile = tempDir.resolve("nested.yml").toFile();
            try (FileWriter writer = new FileWriter(ymlFile)) {
                writer.write("server:\n");
                writer.write("  port: 8080\n");
                writer.write("  ssl:\n");
                writer.write("    enabled: true\n");
                writer.write("    key-store: keystore.p12\n");
            }

            Map<String, String> ymlVars = YmlLoader.loadYml(ymlFile.getAbsolutePath());

            assertThat(ymlVars)
                    .containsEntry("server.port", "8080")
                    .containsEntry("server.ssl.enabled", "true")
                    .containsEntry("server.ssl.key-store", "keystore.p12")
                    .hasSize(3);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to create nested YAML file: " + e.getMessage(), e);
            fail("Exception occurred while creating nested YAML file: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Load YAML file - skips comments")
    void testLoadYml_skipsComments(@TempDir Path tempDir) {
        try {
            File ymlFile = tempDir.resolve("comments.yml").toFile();
            try (FileWriter writer = new FileWriter(ymlFile)) {
                writer.write("# Main configuration\n");
                writer.write("app:\n");
                writer.write("  # Database settings\n");
                writer.write("  db:\n");
                writer.write("    url: jdbc:postgresql://localhost:5432/testdb\n");
            }

            Map<String, String> ymlVars = YmlLoader.loadYml(ymlFile.getAbsolutePath());

            assertThat(ymlVars)
                    .containsEntry("app.db.url", "jdbc:postgresql://localhost:5432/testdb")
                    .hasSize(1);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to create YAML file with comments: " + e.getMessage(), e);
            fail("Exception occurred while creating YAML file with comments: " + e.getMessage());
        }
    }
}