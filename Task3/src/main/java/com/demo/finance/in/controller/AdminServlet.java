package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.service.AdminService;
import com.demo.finance.out.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The {@code AdminServlet} class handles incoming HTTP requests related to admin functionalities.
 * It provides endpoints for managing users, including viewing all users, updating roles, blocking/unblocking,
 * and deleting users.
 */
@WebServlet("/api/admin/users/*")
public class AdminServlet extends HttpServlet {

    private final AdminService adminService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    /**
     * Constructs an {@code AdminServlet} with the specified {@code AdminService}.
     */
    public AdminServlet(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Handles GET requests to retrieve all users.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/".equals(pathInfo)) {
            try {
                int page = Integer.parseInt(request.getParameter("page") != null
                        ? request.getParameter("page") : "1");
                int size = Integer.parseInt(request.getParameter("size") != null
                        ? request.getParameter("size") : "10");
                if (page <= 0 || size <= 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter()
                            .write("Invalid pagination parameters. Page and size must be positive integers.");
                    return;
                }
                int offset = (page - 1) * size;
                List<User> users = adminService.getPaginatedUsers(offset, size);
                int totalUsers = adminService.getTotalUserCount();
                int totalPages = (int) Math.ceil((double) totalUsers / size);
                List<UserDto> userDtos = users.stream()
                        .map(UserMapper.INSTANCE::toDto)
                        .collect(Collectors.toList());
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("data", userDtos);
                responseMap.put("metadata", Map.of(
                        "totalItems", totalUsers,
                        "totalPages", totalPages,
                        "currentPage", page,
                        "pageSize", size
                ));
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(responseMap));
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid pagination parameters.");
            }
        } else if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                Long userId = Long.parseLong(pathInfo.substring(1));
                User user = userService.getUser(userId);
                if (user != null) {
                    UserDto userDto = UserMapper.INSTANCE.toDto(user);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(userDto));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("User not found.");
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

    /**
     * Handles PUT requests to update a user's role.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                Long userId = Long.parseLong(pathInfo.substring(1));
                StringBuilder jsonBody = new StringBuilder();
                String line;
                try (BufferedReader reader = request.getReader()) {
                    while ((line = reader.readLine()) != null) {
                        jsonBody.append(line);
                    }
                }
                UserDto userDto = objectMapper.readValue(jsonBody.toString(), UserDto.class);
                userDto.setUserId(userId);
                User user = UserMapper.INSTANCE.toEntity(userDto);
                boolean success = adminService.updateUserRole(userId, user.getRole());
                if (success) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(userDto));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Failed to update role.");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid user ID.");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid JSON format.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }

    /**
     * Handles PATCH requests to block or unblock a user.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                Long userId = Long.parseLong(pathInfo.substring(1));
                StringBuilder jsonBody = new StringBuilder();
                String line;
                try (BufferedReader reader = request.getReader()) {
                    while ((line = reader.readLine()) != null) {
                        jsonBody.append(line);
                    }
                }
                UserDto userDto = objectMapper.readValue(jsonBody.toString(), UserDto.class);
                userDto.setUserId(userId);
                boolean success;
                if (userDto.isBlocked()) {
                    success = adminService.blockUser(userId);
                    if (success) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("User blocked successfully.");
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("Failed to block user.");
                    }
                } else {
                    success = adminService.unBlockUser(userId);
                    if (success) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("User unblocked successfully.");
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("Failed to unblock user.");
                    }
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid user ID.");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid JSON format.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }

    /**
     * Handles DELETE requests to delete a user.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                Long userId = Long.parseLong(pathInfo.substring(1));
                boolean success = adminService.deleteUser(userId);
                if (success) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("User deleted successfully.");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Failed to delete user.");
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