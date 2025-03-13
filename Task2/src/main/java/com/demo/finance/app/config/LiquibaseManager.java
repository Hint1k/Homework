package com.demo.finance.app.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class LiquibaseManager {

    private final DatabaseConfig databaseConfig;

    public LiquibaseManager(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    /**
     * Injects the admin credentials into the Liquibase changelog XML file
     * by replacing placeholders with values from the .env file.
     *
     * @param changeLogFile Path to the Liquibase changelog XML file
     * @throws IOException If there's an issue reading or writing the file
     */
    public Path injectAdminCredentialsIntoChangelog(String changeLogFile) throws IOException {
        String adminEmail = databaseConfig.getAdminEmail();
        String adminPassword = databaseConfig.getAdminPassword();

        // Read the original XML changelog file
        Path originalPath = Paths.get(changeLogFile);
        String changelog = Files.readString(originalPath);

        // Replace placeholders with actual values from the .env file
        changelog = changelog.replace("${ADMIN_EMAIL}", adminEmail);
        changelog = changelog.replace("${ADMIN_PASSWORD_HASH}", adminPassword);

        // Create a temporary file
        Path tempFile = Files.createTempFile("changelog", ".xml");
        Files.writeString(tempFile, changelog);

        log.info("Created temporary changelog file: {}", tempFile.toString());

        // Return the path to the temporary file
        return tempFile;
    }

    /**
     * Runs Liquibase migrations after injecting the admin credentials
     * into the changelog XML file.
     */
    public void runMigrations() {
        String changeLogFile = databaseConfig.getLiquibaseChangeLogFile();
        String url = databaseConfig.getLiquibaseUrl();
        String username = databaseConfig.getLiquibaseUsername();
        String password = databaseConfig.getLiquibasePassword();

        Path tempFile = null;
        try {
            // Inject admin credentials into the changelog file and get the temporary file path
            tempFile = injectAdminCredentialsIntoChangelog(changeLogFile);

            // Connect to the database
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                Database database = DatabaseFactory.getInstance()
                        .findCorrectDatabaseImplementation(new JdbcConnection(connection));

                // Use ClassLoaderResourceAccessor for Liquibase migration
                Liquibase liquibase = new Liquibase(
                        tempFile.toString(), // Use the temporary file
                        new ClassLoaderResourceAccessor(getClass().getClassLoader()),
                        database
                );
                liquibase.update("");
                log.info("Liquibase migration completed successfully.");
            }

        } catch (Exception e) {
            log.error("Failed to run Liquibase migrations: {}", e.getMessage());
            throw new RuntimeException(e);

        } finally {
            // Clean up the temporary file
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                    log.info("Deleted temporary changelog file: {}", tempFile.toString());
                } catch (IOException e) {
                    log.warn("Failed to delete temporary changelog file: {}", tempFile.toString(), e);
                }
            }
        }
    }
}