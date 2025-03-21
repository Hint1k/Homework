package com.demo.finance.in.controller;

import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidatedUser;
import com.demo.finance.domain.utils.impl.ValidationUtilsImpl;
import com.demo.finance.out.service.RegistrationService;
import com.demo.finance.out.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {
    private final RegistrationService registrationService;
    private final UserService userService;
    private final ValidationUtilsImpl validationUtils;
    private final ObjectMapper objectMapper;

    public UserServlet(RegistrationService registrationService, UserService userService) {
        this.registrationService = registrationService;
        this.userService = userService;
        this.validationUtils = new ValidationUtilsImpl();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/".equals(pathInfo)) {
            try {
                String json = readRequestBody(request);
                ValidatedUser validatedUser = validationUtils.validateUserJson(json, Mode.CREATE);
                boolean success = registrationService.registerUser(validatedUser);
                if (success) {
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(validatedUser.userDto()));
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
                ValidatedUser validatedUser = validationUtils.validateUserJson(json, Mode.AUTHENTICATE);
                boolean success = registrationService.authenticate(validatedUser);
                if (success) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(validatedUser.userDto()));
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Authentication failed.");
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid request parameters.");
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
            User currentUser = (User) request.getSession().getAttribute("currentUser");
            if (currentUser != null) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(UserMapper.INSTANCE.toDto(currentUser)));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("No user is currently logged in.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String json = readRequestBody(request);
            ValidatedUser validatedUser = validationUtils.validateUserJson(json, Mode.UPDATE);
            boolean success = userService.updateOwnAccount(validatedUser);
            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(validatedUser.userDto()));
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
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("No user is currently logged in.");
            return;
        }
        try {
            boolean success = userService.deleteOwnAccount(currentUser.getUserId());
            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Account deleted successfully.");
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