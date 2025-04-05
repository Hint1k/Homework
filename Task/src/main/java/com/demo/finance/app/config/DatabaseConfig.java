package com.demo.finance.app.config;

import com.demo.finance.domain.utils.SystemPropLoader;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * The {@code DatabaseConfig} class is responsible for managing database-related configuration properties.
 * It ensures that required database properties (URL, username, and password) are loaded from system properties
 * or external configuration files and validates their presence and correctness.
 * <p>
 * This class uses environment-specific configuration files (e.g., `.env` and `application.yml`) to load properties
 * and provides methods to retrieve these properties securely. Missing or invalid properties will result in runtime
 * exceptions to prevent misconfiguration.
 */
@Component
@Slf4j
public class DatabaseConfig {

    private static final String DB_URL = "DB_URL";
    private static final String DB_USERNAME = "DB_USERNAME";
    private static final String DB_PASSWORD = "DB_PASSWORD";
    private static final String DEFAULT_ENV_FILE = "/app/.env";

    @Value("${app.db.url}")
    private String injectedUrl;

    /**
     * Initializes the database configuration by loading and validating required properties.
     * This method is automatically invoked after the bean is constructed.
     * <p>
     * It ensures that all required database properties are available in the system properties.
     * If any property is missing or empty, a runtime exception is thrown.
     *
     * @see #loadAndValidateProperties()
     */
    @PostConstruct
    public void init() {
        loadAndValidateProperties();
    }

    /**
     * Retrieves the database URL from the system properties.
     * <p>
     * Before returning the value, this method validates that the property is present and non-empty.
     * If the property is missing or invalid, a runtime exception is thrown.
     *
     * @return the database URL as a string
     * @throws RuntimeException if the database URL is not configured
     */
    public String getDbUrl() {
        validateProperty(DB_URL);
        return System.getProperty(DB_URL);
    }

    /**
     * Retrieves the database username from the system properties.
     * <p>
     * Before returning the value, this method validates that the property is present and non-empty.
     * If the property is missing or invalid, a runtime exception is thrown.
     *
     * @return the database username as a string
     * @throws RuntimeException if the database username is not configured
     */
    public String getDbUsername() {
        validateProperty(DB_USERNAME);
        return System.getProperty(DB_USERNAME);
    }

    /**
     * Retrieves the database password from the system properties.
     * <p>
     * Before returning the value, this method validates that the property is present and non-empty.
     * If the property is missing or invalid, a runtime exception is thrown.
     *
     * @return the database password as a string
     * @throws RuntimeException if the database password is not configured
     */
    public String getDbPassword() {
        validateProperty(DB_PASSWORD);
        return System.getProperty(DB_PASSWORD);
    }

    /**
     * Loads and validates database-related properties required by the application.
     * <p>
     * This method performs the following steps:
     * <ul>
     *   <li>Determines the path to the {@code .env} file using the {@code ENV_PATH} system property
     *   or a default path.</li>
     *   <li>Loads the {@code DB_USERNAME} and {@code DB_PASSWORD} properties from the environment file
     *       using the {@link SystemPropLoader} utility.</li>
     *   <li>Validates that the injected value of {@code app.db.url} from {@code application.yml}
     *   is not null or empty.</li>
     *   <li>Sets the injected database URL as the {@code DB_URL} system property to ensure compatibility
     *       with components relying on system properties.</li>
     *   <li>Validates that all required database properties ({@code DB_URL}, {@code DB_USERNAME}, {@code DB_PASSWORD})
     *       are present and non-empty in the system properties.</li>
     * </ul>
     *
     * @throws RuntimeException if any required property is missing or empty
     */
    private void loadAndValidateProperties() {
        String envFilePath = System.getProperty("ENV_PATH", DEFAULT_ENV_FILE);
        Set<String> envProperties = Set.of(DB_USERNAME, DB_PASSWORD);
        SystemPropLoader.loadAndSetProperties(envFilePath, envProperties);
        if (injectedUrl != null) {
            validateInjectedValue(injectedUrl);
            System.setProperty(DB_URL, injectedUrl);
        }
        validateProperty(DB_USERNAME);
        validateProperty(DB_PASSWORD);
        validateProperty(DB_URL);
    }

    /**
     * Validates that a given property key exists in the system properties and is non-empty.
     * <p>
     * If the property is missing or empty, this method logs a severe error message and throws a runtime exception.
     *
     * @param propertyKey the key of the property to validate
     * @throws RuntimeException if the property is missing or empty
     */
    private void validateProperty(String propertyKey) {
        String propertyValue = System.getProperty(propertyKey);
        if (propertyValue == null || propertyValue.isEmpty()) {
            log.error("{} is missing or empty in the system properties.", propertyKey);
            throw new RuntimeException(propertyKey + " is not configured.");
        }
    }

    /**
     * Validates that the injected value for {@code app.db.url} from {@code application.yml} is not null or empty.
     * <p>
     * This method is used to ensure that a meaningful and valid database URL is provided via configuration.
     * If the value is {@code null}, empty, or only whitespace, it logs an error and throws a {@link RuntimeException}.
     *
     * @param value the injected value of {@code app.db.url} to validate
     * @throws RuntimeException if the value is {@code null}, empty, or blank
     */
    private void validateInjectedValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            log.error("app.db.url is missing or empty in application.yml.");
            throw new RuntimeException("app.db.url is missing or empty in application.yml.");
        }
    }
}