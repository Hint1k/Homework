package com.demo.finance.app.config;

import com.demo.finance.exception.DatabaseConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class responsible for managing database connections.
 * It provides a static method to retrieve a connection using configuration details
 * from the {@link DatabaseConfig} class. This class follows the singleton pattern
 * to prevent instantiation.
 */
public class DataSourceManager {

    private static final Logger log = Logger.getLogger(DataSourceManager.class.getName());
    private static final DatabaseConfig config = DatabaseConfig.getInstance();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private DataSourceManager() {
    } // Private constructor to prevent instantiation

    /**
     * Retrieves a database connection using the configuration details (URL, username, password)
     * provided by the {@link DatabaseConfig} class.
     *
     * @return a {@link Connection} object representing the database connection
     * @throws DatabaseConnectionException if a SQLException occurs while establishing the connection
     */
    public static Connection getConnection() {
        String url = config.getDbUrl();
        String username = config.getDbUsername();
        String password = config.getDbPassword();
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            String errorMessage = "Failed to establish a database connection. URL: " + url + ", Username: " + username;
            log.log(Level.SEVERE, errorMessage, e);
            throw new DatabaseConnectionException(errorMessage, e);
        }
    }
}