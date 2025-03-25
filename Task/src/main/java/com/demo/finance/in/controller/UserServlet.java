package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.DuplicateEmailException;
import com.demo.finance.exception.ValidationException;
import com.demo.finance.out.service.RegistrationService;
import com.demo.finance.out.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * The {@code UserServlet} class is a servlet that handles HTTP requests related to user operations,
 * such as registration, authentication, account updates, and deletion. It extends the {@code BaseServlet}
 * to reuse common functionality.
 */
@WebServlet("/api/users/*")
public class UserServlet extends BaseServlet {

    private final RegistrationService registrationService;
    private final UserService userService;

    /**
     * Constructs a new instance of {@code UserServlet} with the required dependencies.
     *
     * @param registrationService the service responsible for user registration and authentication
     * @param userService         the service responsible for user-related operations
     * @param validationUtils     the utility for validating incoming JSON data
     * @param objectMapper        the object mapper for JSON serialization and deserialization
     */
    public UserServlet(RegistrationService registrationService, UserService userService,
                       ValidationUtils validationUtils, ObjectMapper objectMapper) {
        super(validationUtils, objectMapper);
        this.registrationService = registrationService;
        this.userService = userService;
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
            handleRegistration(request, response);
        } else if ("/authenticate".equals(pathInfo)) {
            handleAuthentication(request, response);
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
            handleGetCurrentUser(request, response);
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
        handleUpdateUser(request, response);
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
        handleDeleteUser(request, response);
    }

    /**
     * Handles user registration requests.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleRegistration(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String json = readRequestBody(request);
            UserDto userDto = validationUtils.validateUserJson(json, Mode.REGISTER);
            boolean success = registrationService.registerUser(userDto);
            if (success) {
                User user = userService.getUserByEmail(userDto.getEmail());
                if (user != null) {
                    UserDto registeredUserDto = UserDto.removePassword(UserMapper.INSTANCE.toDto(user));
                    sendSuccessResponse(response, HttpServletResponse.SC_CREATED,
                            "User registered successfully", registeredUserDto);
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Failed to retrieve user details.");
                }
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Failed to register user.");
            }
        } catch (DuplicateEmailException | ValidationException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Handles user authentication requests.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                    sendSuccessResponse(response, HttpServletResponse.SC_OK,
                            "Authentication successful", authUserDto);
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Failed to retrieve user details.");
                }
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid credentials.");
            }
        } catch (ValidationException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Handles requests to retrieve the currently authenticated user's details.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleGetCurrentUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
        if (userDto != null) {
            sendSuccessResponse(response, HttpServletResponse.SC_OK, "Authenticated user details", userDto);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "No user is currently logged in.");
        }
    }

    /**
     * Handles requests to update the authenticated user's account details.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleUpdateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                    sendSuccessResponse(response, HttpServletResponse.SC_OK,
                            "User updated successfully", updatedUserDto);
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
     * Handles requests to delete the authenticated user's account.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            HttpSession session = request.getSession(false);
            UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
            User currentUser = UserMapper.INSTANCE.toEntity(userDto);
            boolean success = userService.deleteOwnAccount(currentUser.getUserId());
            if (success) {
                session.invalidate();
                sendSuccessResponse(response, HttpServletResponse.SC_OK,
                        "Account deleted successfully", currentUser.getEmail());
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Failed to delete account.");
            }
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An error occurred while deleting the account.");
        }
    }
}