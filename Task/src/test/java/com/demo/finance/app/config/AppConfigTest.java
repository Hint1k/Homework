package com.demo.finance.app.config;

import com.demo.finance.out.repository.impl.AbstractContainerBaseSetup;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.Slf4jRequestLogWriter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppConfigTest extends AbstractContainerBaseSetup {

    private final int testPort = 8080;
    private Server server;

    @Configuration
    @ComponentScan(basePackages = {"com.demo.finance.app.config"}, excludeFilters = @ComponentScan.Filter(
            type = FilterType.REGEX, pattern = "com\\.demo\\.finance\\..*Controller"))
    static class TestConfig {
    }

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
            if (server != null && server.isStarted()) {
                server.stop();
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void startServer() {
        try {
            AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
            context.register(TestConfig.class);
            ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            contextHandler.setContextPath("/");
            context.setServletContext(contextHandler.getServletContext());
            context.refresh();
            DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
            ServletHolder servletHolder = new ServletHolder("dispatcher", dispatcherServlet);
            contextHandler.addServlet(servletHolder, "/*");
            enableCors(contextHandler);
            server = new Server(testPort);
            server.setHandler(contextHandler);
            configureRequestLogging(server);
            server.start();
        } catch (Exception e) {
            fail("Failed to start server: " + e.getMessage(), e);
        }
    }

    private void configureRequestLogging(Server server) {
        Slf4jRequestLogWriter logWriter = new Slf4jRequestLogWriter();
        logWriter.setLoggerName("org.eclipse.jetty.server.RequestLog");
        String logFormat = "%{client}a - %u %t '%r' %s %O '%{Referer}i' '%{User-Agent}i' '%C'";
        server.setRequestLog(new CustomRequestLog(logWriter, logFormat));
    }

    private void enableCors(ServletContextHandler contextHandler) {
        FilterHolder corsFilter = new FilterHolder(CrossOriginFilter.class);
        corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "http://localhost:8080");
        corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,PUT,DELETE,OPTIONS");
        corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "*");
        corsFilter.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
        contextHandler.addFilter(corsFilter, "/*", null);
    }

    @Test
    @DisplayName("Verify CORS allowed on authenticate endpoint")
    void testAuthenticateEndpointCors() {
        startServer();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:" + testPort + "/api/users/authenticate"))
                                    .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                                    .header("Origin", "http://localhost:8080")
                                    .header("Access-Control-Request-Method", "POST")
                                    .build(),
                            HttpResponse.BodyHandlers.ofString());

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.headers().map())
                    .containsKey("access-control-allow-origin")
                    .containsKey("access-control-allow-methods");

            assertThat(response.headers().firstValue("access-control-allow-origin"))
                    .hasValue("http://localhost:8080");
            assertThat(response.headers().firstValue("access-control-allow-methods"))
                    .as("Check presence of 'Access-Control-Allow-Methods' header")
                    .isPresent()
                    .hasValueSatisfying(value -> assertThat(value).contains("POST"));
        } catch (Exception e) {
            fail("CORS test failed: " + e.getMessage());
        } finally {
            stopServer();
        }
    }

    @Test
    @DisplayName("Verify Swagger UI is accessible")
    void testSwaggerUI() {
        startServer();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:" + testPort + "/swagger-ui/index.html"))
                                    .GET()
                                    .build(),
                            HttpResponse.BodyHandlers.ofString());

            assertThat(response.statusCode()).isEqualTo(200);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            stopServer();
        }
    }

    @Test
    @DisplayName("Verify root path redirects to Swagger UI")
    void testRootRedirectToSwagger() {
        startServer();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:" + testPort + "/"))
                                    .GET()
                                    .build(),
                            HttpResponse.BodyHandlers.ofString());

            assertThat(response.statusCode()).isEqualTo(302);
            assertThat(response.headers().firstValue("Location"))
                    .hasValue("http://localhost:8080/swagger-ui.html");
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            stopServer();
        }
    }

    @Test
    @DisplayName("Verify invalid JSON returns 400 Bad Request")
    void testMalformedJsonErrorHandling() {
        startServer();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:" + testPort + "/api/users/authenticate"))
                                    .header("Content-Type", "application/json")
                                    .POST(HttpRequest.BodyPublishers.ofString("{ invalid json }"))
                                    .build(),
                            HttpResponse.BodyHandlers.ofString());

            assertThat(response.statusCode()).isEqualTo(400);
            assertThat(response.body()).contains("Invalid JSON format");
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            stopServer();
        }
    }
}