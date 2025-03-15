package com.demo.finance.app.config;

import com.demo.finance.out.repository.AbstractContainerBaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThatCode;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LiquibaseManagerTest extends AbstractContainerBaseTest {

    private static final Logger log = Logger.getLogger(LiquibaseManagerTest.class.getName());
    private static LiquibaseManager liquibaseManager;

    @BeforeAll
    void setup() {
        DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
        liquibaseManager = new LiquibaseManager(databaseConfig);
    }

    @Test
    @DisplayName("Run Liquibase migrations successfully")
    void testRunMigrations_success() {
        assertThatCode(() -> liquibaseManager.runMigrations()).doesNotThrowAnyException();
        log.info("Liquibase migrations ran successfully without exceptions.");
    }
}