package com.demo.finance.app.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages Liquibase database migrations.
 * This class is responsible for loading environment variables, verifying the existence of the changelog file,
 * and executing Liquibase migrations against the configured database.
 */
public class LiquibaseManager {

    private static final Logger log = Logger.getLogger(LiquibaseManager.class.getName());
    private final DatabaseConfig databaseConfig;
    private static final String CHANGELOG = "db/changelog/changelog.xml";
    private final static String ADMIN_EMAIL = "ADMIN_EMAIL";
    private final static String ADMIN_PASSWORD = "ADMIN_PASSWORD";
    private static final String DEFAULT_ENV = ".env";

    /**
     * Constructs a new instance of {@code LiquibaseManager} with the provided {@link DatabaseConfig}.
     *
     * @param databaseConfig the configuration object containing database connection details
     */
    public LiquibaseManager(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    /**
     * Executes Liquibase migrations using the configured database connection and changelog file.
     * This method performs the following steps:
     * 1. Loads environment variables (admin credentials).
     * 2. Verifies the existence of the changelog file in the classpath.
     * 3. Establishes a connection to the database and applies the migrations.
     *
     * @throws RuntimeException if any step fails, such as missing changelog file, invalid credentials,
     * or migration errors
     */
    public void runMigrations() {
        loadEnvVariables();

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

    /**
     * Loads environment variables (admin email and password) from the `.env` file.
     * If the `.env` file is missing or contains invalid/empty values for admin credentials,
     * an exception is thrown.
     *
     * @throws RuntimeException if the `.env` file is missing, malformed, or contains empty/invalid admin credentials
     */
    private void loadEnvVariables() {
        try {
            String envFilePath = System.getProperty("ENV_PATH", DEFAULT_ENV);
            Map<String, String> envVars = EnvLoader.loadEnv(envFilePath);

            String adminEmail = envVars.get(ADMIN_EMAIL);
            if (adminEmail == null || adminEmail.isEmpty()) {
                log.severe(ADMIN_EMAIL + " is missing or empty in the .env file.");
                throw new RuntimeException(ADMIN_EMAIL + " is not configured in the .env file.");
            }

            String adminPassword = envVars.get(ADMIN_PASSWORD);
            if (adminPassword == null || adminPassword.isEmpty()) {
                log.severe(ADMIN_PASSWORD + " is missing or empty in the .env file.");
                throw new RuntimeException(ADMIN_PASSWORD + " is not configured in the .env file.");
            }

            System.setProperty(ADMIN_EMAIL, adminEmail);
            System.setProperty(ADMIN_PASSWORD, adminPassword);

            log.info("Successfully loaded admin credentials from the .env file.");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error loading environment variables: " + e.getMessage(), e);
            throw new RuntimeException("Failed to load environment variables", e);
        }
    }
}