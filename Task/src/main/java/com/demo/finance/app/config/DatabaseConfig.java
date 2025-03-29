package com.demo.finance.app.config;

import com.demo.finance.domain.utils.SystemPropLoader;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.logging.Logger;

@Component
public class DatabaseConfig {
    private static final Logger log = Logger.getLogger(DatabaseConfig.class.getName());
    private static final String DB_URL = "DB_URL";
    private static final String DB_USERNAME = "DB_USERNAME";
    private static final String DB_PASSWORD = "DB_PASSWORD";
    private static final String DEFAULT_ENV_FILE = "/app/.env";
    private static final String DEFAULT_YML_FILE = "/app/application.yml";

    @PostConstruct
    public void init() {
        loadAndValidateProperties();
    }

    public String getDbUrl() {
        validateProperty(DB_URL);
        return System.getProperty(DB_URL);
    }

    public String getDbUsername() {
        validateProperty(DB_USERNAME);
        return System.getProperty(DB_USERNAME);
    }

    public String getDbPassword() {
        validateProperty(DB_PASSWORD);
        return System.getProperty(DB_PASSWORD);
    }

    private void loadAndValidateProperties() {
        String envFilePath = System.getProperty("ENV_PATH", DEFAULT_ENV_FILE);
        String ymlFilePath = System.getProperty("YML_PATH", DEFAULT_YML_FILE);
        Set<String> envProperties = Set.of(DB_USERNAME, DB_PASSWORD);
        Set<String> ymlProperties = Set.of(DB_URL);

        SystemPropLoader.loadAndSetProperties(envFilePath, ymlFilePath, envProperties, ymlProperties);

        validateProperty(DB_URL);
        validateProperty(DB_USERNAME);
        validateProperty(DB_PASSWORD);
    }

    private void validateProperty(String propertyKey) {
        String propertyValue = System.getProperty(propertyKey);
        if (propertyValue == null || propertyValue.isEmpty()) {
            log.severe(propertyKey + " is missing or empty in the system properties.");
            throw new RuntimeException(propertyKey + " is not configured.");
        }
    }
}