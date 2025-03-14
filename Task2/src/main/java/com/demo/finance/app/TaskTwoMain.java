package com.demo.finance.app;

import com.demo.finance.app.config.ApplicationConfig;
import com.demo.finance.app.config.DatabaseConfig;
import com.demo.finance.app.config.LiquibaseManager;
import com.demo.finance.in.cli.CliHandler;

import java.util.concurrent.CountDownLatch;

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
        System.out.println("Starting Personal Finance App...");

        // Step 1: Initialize configuration
        DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

        // Step 2: Run Liquibase migrations
        LiquibaseManager migrationService = new LiquibaseManager(databaseConfig);
        migrationService.runMigrations();

        // Step 3: Check if running in Docker (non-interactive mode)
        if (System.console() == null) {
            // Keep the application running indefinitely to prevent container restarts
            CountDownLatch latch = new CountDownLatch(1);
            try {
                latch.await();
            } catch (InterruptedException e) {
                System.out.println("Application interrupted. Shutting down...");
            }
        } else {
            // Step 4: Start CLI mode (only if interactive)
            ApplicationConfig config = new ApplicationConfig();
            CliHandler cliHandler = config.getCliHandler();
            cliHandler.start();
        }
    }
}