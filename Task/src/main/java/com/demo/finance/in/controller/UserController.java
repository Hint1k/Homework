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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import java.time.Instant;
import java.util.Map;

/**
 * The {@code UserController} class is a REST controller that provides endpoints for user management,
 * including registration, authentication, updating user details, logging out, and deleting accounts.
 * <p>
 * This controller leverages validation utilities to ensure that incoming requests meet the required constraints
 * and formats. It also uses service layers to perform business logic related to user operations and a mapper
 * to convert between entities and DTOs. Session attributes are used to manage the currently authenticated user.
 */
@RestController
@RequestMapping("/api/users")
@SessionAttributes("currentUser")
public class UserController extends BaseController {

    private final RegistrationService registrationService;
    private final UserService userService;
    private final ValidationUtils validationUtils;
    private final UserMapper userMapper;

    /**
     * Constructs a new {@code UserController} instance with the required dependencies.
     *
     * @param registrationService the service responsible for user registration and authentication
     * @param userService         the service responsible for user-related operations
     * @param validationUtils     the utility for validating request parameters and DTOs
     * @param userMapper          the mapper for converting between user entities and DTOs
     */
    @Autowired
    public UserController(RegistrationService registrationService, UserService userService,
                          ValidationUtils validationUtils, UserMapper userMapper) {
        this.registrationService = registrationService;
        this.userService = userService;
        this.validationUtils = validationUtils;
        this.userMapper = userMapper;
    }

