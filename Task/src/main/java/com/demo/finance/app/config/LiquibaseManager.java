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

/**
 * Manages Liquibase database migrations.
 * This class is responsible for validating the existence of the changelog file,
 * establishing a connection to the database, and executing Liquibase migrations.
 */
public class LiquibaseManager {

    private static final Logger log = Logger.getLogger(LiquibaseManager.class.getName());
    private final DatabaseConfig databaseConfig;
    private static final String CHANGELOG = "db/changelog/changelog.xml";

    /**
     * Constructs a new instance of {@code LiquibaseManager} with the provided database configuration.
     *
     * @param databaseConfig The configuration containing database connection details (URL, username, password).
     */
    public LiquibaseManager(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    /**
     * Executes the Liquibase migration process.
     * This method first validates the existence of the changelog file and then runs the migrations.
     */
    public void runMigrations() {
        validateChangelogFile();
        runLiquibaseMigrations();
    }

    /**
     * Executes Liquibase migrations against the configured database.
     * Establishes a connection to the database using the provided configuration and applies the migrations
     * defined in the changelog file.
     *
     * @throws RuntimeException If an error occurs while connecting to the database or running the migrations.
     */
    private void runLiquibaseMigrations() {
        String url = databaseConfig.getDbUrl();
        String username = databaseConfig.getDbUsername();
        String password = databaseConfig.getDbPassword();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(CHANGELOG, new ClassLoaderResourceAccessor(), database);
            liquibase.update("");
            log.info("Liquibase migration completed successfully.");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to run Liquibase migrations: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Validates the existence of the Liquibase changelog file in the classpath.
     * Logs an informational message if the file is found, or throws an exception if the file is missing.
     *
     * @throws RuntimeException If the changelog file is not found in the classpath.
     */
    private void validateChangelogFile() {
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
    }
}