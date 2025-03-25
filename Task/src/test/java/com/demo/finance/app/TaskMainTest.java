package com.demo.finance.app;

import com.demo.finance.app.config.AppConfig;
import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.in.filter.AuthenticationFilter;
import com.demo.finance.in.filter.ExceptionHandlerFilter;
import com.demo.finance.out.repository.impl.AbstractContainerBaseSetup;
import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

@TestInstance(Lifecycle.PER_CLASS)
public class TaskMainTest extends AbstractContainerBaseSetup {

    private static final int TEST_PORT = 8080;
    private final AtomicReference<Server> serverRef = new AtomicReference<>();

    @BeforeEach
    void setUp() {
        stopServer();
    }

    @AfterEach
    void tearDown() {
        stopServer();
    }

    private void stopServer() {
        try {
            Server server = serverRef.get();
            if (server != null && server.isStarted()) {
                server.stop();
            }
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            serverRef.set(null);
        }
    }

    private void startServer() throws InterruptedException {
            Server server = new Server(TEST_PORT);
            configureServer(server);
            serverRef.set(server);
            new Thread(() -> {
                try {
                    server.start();
                    server.join();
                } catch (Exception e) {
                    throw new RuntimeException("Server failed to start", e);
                }
            }).start();
            Thread.sleep(1000);
    }

    private void configureServer(Server server) {
        AppConfig appConfig = new AppConfig();
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.setSessionHandler(new SessionHandler());
        context.addFilter(new FilterHolder(new ExceptionHandlerFilter()),
                "/*", EnumSet.of(DispatcherType.REQUEST));
        context.addFilter(new FilterHolder(new AuthenticationFilter()), "/api/*", null);
        context.addServlet(new ServletHolder(appConfig.getUserServlet()), "/api/users/*");
        context.addServlet(new ServletHolder(appConfig.getTransactionServlet()), "/api/transactions/*");
        context.addServlet(new ServletHolder(appConfig.getAdminServlet()), "/api/admin/users/*");
        context.addServlet(new ServletHolder(appConfig.getBudgetServlet()), "/api/budgets/*");
        context.addServlet(new ServletHolder(appConfig.getGoalServlet()), "/api/goals/*");
        context.addServlet(new ServletHolder(appConfig.getNotificationServlet()), "/api/notifications/*");
        context.addServlet(new ServletHolder(appConfig.getReportServlet()), "/api/reports/*");
    }

    @Test
    @DisplayName("Verify server starts successfully with all components")
    void testServerStartup() {
        try {
            startServer();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:" + TEST_PORT + "/api/users/health"))
                                    .GET()
                                    .build(),
                            HttpResponse.BodyHandlers.ofString());

            assertThat(response.statusCode()).isNotEqualTo(404);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            stopServer();
        }
    }

    @Test
    @DisplayName("Verify authentication filter is properly configured")
    void testAuthenticationFilter() {
        try {
            startServer();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:" + TEST_PORT + "/api/users"))
                                    .GET()
                                    .build(),
                            HttpResponse.BodyHandlers.ofString());

            assertThat(response.statusCode()).isEqualTo(401);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            stopServer();
        }
    }

    @Test
    @DisplayName("Verify server shutdown on error")
    void testServerFailure() {
        String originalDbUrl = System.getProperty("DB_URL");
        System.setProperty("DB_URL", "invalid_url");

        assertThatThrownBy(() -> {
            System.setProperty("TEST_ENV", "true");
            TaskMain.main(new String[]{});
        }).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("java.sql.SQLException");

        System.setProperty("DB_URL", originalDbUrl);
        System.clearProperty("TEST_ENV");
    }

    @Test
    @DisplayName("Verify database connection through AppConfig")
    void testDatabaseConnection() {
        try (Connection conn = DataSourceManager.getConnection()) {

            assertThat(conn.isValid(1)).isTrue();

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                         "SELECT table_name FROM information_schema.tables " +
                                 "WHERE table_schema = 'finance'")) {
                List<String> tables = new ArrayList<>();
                while (rs.next()) {
                    tables.add(rs.getString(1));
                }

                assertThat(tables).contains("users", "transactions", "budgets", "goals").doesNotHaveDuplicates();
            }
        } catch (SQLException e) {
            fail("Database connection failed", e);
        } finally {
            stopServer();
        }
    }

    @Test
    @DisplayName("Verify server starts with correct configuration")
    void testTaskMainExecution() {
        Server server = new Server(TEST_PORT);
        AppConfig appConfig = new AppConfig();
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.setSessionHandler(new SessionHandler());
        context.addFilter(new FilterHolder(new ExceptionHandlerFilter()),
                "/*", EnumSet.of(DispatcherType.REQUEST));
        context.addFilter(new FilterHolder(new AuthenticationFilter()), "/api/*", null);
        context.addServlet(new ServletHolder(appConfig.getUserServlet()), "/api/users/*");
        context.addServlet(new ServletHolder(appConfig.getTransactionServlet()), "/api/transactions/*");
        context.addServlet(new ServletHolder(appConfig.getAdminServlet()), "/api/admin/users/*");
        context.addServlet(new ServletHolder(appConfig.getBudgetServlet()), "/api/budgets/*");
        context.addServlet(new ServletHolder(appConfig.getGoalServlet()), "/api/goals/*");
        context.addServlet(new ServletHolder(appConfig.getNotificationServlet()), "/api/notifications/*");
        context.addServlet(new ServletHolder(appConfig.getReportServlet()), "/api/reports/*");
        ServletHolder[] servletHolders = context.getServletHandler().getServlets();

        List<String> actualServletNames = Arrays.stream(servletHolders)
                .map(servlet -> servlet.getName().split("-")[0])
                .toList();

        List<String> expectedServletNames = List.of(
                "com.demo.finance.in.controller.UserServlet",
                "com.demo.finance.in.controller.TransactionServlet",
                "com.demo.finance.in.controller.AdminServlet",
                "com.demo.finance.in.controller.BudgetServlet",
                "com.demo.finance.in.controller.GoalServlet",
                "com.demo.finance.in.controller.NotificationServlet",
                "com.demo.finance.in.controller.ReportServlet"
        );

        assertThat(actualServletNames).containsExactlyInAnyOrderElementsOf(expectedServletNames);
        stopServer();
    }

    @Test
    @DisplayName("To include this class in JaCoCo coverage report")
    void testMainMethodCoverage() {
        System.setProperty("TEST_ENV", "true");
        assertThatCode(() -> TaskMain.main(new String[]{}))
                .doesNotThrowAnyException();
        System.clearProperty("TEST_ENV");
    }
}