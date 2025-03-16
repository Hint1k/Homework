package com.demo.finance.app.config;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Configuration class for managing database connection details.
 * This class uses environment variables (loaded from a `.env` file) and system properties
 * to retrieve database URL, username, and password. It ensures that required database
 * configurations are present and overrides them with system properties if they exist.
 */
public class DatabaseConfig {

    private static final Logger log = Logger.getLogger(DatabaseConfig.class.getName());
    private final Map<String, String> envVars;
    private static final String DEFAULT_ENV = ".env";
    private static final String DB_URL = "DB_URL";
    private static final String DB_USERNAME = "DB_USERNAME";
    private static final String DB_PASSWORD = "DB_PASSWORD";
    private static final DatabaseConfig INSTANCE = new DatabaseConfig();

    /**
     * Private constructor to enforce the singleton pattern.
     * Loads environment variables from the `.env` file and overrides them with system properties if available.
     */
    private DatabaseConfig() {
        String envFilePath = System.getProperty("ENV_PATH", DEFAULT_ENV);
        envVars = EnvLoader.loadEnv(envFilePath);
        overrideWithSystemProperties();
    }

    /**
     * Retrieves the singleton instance of the `DatabaseConfig` class.
     *
     * @return the singleton instance of `DatabaseConfig`
     */
    public static DatabaseConfig getInstance() {
        return INSTANCE;
    }

    /**
     * Retrieves the database URL from the environment variables.
     * If the URL is missing or empty, logs an error and throws a `RuntimeException`.
     *
     * @return the database URL
     * @throws RuntimeException if the database URL is not configured in the `.env` file
     */
    public String getDbUrl() {
        String dbUrl = envVars.get(DB_URL);
        if (dbUrl == null || dbUrl.isEmpty()) {
            log.severe(DB_URL + " is missing or empty in the .env file.");
            throw new RuntimeException(DB_URL + " is not configured in the .env file.");
        }
        return dbUrl;
    }

    /**
     * Retrieves the database username from the environment variables.
     * If the username is missing or empty, logs an error and throws a `RuntimeException`.
     *
     * @return the database username
     * @throws RuntimeException if the database username is not configured in the `.env` file
     */
    public String getDbUsername() {
        String dbUsername = envVars.get(DB_USERNAME);
        if (dbUsername == null || dbUsername.isEmpty()) {
            log.severe(DB_USERNAME + " is missing or empty in the .env file.");
            throw new RuntimeException(DB_USERNAME + " is not configured in the .env file.");
        }
        return dbUsername;
    }

    /**
     * Retrieves the database password from the environment variables.
     * If the password is missing or empty, logs an error and throws a `RuntimeException`.
     *
     * @return the database password
     * @throws RuntimeException if the database password is not configured in the `.env` file
     */
    public String getDbPassword() {
        String dbPassword = envVars.get(DB_PASSWORD);
        if (dbPassword == null || dbPassword.isEmpty()) {
            log.severe(DB_PASSWORD + " is missing or empty in the .env file.");
            throw new RuntimeException(DB_PASSWORD + " is not configured in the .env file.");
        }
        return dbPassword;
    }

    /**
     * Overrides the database connection details (URL, username, password) with system properties
     * if they are set. Logs a message indicating that overrides have been applied.
     */
    private void overrideWithSystemProperties() {
        String dbUrl = System.getProperty(DB_URL, envVars.get(DB_URL));
        String dbUsername = System.getProperty(DB_USERNAME, envVars.get(DB_USERNAME));
        String dbPassword = System.getProperty(DB_PASSWORD, envVars.get(DB_PASSWORD));

        envVars.put(DB_URL, dbUrl);
        envVars.put(DB_USERNAME, dbUsername);
        envVars.put(DB_PASSWORD, dbPassword);

        log.info("Database connection details overridden with system properties if they exist.");
    }
}