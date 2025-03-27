package com.demo.finance.app;

import com.demo.finance.app.config.AppConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import java.util.logging.Logger;

public class TaskMain {

    private static final Logger log = Logger.getLogger(TaskMain.class.getName());

    public static void main(String[] args) {
        Server server = new Server(8080);

        // Create Spring context
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);

        // Configure Jetty to use Spring
        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setContextPath("/");
        contextHandler.addEventListener(new ContextLoaderListener(context));

        // Add Spring MVC DispatcherServlet
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        ServletHolder servletHolder = new ServletHolder(dispatcherServlet);
        contextHandler.addServlet(servletHolder, "/api/*");

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
