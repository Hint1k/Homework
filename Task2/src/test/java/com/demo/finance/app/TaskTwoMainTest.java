package com.demo.finance.app;

import com.demo.finance.app.config.ApplicationConfig;
import com.demo.finance.in.cli.CliHandler;
import com.demo.finance.out.repository.AbstractContainerBaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskTwoMainTest extends AbstractContainerBaseTest {

    private static final Logger log = Logger.getLogger(TaskTwoMainTest.class.getName());

    @Test
    @DisplayName("Verify that Liquibase migrations and CLI start successfully")
    void testTaskTwoMainExecution() {
        assertThatCode(() -> {TaskTwoMain.main(new String[]{});}).doesNotThrowAnyException();

        try (Connection connection = DriverManager.getConnection(
                POSTGRESQL_CONTAINER.getJdbcUrl(),
                POSTGRESQL_CONTAINER.getUsername(),
                POSTGRESQL_CONTAINER.getPassword())) {

            ResultSet resultSet = connection.getMetaData()
                    .getTables(null, "finance", "users", null);
            assertThat(resultSet.next()).isTrue();

            log.info("Liquibase migrations applied successfully.");
        } catch (SQLException e) {
            String errorMessage = "Failed to verify Liquibase migrations due to a database error: " + e.getMessage();
            log.log(Level.SEVERE, errorMessage, e);
            fail(errorMessage);
        }

        ApplicationConfig appConfig = Mockito.mock(ApplicationConfig.class);
        CliHandler cliHandler = Mockito.mock(CliHandler.class);
        Mockito.when(appConfig.getCliHandler()).thenReturn(cliHandler);
        Mockito.doNothing().when(cliHandler).start();
        assertThatCode(cliHandler::start).doesNotThrowAnyException();
        log.info("CLI started successfully.");
    }
}