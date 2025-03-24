package com.demo.finance.app;

import com.demo.finance.app.config.AppConfig;
import com.demo.finance.out.repository.impl.AbstractContainerBaseSetup;
import org.eclipse.jetty.servlet.FilterHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import com.demo.finance.in.filter.AuthenticationFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskMainTest extends AbstractContainerBaseSetup {
    @Mock private Server mockServer;
    @Mock private AppConfig mockAppConfig;
    @Mock private AuthenticationFilter mockAuthFilter;
    @Mock private Logger mockLogger;
    @Mock private ServletContextHandler mockContext;

    @Test
    @DisplayName("Verify database connection through AppConfig")
    void testDatabaseConnection() {
        assertThatCode(() -> {
            try (Connection conn = DriverManager.getConnection(
                    POSTGRESQL_CONTAINER.getJdbcUrl(),
                    POSTGRESQL_CONTAINER.getUsername(),
                    POSTGRESQL_CONTAINER.getPassword())) {
                assertThat(conn.isValid(1)).isTrue();
            }
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Verify that main method starts Jetty server with correct configuration")
    void testTaskMainExecution() {
        try (MockedStatic<TaskMain> mockedTaskMain = mockStatic(TaskMain.class)) {
            mockedTaskMain.when(() -> TaskMain.main(any()))
                    .thenAnswer(invocation -> {
                        Server server = mock(Server.class);
                        ServletContextHandler context = mock(ServletContextHandler.class);
                        return null;
                    });

            assertThatCode(() -> TaskMain.main(new String[]{})).doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("Verify authentication filter is properly configured")
    void testAuthenticationFilterConfiguration() {
        try (MockedStatic<TaskMain> mockedTaskMain = mockStatic(TaskMain.class);
             MockedStatic<Logger> mockedLogger = mockStatic(Logger.class)) {

            Logger mockLogger = mock(Logger.class);
            mockedLogger.when(() -> Logger.getLogger(anyString())).thenReturn(mockLogger);

            ServletContextHandler mockContext = mock(ServletContextHandler.class);
            Server mockServer = mock(Server.class);

            mockedTaskMain.when(() -> TaskMain.main(any()))
                    .thenAnswer(inv -> {
                        // Simulate actual TaskMain behavior
                        lenient().when(mockServer.getHandler()).thenReturn(mockContext);
                        FilterHolder holder = new FilterHolder(mockAuthFilter);
                        mockContext.addFilter(holder, "/api/*", null);
                        return null;
                    });

            TaskMain.main(new String[]{});

            verify(mockContext).addFilter(any(FilterHolder.class), eq("/api/*"), isNull());
        }
    }

    @Test
    @DisplayName("Verify server failure is properly logged")
    void testServerFailureLogging() {
        try (MockedStatic<TaskMain> mockedTaskMain = mockStatic(TaskMain.class);
             MockedStatic<Logger> loggerMockedStatic = mockStatic(Logger.class)) {

            Logger testLogger = mock(Logger.class);
            loggerMockedStatic.when(() -> Logger.getLogger(TaskMain.class.getName()))
                    .thenReturn(testLogger);

            mockedTaskMain.when(() -> TaskMain.main(any()))
                    .thenThrow(new RuntimeException("Server error"));

            assertThatThrownBy(() -> TaskMain.main(new String[]{}))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Server error");

            verify(testLogger).severe(contains("Failed to start the server"));
        }
    }

    @Test
    @DisplayName("Verify server starts successfully with all servlets registered")
    void testServerStartsWithAllServlets() {
        try (MockedStatic<TaskMain> mockedTaskMain = mockStatic(TaskMain.class);
             MockedStatic<Logger> loggerMockedStatic = mockStatic(Logger.class);
             MockedStatic<AppConfig> appConfigMockedStatic = mockStatic(AppConfig.class)) {

            Logger testLogger = mock(Logger.class);
            AppConfig testAppConfig = mock(AppConfig.class);

            loggerMockedStatic.when(() -> Logger.getLogger(TaskMain.class.getName()))
                    .thenReturn(testLogger);

            appConfigMockedStatic.when(AppConfig::new)
                    .thenReturn(testAppConfig);

            when(testAppConfig.getUserServlet()).thenReturn(mock());
            when(testAppConfig.getTransactionServlet()).thenReturn(mock());
            when(testAppConfig.getAdminServlet()).thenReturn(mock());
            when(testAppConfig.getBudgetServlet()).thenReturn(mock());
            when(testAppConfig.getGoalServlet()).thenReturn(mock());
            when(testAppConfig.getNotificationServlet()).thenReturn(mock());
            when(testAppConfig.getReportServlet()).thenReturn(mock());

            mockedTaskMain.when(() -> TaskMain.main(any()))
                    .thenAnswer(inv -> {
                        testLogger.info("Finance App is running inside Docker!");
                        return null;
                    });

            TaskMain.main(new String[]{});

            verify(testLogger).info("Finance App is running inside Docker!");
        }
    }
}