package com.demo.finance.app;

import com.demo.finance.app.config.ApplicationConfig;
import com.demo.finance.app.config.DatabaseConfig;
import com.demo.finance.app.config.LiquibaseManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The entry point for the TaskTwo application.
 * This class initializes the Personal Finance application by performing the following steps:
 * 1. Runs Liquibase migrations to apply database schema changes.
 * 2. Schedules the startup of the Command Line Interface (CLI) after a short delay
 * to ensure Liquibase logs are written.
 */
public class TaskTwoMain {

    /**
     * The main method serves as the entry point for the application.
     * It performs the following actions:
     * 1. Prints a startup message to indicate the beginning of the application.
     * 2. Initializes and runs Liquibase migrations using the {@link LiquibaseManager}.
     * 3. Schedules the CLI to start after a delay of 3 seconds to allow Liquibase logs to be written to the console.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        System.out.println("Starting Personal Finance App...");

        LiquibaseManager liquibaseManager = new LiquibaseManager(DatabaseConfig.getInstance());
        liquibaseManager.runMigrations();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            ApplicationConfig appConfig = new ApplicationConfig();
            CliHandler cliHandler = appConfig.getCliHandler();
            cliHandler.start();
        }, 3, TimeUnit.SECONDS); // making sure all liquibase logs are written in console
        scheduler.shutdown();
    }
}