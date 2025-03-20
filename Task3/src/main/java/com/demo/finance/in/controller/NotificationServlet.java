package com.demo.finance.in.controller;

import com.demo.finance.out.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * The {@code NotificationServlet} class handles incoming HTTP requests related to notifications.
 * It provides endpoints for retrieving budget and goal notifications for users.
 */
@WebServlet("/api/notifications/*")
public class NotificationServlet extends HttpServlet {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a {@code NotificationServlet} with the specified {@code NotificationService}.
     *
     * @param notificationService the {@code NotificationService} used for fetching notifications
     */
    public NotificationServlet(NotificationService notificationService) {
        this.notificationService = notificationService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Handles GET requests to retrieve a user's budget notification.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/budget".equals(pathInfo)) {
            try {
                Long userId = Long.parseLong(request.getParameter("userId"));
                String notification = notificationService.fetchBudgetNotification(userId);
                if (notification != null && !notification.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(Map.of("message", notification)));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("No budget notification found for the user.");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid user ID.");
            }
        }
        else if ("/goal".equals(pathInfo)) {
            try {
                Long userId = Long.parseLong(request.getParameter("userId"));
                String notification = notificationService.fetchGoalNotification(userId);
                if (notification != null && !notification.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(Map.of("message", notification)));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("No goal notification found for the user.");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid user ID.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }
}