package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.service.RegistrationService;
import com.demo.finance.out.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.io.BufferedReader;

/**
 * The {@code UserServlet} class handles incoming HTTP requests related to user registration and account management.
 * It provides endpoints for registering, authenticating, updating, and deleting user accounts.
 */
@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {

    private final RegistrationService registrationService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a {@code UserServlet} with the specified {@code RegistrationService} and {@code UserService}.
     */
    public UserServlet(RegistrationService registrationService, UserService userService) {
        this.registrationService = registrationService;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Handles POST requests to register a new user.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/".equals(pathInfo)) {
            try {
                StringBuilder jsonBody = new StringBuilder();
                String line;
                try (BufferedReader reader = request.getReader()) {
                    while ((line = reader.readLine()) != null) {
                        jsonBody.append(line);
                    }
                }
                @SuppressWarnings("unchecked")
                Map<String, Object> requestBody = objectMapper.readValue(jsonBody.toString(), Map.class);
                String password = (String) requestBody.get("password");
                if (password == null || password.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Password is required.");
                    return;
                }
                String userJson = objectMapper.writeValueAsString(requestBody);
                UserDto userDto = objectMapper.readValue(userJson, UserDto.class);
                User user = UserMapper.INSTANCE.toEntity(userDto);
                user.setPassword(password);
                boolean success = registrationService.registerUser(
                        user.getName(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getRole()
                );
                if (success) {
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(userDto));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Failed to register user.");
                }
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
     * Handles GET requests to authenticate a user.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/authenticate".equals(pathInfo)) {
            try {
                String email = request.getParameter("email");
                String password = request.getParameter("password");

                Optional<User> userOptional = registrationService.authenticate(email, password);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    UserDto userDto = UserMapper.INSTANCE.toDto(user);

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(userDto));
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Authentication failed.");
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid request parameters.");
            }
        } else if ("/me".equals(pathInfo)) {
            User currentUser = (User) request.getSession().getAttribute("currentUser");
            if (currentUser != null) {
                UserDto userDto = UserMapper.INSTANCE.toDto(currentUser);
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(userDto));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("No user is currently logged in.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }

    /**
     * Handles PUT requests to update a user's account details.
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
                boolean success = userService.updateOwnAccount(
                        user.getUserId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getRole(),
                        user.getVersion(),
                        Boolean.parseBoolean(request.getParameter("isPasswordUpdated"))
                );
                if (success) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(userDto));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Failed to update account.");
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
     * Handles DELETE requests to delete a user's account.
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
                boolean success = userService.deleteOwnAccount(userId);
                if (success) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Account deleted successfully.");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Failed to delete account.");
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