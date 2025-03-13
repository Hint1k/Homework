package com.demo.finance.app.liquibase;

import com.demo.finance.app.config.DatabaseConfig;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;

public class LiquibaseMigrationService {

    private final DatabaseConfig databaseConfig;

    public LiquibaseMigrationService(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public void runMigrations() {
        String url = databaseConfig.getLiquibaseUrl();
        String username = databaseConfig.getLiquibaseUsername();
        String password = databaseConfig.getLiquibasePassword();
        String changeLogFile = databaseConfig.getLiquibaseChangeLogFile();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);
            liquibase.update("");
            System.out.println("Liquibase migration completed successfully.");
        } catch (Exception e) {
            System.err.println("Failed to run Liquibase migrations: " + e.getMessage());
            e.printStackTrace();
        }
    }
}