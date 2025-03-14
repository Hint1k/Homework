package com.demo.finance.app.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LiquibaseManager {

    private final DatabaseConfig databaseConfig;
    private static final Logger log = Logger.getLogger(LiquibaseManager.class.getName());

    public LiquibaseManager(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    private void createDatabaseIfNotExists() {
        String adminUrl = "jdbc:postgresql://postgres:5432/postgres?user=" + databaseConfig.getLiquibaseUsername()
                + "&password=" + databaseConfig.getLiquibasePassword();

        try (Connection conn = DriverManager.getConnection(adminUrl);
             Statement stmt = conn.createStatement()) {
            // Check if the database exists before trying to create it
            ResultSet rs = conn.getMetaData().getCatalogs();
            boolean dbExists = false;
            while (rs.next()) {
                if ("financedb".equalsIgnoreCase(rs.getString(1))) {
                    dbExists = true;
                    break;
                }
            }
            if (!dbExists) {
                stmt.executeUpdate("CREATE DATABASE financedb;");
                log.info("Database 'financedb' created successfully.");
            } else {
                log.info("Database 'financedb' already exists. Skipping creation.");
            }
        } catch (SQLException e) {
            log.info("Database might already exist or an error occurred: " + e.getMessage());
        }
    }

    private void ensureSchemaExists() {
        String dbUrl = databaseConfig.getLiquibaseUrl(); // This should now be financedb
        String username = databaseConfig.getLiquibaseUsername();
        String password = databaseConfig.getLiquibasePassword();

        try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS finance;");
            log.info("Schema 'finance' ensured.");
        } catch (SQLException e) {
            log.severe("Failed to create schema: " + e.getMessage());
        }
    }

    /**
     * Runs Liquibase migrations using the changelog inside the JAR.
     */
    public void runMigrations() {
        createDatabaseIfNotExists();
        ensureSchemaExists();

        // Path inside the JAR
        String changeLogFile = "db/changelog/changelog.xml";

        String url = databaseConfig.getLiquibaseUrl();
        String username = databaseConfig.getLiquibaseUsername();
        String password = databaseConfig.getLiquibasePassword();

        try {
            log.info("Attempting to load changelog file from classpath: " + changeLogFile);

            // Connect to the database
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                Database database = DatabaseFactory.getInstance()
                        .findCorrectDatabaseImplementation(new JdbcConnection(connection));

                // Set the default schema programmatically
                database.setDefaultSchemaName(databaseConfig.getLiquibaseDefaultSchema());

                // Load changelog from the JAR using ClassLoaderResourceAccessor
                Liquibase liquibase = new Liquibase(
                        changeLogFile,
                        new ClassLoaderResourceAccessor(),
                        database
                );

                liquibase.update("");
                log.info("Liquibase migration completed successfully.");
            }

        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to run Liquibase migrations: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}