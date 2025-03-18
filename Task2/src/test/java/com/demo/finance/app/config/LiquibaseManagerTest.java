package com.demo.finance.app.config;

import com.demo.finance.out.repository.impl.AbstractContainerBaseSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LiquibaseManagerTest extends AbstractContainerBaseSetup {

    private static final Logger log = Logger.getLogger(LiquibaseManagerTest.class.getName());
    private static LiquibaseManager liquibaseManager;

    @BeforeAll
    void setup() {
        DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

        log.info("Database URL: " + databaseConfig.getDbUrl());
        log.info("Database Username: " + databaseConfig.getDbUsername());
        log.info("Database Password: " + databaseConfig.getDbPassword());

        liquibaseManager = new LiquibaseManager(databaseConfig);
    }

    @Test
    @DisplayName("Run Liquibase migrations successfully")
    void testRunMigrations_success() {
        assertThatCode(() -> liquibaseManager.runMigrations()).doesNotThrowAnyException();
        log.info("Liquibase migrations ran successfully without exceptions.");
    }

    @Test
    @DisplayName("Run Liquibase migrations with invalid credentials - should throw exception")
    void testRunMigrations_InvalidCredentials() {
        System.setProperty("DB_USERNAME", "invalidUser");
        System.setProperty("DB_PASSWORD", "invalidPass");

        DatabaseConfig invalidConfig = DatabaseConfig.getInstance();
        LiquibaseManager invalidLiquibaseManager = new LiquibaseManager(invalidConfig);

        assertThatThrownBy(invalidLiquibaseManager::runMigrations).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("FATAL: password authentication failed for user \"invalidUser\"");

        log.info("Liquibase migrations failed as expected with invalid credentials.");
    }
}