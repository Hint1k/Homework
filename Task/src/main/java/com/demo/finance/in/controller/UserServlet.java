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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * The {@code UserServlet} class is a servlet that handles HTTP requests related to user operations,
 * such as registration, authentication, account updates, and deletion. It validates incoming JSON data,
 * interacts with services for business logic, and returns appropriate responses.
 */
@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {

    private final RegistrationService registrationService;
    private final UserService userService;
    private final ValidationUtils validationUtils;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new instance of {@code UserServlet} with the required dependencies.
     *
     * @param registrationService the service responsible for user registration and authentication
     * @param userService          the service responsible for user-related operations
     * @param validationUtils      the utility for validating incoming JSON data
     * @param objectMapper         the object mapper for JSON serialization and deserialization
     */
    public UserServlet(RegistrationService registrationService, UserService userService,
                       ValidationUtils validationUtils, ObjectMapper objectMapper) {
        this.registrationService = registrationService;
        this.userService = userService;
        this.validationUtils = validationUtils;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Handles POST requests for user registration and authentication.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
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
                        sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                "Failed to retrieve user details.");
                    }
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                            "Failed to register user.");
                }
            } catch (ValidationException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
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
                        sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                "Failed to retrieve user details.");
                    }
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                            "Invalid credentials.");
                }
            } catch (Exception e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid request parameters.");
            }
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    /**
     * Handles GET requests to retrieve the currently authenticated user's details.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
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
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "No user is currently logged in.");
            }
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    /**
     * Handles PUT requests for updating the authenticated user's account details.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
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
                    sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Failed to retrieve updated user details.");
                }
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Failed to update account.");
            }
        } catch (ValidationException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Handles DELETE requests for deleting the authenticated user's account.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
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
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Failed to delete account.");
            }
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An error occurred while deleting the account.");
        }
    }

    /**
     * Reads and returns the body of the HTTP request as a JSON string.
     *
     * @param request the HTTP servlet request
     * @return the JSON string from the request body
     * @throws IOException if an I/O error occurs while reading the request body
     */
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

    /**
     * Sends an error response with the specified status code and error message.
     *
     * @param response     the HTTP servlet response
     * @param statusCode   the HTTP status code to set in the response
     * @param errorMessage the error message to include in the response body
     * @throws IOException if an I/O error occurs while writing the response
     */
    private void sendErrorResponse(HttpServletResponse response, int statusCode, String errorMessage)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        Map<String, String> errorResponse = Map.of("error", errorMessage);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}