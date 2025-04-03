package com.demo.finance.domain.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for loading properties from a YAML file.
 * This class reads key-value pairs from the specified file and provides
 * them as a map for use in the application. It ensures that the file is
 * properly formatted and handles errors such as missing files, malformed data,
 * or duplicate keys.
 */
@Component
@Slf4j
public class YmlLoader {

    /**
     * Loads properties from a YAML file and returns them as a flat map of key-value pairs.
     * <p>
     * This method reads the YAML file at the specified path, parses its contents, and flattens
     * nested structures into a single-level map. For example, a nested structure like
     * {@code server.port: 8080} will be flattened to {@code "server.port": "8080"}.
     * <p>
     * If the file is missing, malformed, or contains duplicate keys, this method logs an error
     * and throws a runtime exception with a descriptive message.
     *
     * @param filePath Path to the YAML file (e.g., "application.yml", "config/app.yml")
     * @return A map of key-value pairs from the YAML file
     * @throws RuntimeException If the file is missing, malformed, or contains duplicate keys
     */
    public static Map<String, String> loadYml(String filePath) {
        Map<String, String> properties = new HashMap<>();
        try {
            log.info("Loading properties from {}", filePath);

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Map<String, Object> yamlData = mapper.readValue(
                    Files.newInputStream(Paths.get(filePath)), new TypeReference<>() {}
            );

            flattenYaml("", yamlData, properties);
            log.info("Successfully loaded {} properties from {}", properties.size(), filePath);
        } catch (IOException e) {
            log.error("Error loading YAML file: {}", filePath, e);
            throw new RuntimeException("Unable to find or read YAML file: " + filePath, e);
        }

        return properties;
    }

    /**
     * Recursively flattens a nested YAML structure into a flat map of key-value pairs.
     * <p>
     * This method processes each key-value pair in the provided map. If a value is itself a map,
     * the method recursively processes it, appending the current key to the parent key with a dot (.)
     * separator. If a value is not a map, it is added to the properties map after validation.
     * <p>
     * The method performs the following validations:
     * <ul>
     *   <li>Skips keys that start with '#' or are empty, as they are considered comments or invalid.</li>
     *   <li>Throws an exception if a duplicate key is found in the YAML file.</li>
     *   <li>Throws an exception if a value is null or empty, indicating malformed data.</li>
     * </ul>
     *
     * @param parentKey The parent key prefix for nested keys (empty for top-level keys)
     * @param map       The current map being processed (may contain nested maps)
     * @param properties The flat map of key-value pairs being populated
     * @throws RuntimeException If duplicate keys or malformed key-value pairs are detected
     */
    private static void flattenYaml(String parentKey, Map<String, Object> map, Map<String, String> properties) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey().trim();

            if (key.startsWith("#") || key.isEmpty()) {
                continue;
            }

            String fullKey = parentKey.isEmpty() ? key : parentKey + "." + key;

            Object value = entry.getValue();
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                flattenYaml(fullKey, nestedMap, properties);
            } else {
                if (properties.containsKey(fullKey)) {
                    log.error("Duplicate key found in YAML file: {}", fullKey);
                    throw new RuntimeException("Duplicate key found in YAML file: " + fullKey);
                }

                if (value == null || value.toString().trim().isEmpty()) {
                    log.error("Malformed key-value pair in YAML file: {} -> {}", fullKey, value);
                    throw new RuntimeException("Malformed key-value pair in YAML file: " + fullKey);
                }

                properties.put(fullKey, value.toString());
                log.debug("Loaded property: {} -> {}", fullKey, value);
            }
        }
    }
}