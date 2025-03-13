package com.demo.finance.app.config;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class DatabaseConfig {

    private static final DatabaseConfig INSTANCE = new DatabaseConfig();
    private final Map<String, String> envVars;

    private DatabaseConfig() {
        // Load environment variables from .env file
        envVars = EnvLoader.loadEnv("../.env");
    }

    public static DatabaseConfig getInstance() {
        return INSTANCE;
    }

    // Database connection properties
    public String getDbUrl() {
        return envVars.get("DB_URL");
    }

    public String getDbUsername() {
        return envVars.get("DB_USERNAME");
    }

    public String getDbPassword() {
        return envVars.get("DB_PASSWORD");
    }

    public String getDbDriver() {
        return envVars.get("DB_DRIVER");
    }

    public String getAdminEmail() {
        return envVars.get("ADMIN_EMAIL");
    }

    public String getAdminPassword() {
        return envVars.get("ADMIN_PASSWORD");
    }

    // Liquibase properties
    public String getLiquibaseChangeLogFile() {
        return "db/changelog/changelog.xml"; // Hardcoded as it’s unlikely to change
    }

    public String getLiquibaseDefaultSchema() {
        return "finance"; // Hardcoded as it’s unlikely to change
    }

    public String getLiquibaseUrl() {
        return getDbUrl(); // Use the same DB URL
    }

    public String getLiquibaseUsername() {
        return getDbUsername(); // Use the same DB username
    }

    public String getLiquibasePassword() {
        return getDbPassword(); // Use the same DB password
    }

    public String getLiquibaseDriver() {
        return getDbDriver(); // Use the same DB driver
    }
}