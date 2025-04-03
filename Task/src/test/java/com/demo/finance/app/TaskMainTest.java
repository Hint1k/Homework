package com.demo.finance.app;

import com.demo.finance.app.config.AppConfig;
import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.app.config.DatabaseConfig;
import com.demo.finance.in.filter.AuthenticationFilter;
import com.demo.finance.in.filter.ExceptionHandlerFilter;
import com.demo.finance.out.repository.impl.AbstractContainerBaseSetup;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletException;
import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Slf4jRequestLogWriter;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

@TestInstance(Lifecycle.PER_CLASS)
public class TaskMainTest extends AbstractContainerBaseSetup {

    private int testPort = 8080;
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

    private void startServer() {
       try {
           // Initialize Spring context
           AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
           context.register(AppConfig.class, SwaggerConfig.class);

           // Create Jetty context handler
           ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
           contextHandler.setContextPath("/");

           // Set ServletContext and refresh
           context.setServletContext(contextHandler.getServletContext());
           context.refresh();

           // Register Spring DispatcherServlet
           DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
           ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
           contextHandler.addServlet(servletHolder, "/*");

           // Register filters
           registerFilters(context, contextHandler);

           // Configure server
           Server server = new Server(testPort);
           server.setHandler(contextHandler);
           configureRequestLogging(server);

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
       } catch (Exception e) {
           fail(e.getMessage());
       }
    }

    private void registerFilters(AnnotationConfigWebApplicationContext context,
                                 ServletContextHandler contextHandler) {
        contextHandler.addFilter(
                new FilterHolder(context.getBean(ExceptionHandlerFilter.class)),
                "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR));
        contextHandler.addFilter(
                new FilterHolder(context.getBean(AuthenticationFilter.class)),
                "/*", EnumSet.of(DispatcherType.REQUEST));
    }

    private void configureRequestLogging(Server server) {
        Slf4jRequestLogWriter logWriter = new Slf4jRequestLogWriter();
        logWriter.setLoggerName("org.eclipse.jetty.server.RequestLog");
        String logFormat = "%{client}a - %u %t '%r' %s %O '%{Referer}i' '%{User-Agent}i' '%C'";
        server.setRequestLog(new CustomRequestLog(logWriter, logFormat));
    }

    @Test
    @DisplayName("Verify server starts successfully with all components")
    void testServerStartup() {
        try {
            startServer();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:" + testPort + "/api/users/health"))
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
                                    .uri(URI.create("http://localhost:" + testPort + "/api/users"))
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
                .hasMessageContaining("java.sql.SQLException")
                .hasRootCauseInstanceOf(SQLException.class)
                .hasRootCauseMessage("No suitable driver found for invalid_url");

        System.setProperty("DB_URL", originalDbUrl);
        System.clearProperty("TEST_ENV");
    }

    @Test
    @DisplayName("Verify database connection through DataSourceManager")
    void testDatabaseConnection() {
        try {
            DatabaseConfig databaseConfig = new DatabaseConfig();
            databaseConfig.init();
            DataSourceManager dataSourceManager = new DataSourceManager(databaseConfig);

            try (Connection conn = dataSourceManager.getConnection()) {
                assertThat(conn.isValid(1)).isTrue();

                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(
                             "SELECT table_name FROM information_schema.tables " +
                                     "WHERE table_schema = 'finance'")) {

                    List<String> tables = new ArrayList<>();
                    while (rs.next()) {
                        tables.add(rs.getString(1));
                    }

                    assertThat(tables)
                            .contains("users", "transactions", "budgets", "goals")
                            .doesNotHaveDuplicates();
                }
            }
        } catch (SQLException e) {
            fail("Database connection failed", e);
        }
    }

    @Test
    @DisplayName("Verify DispatcherServlet is properly configured")
    void testDispatcherServletConfiguration() {
        int originalPort = this.testPort;
        this.testPort = 8081;
        try {
            startServer();
            ServletContextHandler context = (ServletContextHandler) serverRef.get().getHandler();
            ServletHolder[] servletHolders = context.getServletHandler().getServlets();
            Optional<ServletHolder> dispatcherHolder = Arrays.stream(servletHolders)
                    .filter(holder -> {
                        try {
                            return holder.getServlet() instanceof DispatcherServlet;
                        } catch (ServletException e) {
                            fail(e.getMessage());
                            return false;
                        }
                    }).findFirst();

            assertThat(dispatcherHolder)
                    .isPresent()
                    .get()
                    .satisfies(holder -> {
                        assertThat(holder.getName()).contains("org.springframework.web.servlet.DispatcherServlet");
                        assertThat(holder.getServlet()).isInstanceOf(DispatcherServlet.class);
                    });
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            stopServer();
            this.testPort = originalPort;
        }
    }

    @Test
    @DisplayName("Verify filters are properly registered")
    void testFilterRegistration() {
        try {
            startServer();

            ServletContextHandler context = (ServletContextHandler) serverRef.get().getHandler();
            FilterHolder[] filterHolders = context.getServletHandler().getFilters();

            assertThat(filterHolders).hasSize(2);
            assertThat(filterHolders[0].getName()).contains("ExceptionHandlerFilter");
            assertThat(filterHolders[1].getName()).contains("AuthenticationFilter");
        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        } finally {
            stopServer();
        }
    }

    @Test
    @DisplayName("Verify server port configuration through observable behavior")
    void testServerPortConfiguration() throws Exception {
        Server testServer = new Server(0);
        try {
            testServer.start();
            int actualPort = testServer.getURI().getPort();

            assertThat(actualPort).isNotEqualTo(-1);
        } finally {
            testServer.stop();
        }
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