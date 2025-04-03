package com.demo.finance.domain.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;
import java.util.Set;

/**
 * A utility class for loading, validating, and setting application-wide properties
 * from both a .env file and a YAML configuration file into JVM system properties.
 * This class ensures that all required properties are present and valid before
 * setting them as system properties.
 */
@Component
@Slf4j
public class SystemPropLoader {

    /**
     * Loads environment variables from a .env file and YAML file, validates them against a set of
     * required properties, and sets them as JVM system properties if they are not already set.
     *
     * @param envFilePath   The path to the .env file containing sensitive key-value pairs (e.g., credentials).
     * @param ymlFilePath   The path to the YAML file containing non-sensitive configuration settings.
     * @param envProperties A set of required property keys that must be present and non-empty in the .env file.
     */
    public static void loadAndSetProperties(String envFilePath, String ymlFilePath, Set<String> envProperties,
                                            Set<String> ymlProperties) {
        processProperties("env", envFilePath, envProperties, EnvLoader::loadEnv);
        processProperties("yml", ymlFilePath, ymlProperties, YmlLoader::loadYml);

        log.info("All properties from .env and YAML have been successfully loaded, validated, and set.");
    }

    /**
     * Helper method to load, validate, and set properties from a given source.
     *
     * @param sourceName         Name of the source (e.g., "environment", "YAML") for logging.
     * @param filePath           Path to the file to be loaded.
     * @param requiredProperties Set of required properties for validation (can be empty if validation is not needed).
     * @param loaderFunction     Function that loads the properties from the file.
     */
    private static void processProperties(String sourceName, String filePath, Set<String> requiredProperties,
                                          Function<String, Map<String, String>> loaderFunction) {
        log.info("Loading properties from " + sourceName + " file: " + filePath);
        Map<String, String> properties = loaderFunction.apply(filePath);
        if (properties.containsKey("app.db.url")) {
            properties.put("DB_URL", properties.remove("app.db.url"));
        }
        if (!requiredProperties.isEmpty()) {
            validateProperties(properties, requiredProperties, sourceName);
        }
        setSystemProperties(properties);
    }

    /**
     * Validates that all required properties are present and non-empty in the provided
     * environment variables map. If any required property is missing or empty, an exception
     * is thrown with a descriptive error message.
     *
     * @param envVars            A map of environment variables loaded from the .env file.
     * @param requiredProperties A set of property keys that must be validated.
     * @throws RuntimeException If any required property is missing or empty in the envVars map.
     */
    private static void validateProperties(
            Map<String, String> envVars, Set<String> requiredProperties, String sourceName) {
        for (String key : requiredProperties) {
            String value = envVars.get(key);
            if (value == null || value.trim().isEmpty()) {
                log.error("Validation failed: Required property '{}' is missing or empty in the {} file.",
                        key, sourceName);
                throw new RuntimeException("Validation failed: Required property '" + key
                        + "' is missing or empty.");
            }
        }
        log.info("All required properties passed validation.");
    }

    /**
     * Sets the provided properties as JVM system properties, but only if they
     * are not already set. Logs a message for each property that is set or skipped.
     *
     * @param properties A map of properties to be set as JVM system properties.
     */
    private static void setSystemProperties(Map<String, String> properties) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (System.getProperty(key) == null) {
                System.setProperty(key, value);
                log.debug("Set JVM property: {}={}", key, value);
            } else {
                log.debug("Skipped setting JVM property (already set): {}", key);
            }
        }
    }
}