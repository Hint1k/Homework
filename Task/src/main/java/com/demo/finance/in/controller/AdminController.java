package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.domain.utils.PaginationParams;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.custom.OptimisticLockException;
import com.demo.finance.exception.custom.UserNotFoundException;
import com.demo.finance.exception.custom.ValidationException;
import com.demo.finance.out.service.AdminService;
import com.demo.finance.out.service.TransactionService;
import com.demo.finance.out.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.demo.finance.domain.utils.SwaggerExamples.Admin.BLOCK_DEFAULT_ADMIN_RESPONSE;
import static com.demo.finance.domain.utils.SwaggerExamples.Admin.BLOCK_USER_REQUEST;
import static com.demo.finance.domain.utils.SwaggerExamples.Admin.BLOCK_USER_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Admin.DELETE_USER_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Admin.GET_USERS_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Admin.GET_USER_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Admin.GET_USER_TRANSACTIONS_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Admin.INVALID_PAGE_RESPONSE;
import static com.demo.finance.domain.utils.SwaggerExamples.Admin.INVALID_SIZE_RESPONSE;
import static com.demo.finance.domain.utils.SwaggerExamples.Admin.INVALID_USER_ID_RESPONSE;
import static com.demo.finance.domain.utils.SwaggerExamples.Admin.UPDATE_DEFAULT_ADMIN_RESPONSE;
import static com.demo.finance.domain.utils.SwaggerExamples.Admin.UPDATE_ROLE_REQUEST;
import static com.demo.finance.domain.utils.SwaggerExamples.Admin.UPDATE_ROLE_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Admin.USER_NOT_FOUND_RESPONSE;

