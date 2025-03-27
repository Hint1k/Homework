package com.demo.finance.app;

import com.demo.finance.app.config.AppConfig;
import com.demo.finance.app.config.LiquibaseManager;
import com.demo.finance.in.filter.AuthenticationFilter;
import com.demo.finance.in.filter.ExceptionHandlerFilter;
import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.EnumSet;
import java.util.logging.Logger;

public class TaskMain {
    private static final Logger log = Logger.getLogger(TaskMain.class.getName());

    public static void main(String[] args) {
        log.info("Starting Finance Application...");

        // Initialize Spring context
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);

        // Create the Jetty context handler
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath("/");

        // Set ServletContext
        context.setServletContext(contextHandler.getServletContext());
        context.refresh();

        // Run Liquibase migrations
        context.getBean(LiquibaseManager.class).runMigrations();

        // Start Jetty server
        startJettyServer(context, contextHandler);
    }

    private static void startJettyServer(AnnotationConfigWebApplicationContext context,
                                         ServletContextHandler contextHandler) {
        Server server = new Server(8080);
        contextHandler.addEventListener(new ContextLoaderListener(context));

        // Register Spring DispatcherServlet
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
        contextHandler.addServlet(servletHolder, "/*");

        // Register filters manually in Jetty
        contextHandler.addFilter(new FilterHolder(context.getBean(ExceptionHandlerFilter.class)), "/*",
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR));
        contextHandler.addFilter(new FilterHolder(context.getBean(AuthenticationFilter.class)), "/*",
                EnumSet.of(DispatcherType.REQUEST));

        server.setHandler(contextHandler);

        try {
            server.start();
            log.info("Finance App is running inside Docker with Spring!");
            server.join();
        } catch (Exception e) {
            log.severe("Failed to start the server: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}