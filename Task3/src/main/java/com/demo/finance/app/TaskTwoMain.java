package com.demo.finance.app;

import com.demo.finance.app.config.DatabaseConfig;
import com.demo.finance.app.config.LiquibaseManager;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskTwoMain {

    private static final Logger log = Logger.getLogger(TaskTwoMain.class.getName());

    public static void main(String[] args) {
        try {
            DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
            new LiquibaseManager(databaseConfig).runMigrations();
            log.info("Finance App is running inside Docker!");
            CountDownLatch latch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Shutting down FinanceApp...");
                latch.countDown();
            }));
            latch.await();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Application failed to start", e);
            System.exit(1);
        }
    }
}