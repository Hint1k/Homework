package com.demo.finance.app.config;

import java.util.Map;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConfig {

    private static final Logger log = Logger.getLogger(DatabaseConfig.class.getName());
    private final Map<String, String> envVars;
    private static final String ENV = "/app/.env";
    private static final String DB_URL = "DB_URL";
    private static final String DB_USERNAME = "DB_USERNAME";
    private static final String DB_PASSWORD = "DB_PASSWORD";
    private static final DatabaseConfig INSTANCE = new DatabaseConfig();

    private DatabaseConfig() {
        File envFile = new File(ENV);
        if (!envFile.exists()) {
            log.severe("The .env file was not found at path: " + ENV);
            throw new RuntimeException("The .env file is missing. Please ensure it is located at " + ENV);
        }

        try {
            log.info("Loading environment variables from .env file at path: " + ENV);
            envVars = EnvLoader.loadEnv(ENV);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to load environment variables from .env file: " + e.getMessage(), e);
            throw new RuntimeException("Failed to load environment variables from .env file.", e);
        }
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
}