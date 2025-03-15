package com.demo.finance.app.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LiquibaseManager {

    private static final Logger log = Logger.getLogger(LiquibaseManager.class.getName());
    private final DatabaseConfig databaseConfig;
    private static final String CHANGELOG = "db/changelog/changelog.xml";

    public LiquibaseManager(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public void runMigrations() {
        try (InputStream changelogStream = getClass().getClassLoader().getResourceAsStream(CHANGELOG)) {
            if (changelogStream == null) {
                log.severe("Changelog file not found in the classpath: " + CHANGELOG);
                throw new RuntimeException("Changelog file is missing. Please ensure it is located at " + CHANGELOG);
            }
            log.info("Changelog file found in the classpath: " + CHANGELOG);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error while checking for changelog file: " + e.getMessage(), e);
            throw new RuntimeException("Failed to verify the existence of the changelog file.", e);
        }

        String url = databaseConfig.getDbUrl();
        String username = databaseConfig.getDbUsername();
        String password = databaseConfig.getDbPassword();

        try {
            log.info("Attempting to load changelog file from classpath: " + CHANGELOG);

            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                Database database = DatabaseFactory.getInstance()
                        .findCorrectDatabaseImplementation(new JdbcConnection(connection));

                Liquibase liquibase = new Liquibase(CHANGELOG, new ClassLoaderResourceAccessor(), database);

                liquibase.update("");
                log.info("Liquibase migration completed successfully.");
            }

        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to run Liquibase migrations: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}