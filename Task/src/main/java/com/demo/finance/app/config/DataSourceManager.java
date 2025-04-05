package com.demo.finance.app.config;

import com.demo.finance.exception.DatabaseConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The {@code DataSourceManager} class is responsible for managing database connections.
 * It uses the configuration provided by the {@link DatabaseConfig} class to establish
 * a connection to the database via JDBC.
 * <p>
 * This class ensures that database connection errors are logged appropriately and
 * wrapped in a custom exception for better error handling.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DataSourceManager {

    private final DatabaseConfig config;

    /**
     * Establishes and returns a connection to the database using the configured URL,
     * username, and password from the {@link DatabaseConfig} instance.
     * <p>
     * If the connection fails due to invalid credentials, network issues, or other SQL-related
     * problems, the error is logged at the SEVERE level, and a custom
     * {@link DatabaseConnectionException} is thrown with detailed information about the failure.
     *
     * @return a {@link Connection} object representing the established database connection
     * @throws DatabaseConnectionException if the connection cannot be established
     */
    public Connection getConnection() {
        String url = config.getDbUrl();
        String username = config.getDbUsername();
        String password = config.getDbPassword();
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            String errorMessage = "Failed to establish a database connection. URL: " + url + ", Username: " + username;
            log.error(errorMessage, e);
            throw new DatabaseConnectionException(errorMessage, e);
        }
    }
}