    /**
     * Registers a new user with the provided details.
     * <p>
     * This endpoint validates the user data and delegates the request to the registration service
     * to create the user. If the operation succeeds, a success response containing the registered user
     * is returned; otherwise, an error response is returned.
     *
     * @param userDtoNew the request body containing the new user's details
     * @return a success response if the operation succeeds or an error response if validation fails
     */
    @PostMapping("/registration")
    @Operation(summary = "Register user", description = "Creates a new user account")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User registration data", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class,
            requiredProperties = {"name", "email", "password"}, example = """
            {
              "name": "John Doe",
              "email": "user@example.com",
              "password": "securePassword123"
            }""")))
    public ResponseEntity<Map<String, Object>> handleRegistration(@RequestBody UserDto userDtoNew) {
        try {
            UserDto userDto = validationUtils.validateRequest(userDtoNew, Mode.REGISTER_USER);
            boolean success = registrationService.registerUser(userDto);
            if (success) {
                User user = userService.getUserByEmail(userDto.getEmail());
                if (user != null) {
                    UserDto registeredUserDto = UserDto.removePassword(userMapper.toDto(user));
                    return buildSuccessResponse(
                            HttpStatus.CREATED, "User registered successfully", registeredUserDto);
                }
                return buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve user details.");
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to register user.");
        } catch (DuplicateEmailException | ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Authenticates a user with the provided email and password.
     * <p>
     * This endpoint validates the user credentials and delegates the request to the registration service
     * to authenticate the user. If successful, a session is created, and a success response containing the
     * authenticated user is returned; otherwise, an error response is returned.
     *
     * @param userDtoNew the request body containing the user's email and password
     * @param request    the HTTP servlet request used to manage the session
     * @param response   the HTTP servlet response used to set cookies
     * @return a success response if authentication succeeds or an error response if validation fails
     */
    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate user", description = "Logs in a user with credentials")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User credentials", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class,
            requiredProperties = {"email", "password"}, example = """
            {
              "email": "user@example.com",
              "password": "securePassword123"
            }""")))
    public ResponseEntity<Map<String, Object>> handleAuthentication(
            @RequestBody UserDto userDtoNew, HttpServletRequest request, HttpServletResponse response) {
        try {
            UserDto userDto = validationUtils.validateRequest(userDtoNew, Mode.AUTHENTICATE);
            boolean success = registrationService.authenticate(userDto);
            if (success) {
                User user = userService.getUserByEmail(userDto.getEmail());
                if (user != null) {
                    HttpSession session = request.getSession();
                    session.setMaxInactiveInterval(1800);
                    UserDto authUserDto = UserDto.removePassword(userMapper.toDto(user));
                    session.setAttribute("currentUser", authUserDto);
                    String cookie = String.format("JSESSIONID=%s; Path=/; HttpOnly; SameSite=Strict%s",
                            session.getId(), request.isSecure() ? "; Secure" : "");
                    response.setHeader("Set-Cookie", cookie);
                    return buildSuccessResponse(HttpStatus.OK, "Authentication successful", authUserDto);
                }
                return buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve user details.");
            }
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Logs out the currently authenticated user and invalidates the session.
     * <p>
     * This endpoint invalidates the user's session and clears the session cookie. A success response is
     * returned upon completion.
     *
     * @param request  the HTTP servlet request used to invalidate the session
     * @param response the HTTP servlet response used to clear cookies
     * @return a success response indicating that the user has been logged out
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Invalidates the current session")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = Object.class, example = "{}")))
    public ResponseEntity<Map<String, Object>> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        Cookie cookie = new Cookie("JSESSIONID", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return buildSuccessResponse(HttpStatus.OK, "Logged out successfully", null);
    }

    /**
     * Retrieves details of the currently authenticated user.
     * <p>
     * This endpoint retrieves the user details from the session attributes and returns them in a success response.
     *
     * @param userDto the currently authenticated user retrieved from the session
     * @return a success response containing the authenticated user's details
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns authenticated user details")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = Object.class, example = "{}")))
    public ResponseEntity<Map<String, Object>> getCurrentUser(@SessionAttribute("currentUser") UserDto userDto) {
        return buildSuccessResponse(HttpStatus.OK, "Authenticated user details", userDto);
    }

    /**
     * Updates the details of the currently authenticated user.
     * <p>
     * This endpoint validates the updated user data and delegates the request to the user service
     * to update the user's details. If the operation succeeds, a success response containing the updated user
     * is returned; otherwise, an error response is returned.
     *
     * @param userDtoNew     the request body containing the updated user details
     * @param currentUserDto the currently authenticated user retrieved from the session
     * @param model          the model used to update session attributes
     * @return a success response if the operation succeeds or an error response if validation fails
     */
    @PutMapping
    @Operation(summary = "Update user", description = "Updates user details")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated user data", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class,
            requiredProperties = {"name", "email", "password"}, example = """
            {
              "name": "New Name",
              "email": "new@example.com",
              "password": "newPassword123"
            }""")))
    public ResponseEntity<Map<String, Object>> updateUser(
            @RequestBody UserDto userDtoNew, @SessionAttribute("currentUser") UserDto currentUserDto, Model model) {
        try {
            UserDto userDto = validationUtils.validateRequest(userDtoNew, Mode.UPDATE_USER);
            boolean success = userService.updateOwnAccount(userDto, currentUserDto.getUserId());
            if (success) {
                User updatedUser = userService.getUserByEmail(userDto.getEmail());
                if (updatedUser != null) {
                    UserDto updatedUserDto = UserDto.removePassword(userMapper.toDto(updatedUser));
                    model.addAttribute("currentUser", updatedUserDto);
                    return buildSuccessResponse(HttpStatus.OK, "User updated successfully", updatedUserDto);
                }
                return buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve updated user details.");
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to update account.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Deletes the account of the currently authenticated user.
     * <p>
     * This endpoint delegates the request to the user service to delete the user's account. If the operation
     * succeeds, the session is invalidated, and a success response is returned; otherwise, an error response
     * is returned.
     *
     * @param userDto       the currently authenticated user retrieved from the session
     * @param sessionStatus the session status used to mark the session as complete
     * @return a success response if the operation succeeds or an error response if validation fails
     */
    @DeleteMapping
    @Operation(summary = "Delete account", description = "Permanently deletes the user account")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = Object.class, example = "{}")))
    public ResponseEntity<Map<String, Object>> deleteUser(
            @SessionAttribute("currentUser") UserDto userDto, SessionStatus sessionStatus) {
        try {
            boolean success = userService.deleteOwnAccount(userDto.getUserId());
            if (success) {
                sessionStatus.setComplete();
                return buildSuccessResponse(HttpStatus.OK, "Account deleted successfully",
                        Map.of("email", userDto.getEmail(), "timestamp", Instant.now()));
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to delete account.");
        } catch (Exception e) {
            return buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while deleting the account.");
        }
    }
}