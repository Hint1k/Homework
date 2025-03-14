package com.demo.finance.app.config;

import com.demo.finance.exception.DatabaseConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataSourceManager {

    private static final Logger log = Logger.getLogger(DataSourceManager.class.getName());
    private static final DatabaseConfig config = DatabaseConfig.getInstance();

    private DataSourceManager() {
    } // Private constructor to prevent instantiation

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