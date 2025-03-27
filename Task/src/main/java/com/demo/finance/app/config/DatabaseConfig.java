package com.demo.finance.app.config;

import com.demo.finance.domain.utils.SystemPropLoader;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.logging.Logger;

/**
 * A configuration class responsible for managing database connection details.
 * This class ensures that required database properties (DB_URL, DB_USERNAME, DB_PASSWORD)
 * are loaded from a .env file, validated, and set as system properties. It follows the
 * singleton pattern to provide a single instance of the configuration throughout the application.
 */
@Configuration
public class DatabaseConfig {

    private static final Logger log = Logger.getLogger(DatabaseConfig.class.getName());
    private static final String DB_URL = "DB_URL";
    private static final String DB_USERNAME = "DB_USERNAME";
    private static final String DB_PASSWORD = "DB_PASSWORD";
    private static final String DEFAULT_ENV_FILE = "/app/.env";
    private static final DatabaseConfig INSTANCE = new DatabaseConfig();

    /**
     * Private constructor to enforce the singleton pattern.
     * Loads and validates required database properties during initialization.
     */
    private DatabaseConfig() {
        loadAndValidateProperties();
    }

    /**
     * Retrieves the singleton instance of the `DatabaseConfig` class.
     *
     * @return The singleton instance of `DatabaseConfig`.
     */
    public static DatabaseConfig getInstance() {
        return INSTANCE;
    }

    /**
     * Retrieves the database URL from the system properties.
     * Validates that the property is present and non-empty before returning it.
     *
     * @return The database URL as a string.
     * @throws RuntimeException If the DB_URL property is missing or empty in the system properties.
     */
    public String getDbUrl() {
        validateProperty(DB_URL);
        return System.getProperty(DB_URL);
    }

    /**
     * Retrieves the database username from the system properties.
     * Validates that the property is present and non-empty before returning it.
     *
     * @return The database username as a string.
     * @throws RuntimeException If the DB_USERNAME property is missing or empty in the system properties.
     */
    public String getDbUsername() {
        validateProperty(DB_USERNAME);
        return System.getProperty(DB_USERNAME);
    }

    /**
     * Retrieves the database password from the system properties.
     * Validates that the property is present and non-empty before returning it.
     *
     * @return The database password as a string.
     * @throws RuntimeException If the DB_PASSWORD property is missing or empty in the system properties.
     */
    public String getDbPassword() {
        validateProperty(DB_PASSWORD);
        return System.getProperty(DB_PASSWORD);
    }

    /**
     * Loads environment variables from a .env file, validates them, and sets them as system properties.
     * Ensures that all required properties (DB_URL, DB_USERNAME, DB_PASSWORD) are present and valid.
     */
    private void loadAndValidateProperties() {
        String envFilePath = System.getProperty("ENV_PATH", DEFAULT_ENV_FILE);
        Set<String> requiredProperties = Set.of(DB_URL, DB_USERNAME, DB_PASSWORD);

        SystemPropLoader.loadAndSetProperties(envFilePath, requiredProperties);

        validateProperty(DB_URL);
        validateProperty(DB_USERNAME);
        validateProperty(DB_PASSWORD);
    }

    /**
     * Validates that a given property is present and non-empty in the system properties.
     *
     * @param propertyKey The key of the property to validate.
     * @throws RuntimeException If the property is missing or empty in the system properties.
     */
    private void validateProperty(String propertyKey) {
        String propertyValue = System.getProperty(propertyKey);
        if (propertyValue == null || propertyValue.isEmpty()) {
            log.severe(propertyKey + " is missing or empty in the system properties.");
            throw new RuntimeException(propertyKey + " is not configured.");
        }
    }
}