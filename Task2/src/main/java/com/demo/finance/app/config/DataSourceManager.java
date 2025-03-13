package com.demo.finance.app.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSourceManager {

    private static final DatabaseConfig config = DatabaseConfig.getInstance();

    // Private constructor to prevent instantiation
    private DataSourceManager() {
    }

    public static Connection getConnection() throws SQLException {
        String url = config.getDbUrl();
        String username = config.getDbUsername();
        String password = config.getDbPassword();
        return DriverManager.getConnection(url, username, password);
    }
}