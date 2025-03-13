package com.demo.finance.app;

import com.demo.finance.app.config.ApplicationConfig;
import com.demo.finance.app.config.DatabaseConfig;
import com.demo.finance.app.config.LiquibaseManager;
import com.demo.finance.in.cli.CliHandler;

/**
 * The entry point for the TaskOne application. It initializes the application configuration
 * and starts the Command Line Interface (CLI) handler to interact with the user.
 */
public class TaskTwoMain {

    /**
     * The main method that initializes the application configuration,
     * retrieves the CLI handler, and starts the application.
     *
     * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Step 1: Initialize configuration
        DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

        // Step 2: Run Liquibase migrations
        LiquibaseManager migrationService = new LiquibaseManager(databaseConfig);
        migrationService.runMigrations();

        // Step 3: Start the application
        ApplicationConfig config = new ApplicationConfig();
        CliHandler cliHandler = config.getCliHandler();
        cliHandler.start();
    }
}