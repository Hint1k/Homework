package com.demo.finance.app;

import com.demo.finance.app.config.AppConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.logging.Logger;

public class TaskMain {

    private static final Logger log = Logger.getLogger(TaskMain.class.getName());

    public static void main(String[] args) {
        AppConfig appConfig = new AppConfig();

        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(appConfig.getUserServlet()), "/api/users/*");
        context.addServlet(new ServletHolder(appConfig.getTransactionServlet()), "/api/transactions/*");
        context.addServlet(new ServletHolder(appConfig.getAdminServlet()), "/api/admin/users/*");
        context.addServlet(new ServletHolder(appConfig.getBudgetServlet()), "/api/budget/*");
        context.addServlet(new ServletHolder(appConfig.getGoalServlet()), "/api/goals/*");
        context.addServlet(new ServletHolder(appConfig.getNotificationServlet()), "/api/notifications/*");
        context.addServlet(new ServletHolder(appConfig.getReportServlet()), "/api/reports/*");

        try {
            server.start();
            log.info("Finance App is running inside Docker!");
            server.join(); // Keep the server running
        } catch (Exception e) {
            log.severe("Failed to start the server: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}