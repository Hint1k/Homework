package com.demo.finance.app.config;

import com.demo.finance.exception.DatabaseConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component
public class DataSourceManager {

    private static final Logger log = Logger.getLogger(DataSourceManager.class.getName());
    private final DatabaseConfig config;

    @Autowired
    public DataSourceManager(DatabaseConfig config) {
        this.config = config;
    }

    public Connection getConnection() {
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