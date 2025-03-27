package com.demo.finance.domain.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class for loading properties from a YAML file.
 * This class reads key-value pairs from the specified file and provides
 * them as a map for use in the application. It ensures that the file is
 * properly formatted and handles errors such as missing files, malformed data,
 * or duplicate keys.
 */
@Component
public class YmlLoader {

    private static final Logger log = Logger.getLogger(YmlLoader.class.getName());

    /**
     * Loads properties from a YAML file.
     *
     * @param filePath Path to the YAML file (e.g., "application.yml", "config/app.yml")
     * @return A map of key-value pairs from the YAML file
     * @throws RuntimeException If the file is missing, malformed, or contains duplicate keys
     */
    public static Map<String, String> loadYml(String filePath) {
        Map<String, String> properties = new HashMap<>();
        try {
            log.info("Loading properties from " + filePath);

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Map<String, Object> yamlData = mapper.readValue(
                    Files.newInputStream(Paths.get(filePath)), new TypeReference<>() {}
            );

            flattenYaml("", yamlData, properties);
            log.info("Successfully loaded " + properties.size() + " properties from " + filePath);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error loading YAML file: " + filePath, e);
            throw new RuntimeException("Unable to find or read YAML file: " + filePath, e);
        }

        return properties;
    }

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
                    log.severe("Duplicate key found in YAML file: " + fullKey);
                    throw new RuntimeException("Duplicate key found in YAML file: " + fullKey);
                }

                if (value == null || value.toString().trim().isEmpty()) {
                    log.severe("Malformed key-value pair in YAML file: " + fullKey + " -> " + value);
                    throw new RuntimeException("Malformed key-value pair in YAML file: " + fullKey);
                }

                properties.put(fullKey, value.toString());
                log.fine("Loaded property: " + fullKey + " -> " + value);
            }
        }
    }
}