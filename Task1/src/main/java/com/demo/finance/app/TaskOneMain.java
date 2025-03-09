package com.demo.finance.app;

import com.demo.finance.in.cli.CliHandler;

/**
 * The entry point for the TaskOne application. It initializes the application configuration
 * and starts the Command Line Interface (CLI) handler to interact with the user.
 */
public class TaskOneMain {

    /**
     * The main method that initializes the application configuration,
     * retrieves the CLI handler, and starts the application.
     *
     * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Initialize the application configuration and CLI handler
        ApplicationConfig config = new ApplicationConfig();
        CliHandler cliHandler = config.getCliHandler();

        // Start the CLI to handle user input and commands
        cliHandler.start();
    }
}