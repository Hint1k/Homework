package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.custom.DuplicateEmailException;
import com.demo.finance.exception.custom.OptimisticLockException;
import com.demo.finance.exception.custom.ValidationException;
import com.demo.finance.out.service.JwtService;
import com.demo.finance.out.service.RegistrationService;
import com.demo.finance.out.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.demo.finance.domain.utils.SwaggerExamples.User.AUTHENTICATION_REQUEST;
import static com.demo.finance.domain.utils.SwaggerExamples.User.AUTHENTICATION_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.User.DELETE_ACCOUNT_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.User.INVALID_CREDENTIALS_RESPONSE;
import static com.demo.finance.domain.utils.SwaggerExamples.User.INVALID_REGISTRATION_RESPONSE;
import static com.demo.finance.domain.utils.SwaggerExamples.User.MISSING_ACCOUNT_FIELD_RESPONSE;
import static com.demo.finance.domain.utils.SwaggerExamples.User.REGISTRATION_REQUEST;
import static com.demo.finance.domain.utils.SwaggerExamples.User.REGISTRATION_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.User.UPDATE_ACCOUNT_REQUEST;
import static com.demo.finance.domain.utils.SwaggerExamples.User.UPDATE_ACCOUNT_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.User.GET_DETAILS_SUCCESS;

/**
 * REST controller for managing user accounts.
 * <p>
 * This controller provides endpoints for user registration, authentication, retrieval,
 * updating, and deletion. It uses various services such as {@link RegistrationService},
 * {@link UserService}, and {@link JwtService} to handle business logic and validation.
 * </p>
 * <p>
 * Accessible under the path <code>/api/users</code>.
 * </p>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final RegistrationService registrationService;
    private final UserService userService;
    private final ValidationUtils validationUtils;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    /**
     * Registers a new user account.
     * <p>
     * This endpoint validates the provided user data, processes the registration request,
     * and returns either a success or error response. If the registration is successful,
     * a response with user details is returned, excluding sensitive fields such as the password.
     * </p>
     *
     * @param userDtoNew the user registration data
     * @return a {@link ResponseEntity} with a success or error message
     */
    @PostMapping("/registration")
    @Operation(summary = "Register user", description = "Creates a new user account")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User registration data", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = REGISTRATION_REQUEST)))
    @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = REGISTRATION_SUCCESS)))
    @ApiResponse(responseCode = "400", description = "Bad Request - Validation error", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "ValidationError",
            value = INVALID_REGISTRATION_RESPONSE)))
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
     * Authenticates a user with credentials.
     * <p>
     * This endpoint validates the provided user credentials, generates a JWT token on successful
     * authentication, and returns the user details (excluding the password). If authentication fails,
     * a 401 Unauthorized error is returned.
     * </p>
     *
     * @param userDtoNew the user login data
     * @param response   the HTTP response object used to set the authorization token header
     * @return a {@link ResponseEntity} containing the authenticated user details or an error message
     */
    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate user", description = "Logs in a user with credentials")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User credentials", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = AUTHENTICATION_REQUEST)))
    @ApiResponse(responseCode = "200", description = "User authenticated successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = AUTHENTICATION_SUCCESS)))
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "InvalidCredentials",
            value = INVALID_CREDENTIALS_RESPONSE)))
    public ResponseEntity<Map<String, Object>> handleAuthentication(
            @RequestBody UserDto userDtoNew, HttpServletResponse response) {
        try {
            UserDto userDto = validationUtils.validateRequest(userDtoNew, Mode.AUTHENTICATE);
            boolean success = registrationService.authenticate(userDto);
            if (success) {
                User user = userService.getUserByEmail(userDto.getEmail());
                if (user != null) {
                    UserDto authUserDto = UserDto.removePassword(userMapper.toDto(user));
                    String token = jwtService
                            .generateToken(authUserDto.getEmail(), List.of(authUserDto.getRole()), user.getUserId());
                    response.setHeader("Authorization", "Bearer " + token);
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
     * Retrieves the details of the currently authenticated user.
     * <p>
     * This endpoint fetches the authenticated user's details from the system based on the user ID
     * and returns the user information excluding sensitive fields such as the password.
     * </p>
     *
     * @param userDto the currently authenticated user's data, passed as a request attribute
     * @return a {@link ResponseEntity} containing the authenticated user's details
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns authenticated user details")
    @ApiResponse(responseCode = "200", description = "User details received successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = GET_DETAILS_SUCCESS)))
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestAttribute("currentUser") UserDto userDto) {
        User user = userService.getUserById(userDto.getUserId());
        UserDto dto = userMapper.toDto(user);
        UserDto.removePassword(dto);
        return buildSuccessResponse(HttpStatus.OK, "Authenticated user details", dto);
    }

    /**
     * Updates the details of the currently authenticated user.
     * <p>
     * This endpoint validates the provided user data, updates the user's details, and returns the updated user
     * information. If the update fails, a 400 Bad Request error is returned.
     * <p>
     * If the update fails due to a version mismatch (indicating that the user was modified by another operation),
     * an {@link OptimisticLockException} is caught, and a 409 Conflict response is returned
     * to handle the concurrency conflict.
     *
     * @param userDtoNew     the updated user data
     * @param currentUserDto the currently authenticated user
     * @return a {@link ResponseEntity} with the updated user details or an error message
     */
    @PutMapping
    @Operation(summary = "Update user", description = "Updates user details")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated user data", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = UPDATE_ACCOUNT_REQUEST)))
    @ApiResponse(responseCode = "200", description = "Updated user successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = UPDATE_ACCOUNT_SUCCESS)))
    @ApiResponse(responseCode = "400", description = "Bad Request - Missing account field", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "ValidationError",
            value = MISSING_ACCOUNT_FIELD_RESPONSE)))
    public ResponseEntity<Map<String, Object>> updateUser(
            @RequestBody UserDto userDtoNew, @RequestAttribute("currentUser") UserDto currentUserDto) {
        try {
            UserDto userDto = validationUtils.validateRequest(userDtoNew, Mode.UPDATE_USER);
            boolean success = userService.updateOwnAccount(userDto, currentUserDto.getUserId());
            if (success) {
                User updatedUser = userService.getUserById(userDto.getUserId());
                if (updatedUser != null) {
                    UserDto updatedUserDto = UserDto.removePassword(userMapper.toDto(updatedUser));
                    return buildSuccessResponse(HttpStatus.OK, "User updated successfully", updatedUserDto);
                }
                return buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve updated user details.");
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to update account.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (OptimisticLockException e) {
            return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Deletes the account of the currently authenticated user.
     * <p>
     * This endpoint permanently deletes the user account and returns a success response. If deletion fails, a
     * 400 Bad Request error is returned.
     * </p>
     *
     * @param userDto the currently authenticated user
     * @return a {@link ResponseEntity} with the success or error message
     */
    @DeleteMapping
    @Operation(summary = "Delete account", description = "Permanently deletes the user account")
    @ApiResponse(responseCode = "200", description = "Deleted user successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = DELETE_ACCOUNT_SUCCESS)))
    public ResponseEntity<Map<String, Object>> deleteUser(@RequestAttribute("currentUser") UserDto userDto) {
        try {
            boolean success = userService.deleteOwnAccount(userDto.getUserId());
            if (success) {
                return buildSuccessResponse(HttpStatus.OK, "Account deleted successfully",
                        Map.of("email", userDto.getEmail()));
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to delete account.");
        } catch (Exception e) {
            return buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while deleting the account.");
        }
    }
}