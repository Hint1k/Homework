package com.demo.finance.app;

import com.demo.finance.app.config.AppConfig;
import com.demo.finance.app.config.LiquibaseManager;
import com.demo.finance.app.config.SwaggerConfig;
import com.demo.finance.in.filter.AuthenticationFilter;
import com.demo.finance.in.filter.ExceptionHandlerFilter;
import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Slf4jRequestLogWriter;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.EnumSet;
import java.util.logging.Logger;

/**
 * The {@code TaskMain} class serves as the entry point for the Finance Application.
 * It initializes the Spring application context, configures the Jetty server, and starts the application.
 * This class also handles Liquibase migrations, filter registration, and request logging configuration.
 */
public class TaskMain {

    private static final Logger log = Logger.getLogger(TaskMain.class.getName());

    /**
     * The main method initializes and starts the Finance Application.
     * <p>
     * This method performs the following steps:
     * <ol>
     *   <li>Initializes the Spring application context with the required configuration classes.</li>
     *   <li>Sets up the Jetty server with a ServletContextHandler and registers the Spring DispatcherServlet.</li>
     *   <li>Runs Liquibase migrations to apply database schema changes.</li>
     *   <li>Registers filters for exception handling and authentication.</li>
     *   <li>Configures request logging for the Jetty server.</li>
     *   <li>Starts the Jetty server on port 8080 and logs the application status.</li>
     * </ol>
     *
     * @param args command-line arguments (not used in this application)
     */
    public static void main(String[] args) {
        log.info("Starting Finance Application...");

        // Initialize Spring context
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class, SwaggerConfig.class);

        // Create the Jetty context handler
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath("/");

        // Set ServletContext
        context.setServletContext(contextHandler.getServletContext());
        context.refresh();

        // Run Liquibase migrations
        context.getBean(LiquibaseManager.class).runMigrations();

        // Register Spring DispatcherServlet
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
        contextHandler.addServlet(servletHolder, "/*");

        // Register filters manually in Jetty
        registerFilters(context, contextHandler);

        // Configure server
        Server server = new Server(8080);
        server.setHandler(contextHandler);
        configureRequestLogging(server);

        try {
            server.start();
            log.info("Finance App is running inside Docker with Spring!");

            // Prevent blocking in tests
            if (System.getProperty("TEST_ENV") == null) {
                server.join();
            }
        } catch (Exception e) {
            log.severe("Failed to start the server: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers filters for the Jetty server.
     * <p>
     * This method adds the {@link ExceptionHandlerFilter} and {@link AuthenticationFilter} to the provided
     * {@link ServletContextHandler}. The filters are applied to all requests and errors as specified.
     *
     * @param context        the Spring application context containing the filter beans
     * @param contextHandler the Jetty {@link ServletContextHandler} to which the filters are added
     */
    private static void registerFilters(AnnotationConfigWebApplicationContext context,
                                        ServletContextHandler contextHandler) {
        contextHandler.addFilter(
                new FilterHolder(context.getBean(ExceptionHandlerFilter.class)),
                "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR));
        contextHandler.addFilter(
                new FilterHolder(context.getBean(AuthenticationFilter.class)),
                "/*", EnumSet.of(DispatcherType.REQUEST));
    }

    /**
     * Configures request logging for the Jetty server.
     * <p>
     * This method sets up a custom request log format and associates it with an SLF4J logger. The log format includes
     * details such as the client IP address, request method, response status, and user agent.
     *
     * @param server the Jetty {@link Server} instance to configure request logging for
     */
    private static void configureRequestLogging(Server server) {
        Slf4jRequestLogWriter logWriter = new Slf4jRequestLogWriter();
        logWriter.setLoggerName("org.eclipse.jetty.server.RequestLog");
        String logFormat = "%{client}a - %u %t '%r' %s %O '%{Referer}i' '%{User-Agent}i' '%C'";
        server.setRequestLog(new CustomRequestLog(logWriter, logFormat));
    }
}