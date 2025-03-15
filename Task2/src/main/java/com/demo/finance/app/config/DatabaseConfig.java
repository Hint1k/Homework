package com.demo.finance.app.config;

import java.util.Map;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConfig {

    private static final Logger log = Logger.getLogger(DatabaseConfig.class.getName());
    private final Map<String, String> envVars;
    private static final String DEFAULT_ENV = "/app/.env";
    private static final String DB_URL = "DB_URL";
    private static final String DB_USERNAME = "DB_USERNAME";
    private static final String DB_PASSWORD = "DB_PASSWORD";
    private static final DatabaseConfig INSTANCE = new DatabaseConfig();

    private DatabaseConfig() {
        String envFilePath = System.getProperty("ENV_PATH", DEFAULT_ENV);
        File envFile = new File(envFilePath);
         if (!envFile.exists()) {
            log.severe("The .env file was not found at path: " + envFilePath);
            throw new RuntimeException("The .env file is missing. Please ensure it is located at " + envFilePath);
        }

        try {
            log.info("Loading environment variables from .env file at path: " + envFilePath);
            envVars = EnvLoader.loadEnv(envFilePath);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to load environment variables from .env file: " + e.getMessage(), e);
            throw new RuntimeException("Failed to load environment variables from .env file.", e);
        }

        overrideWithSystemProperties();
    }

    public static DatabaseConfig getInstance() {
        return INSTANCE;
    }

    public String getDbUrl() {
        String dbUrl = envVars.get(DB_URL);
        if (dbUrl == null || dbUrl.isEmpty()) {
            log.severe(DB_URL + " is missing or empty in the .env file.");
            throw new RuntimeException(DB_URL + " is not configured in the .env file.");
        }
        return dbUrl;
    }

    public String getDbUsername() {
        String dbUsername = envVars.get(DB_USERNAME);
        if (dbUsername == null || dbUsername.isEmpty()) {
            log.severe(DB_USERNAME + " is missing or empty in the .env file.");
            throw new RuntimeException(DB_USERNAME + " is not configured in the .env file.");
        }
        return dbUsername;
    }

    public String getDbPassword() {
        String dbPassword = envVars.get(DB_PASSWORD);
        if (dbPassword == null || dbPassword.isEmpty()) {
            log.severe(DB_PASSWORD + " is missing or empty in the .env file.");
            throw new RuntimeException(DB_PASSWORD + " is not configured in the .env file.");
        }
        return dbPassword;
    }

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