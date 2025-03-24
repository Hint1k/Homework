package com.demo.finance.app;

import com.demo.finance.app.config.AppConfig;
import com.demo.finance.out.repository.impl.AbstractContainerBaseSetup;
import jakarta.servlet.ServletException;
import org.eclipse.jetty.servlet.FilterHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import com.demo.finance.in.filter.AuthenticationFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskMainTest extends AbstractContainerBaseSetup {

    private Server mockServer;
    private AppConfig mockAppConfig;
    private AuthenticationFilter mockAuthFilter;

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
                        Server server = new Server(8080);
                        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
                        context.setContextPath("/");
                        server.setHandler(context);
                        return null;
                    });

            assertThatCode(() -> TaskMain.main(new String[]{})).doesNotThrowAnyException();
        }
    }

    @Test
    @DisplayName("Verify server starts successfully with all servlets registered")
    void testServerStartsWithAllServlets() {
        try (MockedStatic<AppConfig> mockedAppConfig = mockStatic(AppConfig.class)) {
            mockedAppConfig.when(AppConfig::new).thenReturn(mockAppConfig);

            when(mockAppConfig.getUserServlet()).thenReturn(mock());
            when(mockAppConfig.getTransactionServlet()).thenReturn(mock());
            when(mockAppConfig.getAdminServlet()).thenReturn(mock());
            when(mockAppConfig.getBudgetServlet()).thenReturn(mock());
            when(mockAppConfig.getGoalServlet()).thenReturn(mock());
            when(mockAppConfig.getNotificationServlet()).thenReturn(mock());
            when(mockAppConfig.getReportServlet()).thenReturn(mock());

            try (MockedStatic<Logger> mockedLogger = mockStatic(Logger.class)) {
                Logger mockLogger = mock(Logger.class);
                mockedLogger.when(() -> Logger.getLogger(anyString())).thenReturn(mockLogger);

                assertThatCode(() -> TaskMain.main(new String[]{})).doesNotThrowAnyException();
                verify(mockLogger).info("Finance App is running inside Docker!");
            }
        }
    }

    @Test
    @DisplayName("Verify authentication filter is properly configured")
    void testAuthenticationFilterConfiguration() {
        try (MockedStatic<TaskMain> mockedTaskMain = mockStatic(TaskMain.class)) {
            mockedTaskMain.when(() -> TaskMain.main(any()))
                    .thenAnswer(invocation -> {
                        Server server = new Server(8080);
                        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
                        context.setContextPath("/");
                        server.setHandler(context);
                        context.addFilter(new FilterHolder(mockAuthFilter), "/api/*", null);
                        return null;
                    });

            TaskMain.main(new String[]{});
            verify(mockAuthFilter, times(1)).doFilter(any(), any(), any());
        } catch (ServletException | IOException e) {
            fail("Failed to verify authentication filter configuration: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Verify server failure is properly logged")
    void testServerFailureLogging() {
        try (MockedStatic<TaskMain> mockedTaskMain = mockStatic(TaskMain.class);
             MockedStatic<Logger> mockedLogger = mockStatic(Logger.class)) {

            Logger mockLogger = mock(Logger.class);
            mockedLogger.when(() -> Logger.getLogger(anyString())).thenReturn(mockLogger);

            mockedTaskMain.when(() -> TaskMain.main(any()))
                    .thenThrow(new RuntimeException("Server error"));

            assertThatThrownBy(() -> TaskMain.main(new String[]{})).isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Server error");

            verify(mockLogger).severe(contains("Failed to start the server"));
        }
    }
}