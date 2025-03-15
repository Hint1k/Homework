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

    public static void main(String[] args) {
        System.out.println("Starting Personal Finance App...");

        // Step 1: Initialize database configuration and migrations
        DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
        new LiquibaseManager(databaseConfig).runMigrations();

        // Step 2: Start CLI mode (since it's always running inside Docker)
        CliHandler cliHandler = new ApplicationConfig().getCliHandler();
        cliHandler.start();
    }
}