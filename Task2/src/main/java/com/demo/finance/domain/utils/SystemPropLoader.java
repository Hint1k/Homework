package com.demo.finance.domain.utils;

import java.util.Map;
import java.util.logging.Logger;
import java.util.Set;

/**
 * A utility class for loading, validating, and setting application-wide properties
 * from a .env file into JVM system properties. This class ensures that all required
 * properties are present and valid before setting them as system properties.
 */
public class SystemPropLoader {

    private static final Logger log = Logger.getLogger(SystemPropLoader.class.getName());

    /**
     * Loads environment variables from a .env file, validates them against a set of
     * required properties, and sets them as JVM system properties if they are not already set.
     *
     * @param envFilePath       The path to the .env file containing key-value pairs of properties.
     * @param requiredProperties A set of property keys that must be present and non-empty in the .env file.
     * @throws RuntimeException If any required property is missing or empty in the .env file.
     */
    public static void loadAndSetProperties(String envFilePath, Set<String> requiredProperties) {
        log.info("Loading and validating properties from .env file: " + envFilePath);
        Map<String, String> envVars = EnvLoader.loadEnv(envFilePath);

        validateProperties(envVars, requiredProperties);
        setSystemProperties(envVars);

        log.info("All properties have been successfully loaded, validated, and set.");
    }

    /**
     * Validates that all required properties are present and non-empty in the provided
     * environment variables map. If any required property is missing or empty, an exception
     * is thrown with a descriptive error message.
     *
     * @param envVars           A map of environment variables loaded from the .env file.
     * @param requiredProperties A set of property keys that must be validated.
     * @throws RuntimeException If any required property is missing or empty in the envVars map.
     */
    private static void validateProperties(Map<String, String> envVars, Set<String> requiredProperties) {
        for (String key : requiredProperties) {
            String value = envVars.get(key);
            if (value == null || value.trim().isEmpty()) {
                log.severe("Validation failed: Required property '" + key
                        + "' is missing or empty in the .env file.");
                throw new RuntimeException("Validation failed: Required property '" + key
                        + "' is missing or empty.");
            }
        }
        log.info("All required properties passed validation.");
    }

    /**
     * Sets the provided environment variables as JVM system properties, but only if they
     * are not already set. Logs a message for each property that is set or skipped.
     *
     * @param envVars A map of environment variables to be set as JVM system properties.
     */
    private static void setSystemProperties(Map<String, String> envVars) {
        for (Map.Entry<String, String> entry : envVars.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (System.getProperty(key) == null) {
                System.setProperty(key, value);
                log.fine("Set JVM property: " + key + "=" + value);
            } else {
                log.fine("Skipped setting JVM property (already set): " + key);
            }
        }
    }
}