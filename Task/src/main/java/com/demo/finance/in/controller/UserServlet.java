package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.ValidationException;
import com.demo.finance.out.service.RegistrationService;
import com.demo.finance.out.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {

    private final RegistrationService registrationService;
    private final UserService userService;
    private final ValidationUtils validationUtils;
    private final ObjectMapper objectMapper;

    public UserServlet(RegistrationService registrationService, UserService userService,
                       ValidationUtils validationUtils, ObjectMapper objectMapper) {
        this.registrationService = registrationService;
        this.userService = userService;
        this.validationUtils = validationUtils;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/registration".equals(pathInfo)) {
            try {
                String json = readRequestBody(request);
                UserDto userDto = validationUtils.validateUserJson(json, Mode.REGISTER);
                boolean success = registrationService.registerUser(userDto);
                if (success) {
                    User user = userService.getUserByEmail(userDto.getEmail());
                    if (user != null) {
                        UserDto registeredUserDto = UserDto.removePassword(UserMapper.INSTANCE.toDto(user));
                        Map<String, Object> responseBody = Map.of(
                                "message", "User registered successfully",
                                "data", registeredUserDto,
                                "timestamp", java.time.Instant.now().toString()
                        );
                        response.setStatus(HttpServletResponse.SC_CREATED);
                        response.setContentType("application/json");
                        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        response.getWriter().write("Failed to retrieve user details.");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Failed to register user.");
                }
            } catch (ValidationException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(e.getMessage());
            }
        } else if ("/authenticate".equals(pathInfo)) {
            try {
                String json = readRequestBody(request);
                UserDto userDto = validationUtils.validateUserJson(json, Mode.AUTHENTICATE);
                boolean success = registrationService.authenticate(userDto);
                if (success) {
                    User user = userService.getUserByEmail(userDto.getEmail());
                    if (user != null) {
                        UserDto authUserDto = UserDto.removePassword(UserMapper.INSTANCE.toDto(user));
                        HttpSession session = request.getSession();
                        session.setAttribute("currentUser", authUserDto);
                        Map<String, Object> responseBody = Map.of(
                                "message", "Authentication successful",
                                "data", authUserDto,
                                "timestamp", java.time.Instant.now().toString()
                        );
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setContentType("application/json");
                        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        response.getWriter().write("Failed to retrieve user details.");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid credentials.");
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                        "message", "Invalid request parameters.",
                        "timestamp", java.time.Instant.now().toString()
                )));
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/me".equals(pathInfo)) {
            UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
            if (userDto != null) {
                Map<String, Object> responseBody = Map.of(
                        "message", "Authenticated user details",
                        "data", userDto,
                        "timestamp", java.time.Instant.now().toString()
                );
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(responseBody));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                        "message", "No user is currently logged in.",
                        "timestamp", java.time.Instant.now().toString()
                )));
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            UserDto userDtoInSession = (UserDto) request.getSession().getAttribute("currentUser");
            Long userId = userDtoInSession.getUserId();
            String json = readRequestBody(request);
            UserDto userDto = validationUtils.validateUserJson(json, Mode.UPDATE, userId);
            boolean success = userService.updateOwnAccount(userDto);
            if (success) {
                User updatedUser = userService.getUserByEmail(userDto.getEmail());
                if (updatedUser != null) {
                    UserDto updatedUserDto = UserDto.removePassword(UserMapper.INSTANCE.toDto(updatedUser));
                    HttpSession session = request.getSession(false);
                    if (session != null) {
                        session.setAttribute("currentUser", updatedUserDto);
                    }
                    Map<String, Object> responseBody = Map.of(
                            "message", "User updated successfully",
                            "data", updatedUserDto,
                            "timestamp", java.time.Instant.now().toString()
                    );
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Failed to retrieve updated user details.");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Failed to update account.");
            }
        } catch (ValidationException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            HttpSession session = request.getSession(false);
            UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
            User currentUser = UserMapper.INSTANCE.toEntity(userDto);
            boolean success = userService.deleteOwnAccount(currentUser.getUserId());
            if (success) {
                session.invalidate();
                Map<String, Object> responseBody = Map.of(
                        "message", "Account deleted successfully",
                        "email", currentUser.getEmail(),
                        "timestamp", java.time.Instant.now().toString()
                );
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(responseBody));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Failed to delete account.");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred while deleting the account.");
        }
    }

    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder json = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
        }
        return json.toString();
    }
}