package com.demo.finance.app;

import com.demo.finance.app.config.AppConfig;
import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.in.filter.AuthenticationFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.logging.Logger;

/**
 * The {@code TaskMain} class serves as the entry point for the Finance App.
 * It initializes and configures the Jetty server, setting up servlets and filters
 * to handle API requests. This class ensures that the application runs inside a Docker container
 * and provides logging for server startup and runtime errors.
 */
public class TaskMain {

    /**
     * Logger instance for logging events and errors in the {@code TaskMain} class.
     */
    private static final Logger log = Logger.getLogger(TaskMain.class.getName());

    /**
     * The main method initializes the Jetty server, configures servlets and filters,
     * and starts the server to handle incoming HTTP requests.
     * The server listens on port 8080 and supports session management.
     * If the server fails to start, an exception is thrown, and the error is logged.
     *
     * @param args command-line arguments (not used in this implementation)
     */
    public static void main(String[] args) {
        AppConfig appConfig = new AppConfig();

        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.setSessionHandler(new SessionHandler());

        // Add the AuthenticationFilter to secure API endpoints
        context.addFilter(new FilterHolder(new AuthenticationFilter()), "/api/*", null);

        // Register servlets for various API endpoints
        context.addServlet(new ServletHolder(appConfig.getUserServlet()), "/api/users/*");
        context.addServlet(new ServletHolder(appConfig.getTransactionServlet()), "/api/transactions/*");
        context.addServlet(new ServletHolder(appConfig.getAdminServlet()), "/api/admin/users/*");
        context.addServlet(new ServletHolder(appConfig.getBudgetServlet()), "/api/budgets/*");
        context.addServlet(new ServletHolder(appConfig.getGoalServlet()), "/api/goals/*");
        context.addServlet(new ServletHolder(appConfig.getNotificationServlet()), "/api/notifications/*");
        context.addServlet(new ServletHolder(appConfig.getReportServlet()), "/api/reports/*");

        try {
            DataSourceManager.getConnection().close();

            server.start();
            log.info("Finance App is running inside Docker!");

            // Prevent blocking in tests
            if (System.getProperty("TEST_ENV") == null) {
                server.join(); // Keep running only in production
            }
        } catch (Exception e) {
            log.severe("Failed to start the server: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}