package com.demo.finance.app;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

/**
 * The {@code TaskMain} class serves as the entry point for the Personal Finance Tracker Application.
 * It initializes the Spring Boot application and starts the embedded Jetty server.
 */
@SpringBootApplication(scanBasePackages = "com.demo.finance.app")
public class TaskMain {

    public static void main(String[] args) {
        SpringApplication.run(TaskMain.class, args);
    }
}