/**
 * The {@code AdminController} class is a REST controller that provides endpoints for administrative operations
 * related to users and their transactions. It handles requests for user management, such as retrieving paginated
 * user lists, blocking/unblocking users, updating roles, and deleting users. Additionally, it supports retrieving
 * paginated transaction histories for specific users.
 * <p>
 * This controller leverages validation utilities to ensure that incoming requests meet the required constraints
 * and formats. It also uses service layers to perform business logic and map entities to DTOs for secure responses.
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController extends BaseController {

    private final AdminService adminService;
    private final UserService userService;
    private final TransactionService transactionService;
    private final ValidationUtils validationUtils;
    private final UserMapper userMapper;

    /**
     * Retrieves a paginated list of all users in the system.
     * <p>
     * This endpoint validates the pagination parameters and delegates the request to the user service
     * to fetch the paginated response. If the parameters are invalid, an error response is returned.
     *
     * @param paramsNew the pagination parameters provided in the request
     * @return a paginated response containing user data or an error response if validation fails
     */
    @GetMapping
    @Operation(summary = "Get users", description = "Returns paginated list of users")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PaginatedResponse.class),
            examples = @ExampleObject(name = "SuccessResponse", value = GET_USERS_SUCCESS)))
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid page parameter", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "InvalidPage",
            value = INVALID_PAGE_RESPONSE)))
    public ResponseEntity<Map<String, Object>> getPaginatedUsers(
            @ParameterObject @ModelAttribute PaginationParams paramsNew) {
        try {
            PaginationParams params = validationUtils.validateRequest(paramsNew, Mode.PAGE);
            PaginatedResponse<UserDto> paginatedResponse = userService.getPaginatedUsers(params.page(), params.size());
            return buildPaginatedResponse(null, paginatedResponse);
        } catch (ValidationException e) {
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST, "Invalid pagination parameters: " + e.getMessage());
        }
    }

    /**
     * Retrieves a paginated list of transactions for a specific user.
     * <p>
     * This endpoint validates the user ID and pagination parameters before delegating the request to the
     * transaction service. If any validation fails, an error response is returned.
     *
     * @param userId    the ID of the user whose transactions are being retrieved
     * @param paramsNew the pagination parameters provided in the request
     * @return a paginated response containing transaction data or an error response if validation fails
     */
    @GetMapping("/transactions/{userId}")
    @Operation(summary = "Get user transactions", description = "Returns paginated transactions for user")
    @ApiResponse(responseCode = "200", description = "User transactions retrieved successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PaginatedResponse.class),
            examples = @ExampleObject(name = "SuccessResponse", value = GET_USER_TRANSACTIONS_SUCCESS)))
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid size parameter", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "InvalidSize",
            value = INVALID_SIZE_RESPONSE)))
    public ResponseEntity<Map<String, Object>> getPaginatedTransactionsForUser(
            @PathVariable("userId") String userId, @ParameterObject @ModelAttribute PaginationParams paramsNew) {
        try {
            Long userIdLong = validationUtils.parseUserId(userId, Mode.GET);
            PaginationParams params = validationUtils.validateRequest(paramsNew, Mode.PAGE);
            PaginatedResponse<TransactionDto> paginatedResponse =
                    transactionService.getPaginatedTransactionsForUser(userIdLong, params.page(), params.size());
            return buildPaginatedResponse(userIdLong, paginatedResponse);
        } catch (ValidationException | IllegalArgumentException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Retrieves detailed information about a specific user.
     * <p>
     * This endpoint validates the user ID and retrieves the user's details from the admin service. The password
     * field is removed from the response for security reasons. If the user is not found or validation fails,
     * an appropriate error response is returned.
     *
     * @param userId the ID of the user whose details are being retrieved
     * @return a success response containing the user's details or an error response if validation fails
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Get user", description = "Returns user details by ID")
    @ApiResponse(responseCode = "200", description = "User details retrieved successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = GET_USER_SUCCESS)))
    @ApiResponse(responseCode = "404", description = "Not Found - User not found", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "UserNotFound",
            value = USER_NOT_FOUND_RESPONSE)))
    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable("userId") String userId) {
        try {
            Long userIdLong = validationUtils.parseUserId(userId, Mode.GET);
            User user = adminService.getUser(userIdLong);
            if (user != null) {
                UserDto userDto = UserDto.removePassword(userMapper.toDto(user));
                return buildSuccessResponse(HttpStatus.OK, "User details", userDto);
            }
            return buildErrorResponse(HttpStatus.NOT_FOUND, "User not found.");
        } catch (ValidationException | IllegalArgumentException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Blocks or unblocks a specific user.
     * <p>
     * This endpoint validates the user ID and the request body containing the block status. It then delegates
     * the request to the admin service to update the user's blocked status. If the operation succeeds, a success
     * response is returned; otherwise, an error response is returned.
     * <p>
     * If the update fails due to a version mismatch (indicating that the user was modified by another operation),
     * an {@link OptimisticLockException} is caught, and a 409 Conflict response is returned
     * to handle the concurrency conflict.
     *
     * @param userId     the ID of the user to block or unblock
     * @param userDtoNew the request body containing the updated block status
     * @return a success response if the operation succeeds or an error response if validation fails
     * or a concurrency conflict occurs
     */
    @PatchMapping("/block/{userId}")
    @Operation(summary = "Block/unblock user", description = "Updates user's blocked status")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Block status", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = BLOCK_USER_REQUEST)))
    @ApiResponse(responseCode = "200", description = "User blocked/unblocked successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = BLOCK_USER_SUCCESS)))
    @ApiResponse(responseCode = "400", description = "Bad request - Default admin can't be changed",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(name = "DefaultAdmin", value = BLOCK_DEFAULT_ADMIN_RESPONSE)))
    public ResponseEntity<Map<String, Object>> blockUnblockUser(
            @PathVariable("userId") String userId, @RequestBody UserDto userDtoNew) {
        try {
            Long userIdLong = validationUtils.parseUserId(userId, Mode.BLOCK_UNBLOCK);
            UserDto userDto = validationUtils.validateRequest(userDtoNew, Mode.BLOCK_UNBLOCK);
            boolean success = adminService.blockOrUnblockUser(userIdLong, userDto);
            if (success) {
                User updatedUser = userService.getUserById(userIdLong);
                if (updatedUser != null) {
                    UserDto updatedUserDto = UserDto.removePassword(userMapper.toDto(updatedUser));
                    return buildSuccessResponse(HttpStatus.OK,
                            "User blocked/unblocked status changed successfully", updatedUserDto);
                }
                return buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve updated user details.");
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to block/unblock user.");
        } catch (ValidationException | UserNotFoundException | IllegalArgumentException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (OptimisticLockException e) {
            return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Updates the role of a specific user.
     * <p>
     * This endpoint validates the user ID and the request body containing the updated role. It then delegates
     * the request to the admin service to update the user's role. If the operation succeeds, a success response
     * is returned; otherwise, an error response is returned.
     * <p>
     * If the update fails due to a version mismatch (indicating that the user was modified by another operation),
     * an {@link OptimisticLockException} is caught, and a 409 Conflict response is returned
     * to handle the concurrency conflict.
     *
     * @param userId     the ID of the user whose role is being updated
     * @param userDtoNew the request body containing the updated role
     * @return a success response if the operation succeeds or an error response if validation fails
     * or a concurrency conflict occurs
     */
    @PatchMapping("/role/{userId}")
    @Operation(summary = "Update user role", description = "Updates user's role")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Role update data", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = UPDATE_ROLE_REQUEST)))
    @ApiResponse(responseCode = "200", description = "User role updated successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = UPDATE_ROLE_SUCCESS)))
    @ApiResponse(responseCode = "400", description = "Bad request - Default admin can't be changed",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(name = "DefaultAdmin", value = UPDATE_DEFAULT_ADMIN_RESPONSE)))
    public ResponseEntity<Map<String, Object>> updateUserRole(
            @PathVariable("userId") String userId, @RequestBody UserDto userDtoNew) {
        try {
            userDtoNew.setRole(userDtoNew.getRole().toUpperCase());
            Long userIdLong = validationUtils.parseUserId(userId, Mode.UPDATE_ROLE);
            UserDto userDto = validationUtils.validateRequest(userDtoNew, Mode.UPDATE_ROLE);
            boolean success = adminService.updateUserRole(userIdLong, userDto);
            if (success) {
                User updatedUser = userService.getUserById(userIdLong);
                if (updatedUser != null) {
                    UserDto updatedUserDto = UserDto.removePassword(userMapper.toDto(updatedUser));
                    return buildSuccessResponse(
                            HttpStatus.OK, "User role updated successfully", updatedUserDto);
                }
                return buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve updated user details.");
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to update role.");
        } catch (ValidationException | UserNotFoundException | IllegalArgumentException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (OptimisticLockException e) {
            return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Deletes a specific user from the system.
     * <p>
     * This endpoint validates the user ID and delegates the request to the admin service to delete the user.
     * If the operation succeeds, a success response is returned; otherwise, an error response is returned.
     *
     * @param userId the ID of the user to delete
     * @return a success response if the operation succeeds or an error response if validation fails
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user", description = "Permanently deletes user account")
    @ApiResponse(responseCode = "200", description = "User deleted successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Long.class),
            examples = @ExampleObject(name = "SuccessResponse", value = DELETE_USER_SUCCESS)))
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid user ID format", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "InvalidUserId",
            value = INVALID_USER_ID_RESPONSE)))
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable("userId") String userId) {
        try {
            Long userIdLong = validationUtils.parseUserId(userId, Mode.DELETE);
            boolean success = adminService.deleteUser(userIdLong);
            if (success) {
                return buildSuccessResponse(HttpStatus.OK, "Account deleted successfully",
                        Map.of("userId", userIdLong));
            }
            return buildErrorResponse(HttpStatus.NOT_FOUND, "User not found.");
        } catch (ValidationException | IllegalArgumentException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}