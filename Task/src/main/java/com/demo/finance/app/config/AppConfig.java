package com.demo.finance.app.config;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

/**
 * The {@code AppConfig} class is a configuration class that defines application-wide settings
 * for the Spring MVC framework. It enables CORS mappings, configures resource handlers for serving static content
 * (e.g., Swagger UI), and sets up view controllers. This class is annotated with {@link Configuration},
 * {@link EnableWebMvc}, and {@link EnableAspectJAutoProxy} to configure
 * Spring's web MVC capabilities and AspectJ-based proxying.
 */
@Configuration
@EnableAspectJAutoProxy
@Slf4j
public class AppConfig implements WebMvcConfigurer {

    /**
     * Configures Cross-Origin Resource Sharing (CORS) mappings to allow requests from specific origins and methods.
     * <p>
     * This method permits requests from {@code http://localhost:8080} and allows the HTTP methods
     * GET, POST, PUT, DELETE, and OPTIONS. Credentials (e.g., cookies or authorization headers) are also allowed
     * for cross-origin requests.
     *
     * @param registry the {@link CorsRegistry} used to define CORS mappings
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080") // Allow requests only from localhost:8080
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * Configures view controllers to redirect specific paths to predefined views.
     * <p>
     * This method redirects the root path ({@code /}) to the Swagger UI index page ({@code /swagger-ui/index.html}),
     * providing a convenient entry point for API documentation.
     *
     * @param registry the {@link ViewControllerRegistry} used to define view controllers
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui/index.html");
    }

    /**
     * Configures the embedded Jetty web server factory with a specific port indicated in application.yml.
     *
     * @return configured JettyServletWebServerFactory instance
     */
    @Bean
    public JettyServletWebServerFactory jettyServletWebServerFactory() {
        return new JettyServletWebServerFactory();
    }

    /**
     * Creates and configures the primary datasource for the application.
     *
     * @param databaseConfig the configuration properties for database connection
     * @return configured PostgreSQL datasource instance
     */
    @Bean
    @Primary
    public DataSource dataSource(DatabaseConfig databaseConfig) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(databaseConfig.getDbUrl());
        dataSource.setUser(databaseConfig.getDbUsername());
        dataSource.setPassword(databaseConfig.getDbPassword());
        return dataSource;
    }

    /**
     * Creates a Liquibase manager bean for database migrations.
     *
     * @param databaseConfig the configuration properties for database connection
     * @return configured Liquibase manager instance
     */
    @Bean
    public LiquibaseManager liquibaseManager(DatabaseConfig databaseConfig) {
        return new LiquibaseManager(databaseConfig);
    }

    /**
     * Initializes database migrations using Liquibase.
     *
     * @param liquibaseManager the Liquibase manager instance
     * @return CommandLineRunner that executes database migrations
     */
    @Bean
    public CommandLineRunner init(LiquibaseManager liquibaseManager) {
        return args -> liquibaseManager.runMigrations();
    }

    /**
     * Provides startup message displaying application availability and endpoints.
     *
     * @return ApplicationRunner that logs important startup information
     */
    @Bean
    public ApplicationRunner startupMessage() {
        return args -> {
            log.info("The Personal Finance Tracker is up and running!");
            log.info("Swagger UI: http://localhost:8080");
            log.info("API Docs: http://localhost:8080/v3/api-docs");
        };
    }
}