package com.demo.finance.app;

import com.demo.finance.starter.logging.EnableLogging;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * The {@code TaskMain} class serves as the entry point for the Personal Finance Tracker Application.
 * It initializes the Spring Boot application and starts the embedded Jetty server.
 */
@SpringBootApplication(scanBasePackages = "com.demo.finance")
@EnableLogging
@EnableCaching
public class TaskMain {

    /**
     * The main entry point for the Personal Finance Tracker Application.
     * <p>
     * This method initializes and starts the Spring Boot application by invoking the
     * {@link SpringApplication#run(Class, String...)} method. It performs the following:
     * <ul>
     *   <li>Scans the specified base package ({@code com.demo.finance.app}) for Spring components,
     *       configurations, and beans.</li>
     *   <li>Starts the embedded Jetty server to handle incoming HTTP requests.</li>
     *   <li>Initializes the Spring application context, including dependency injection and bean wiring.</li>
     * </ul>
     * <p>
     * The application is configured to use Spring Boot's autoconfiguration and component scanning features,
     * ensuring that all required components are discovered and registered during startup.
     *
     * @param args command-line arguments passed to the application (not used in this implementation)
     */
    public static void main(String[] args) {
        SpringApplication.run(TaskMain.class, args);
    }
}