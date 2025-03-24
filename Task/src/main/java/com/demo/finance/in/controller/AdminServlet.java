package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.domain.utils.PaginationParams;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.ValidationException;
import com.demo.finance.out.service.AdminService;
import com.demo.finance.out.service.TransactionService;
import com.demo.finance.out.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * The {@code AdminServlet} class is a servlet that handles HTTP requests related to admin operations,
 * such as retrieving paginated user lists, fetching user details, blocking/unblocking users, updating user roles,
 * and deleting users. It extends the {@code BaseServlet} to reuse common functionality.
 */
@WebServlet("/api/admin/users/*")
public class AdminServlet extends BaseServlet {

    private final AdminService adminService;
    private final UserService userService;
    private final TransactionService transactionService;

    /**
     * Constructs a new instance of {@code AdminServlet} with the required dependencies.
     *
     * @param adminService       the service responsible for admin-specific operations
     * @param userService        the service responsible for user-related operations
     * @param transactionService the service responsible for transaction-related operations
     * @param validationUtils    the utility for validating incoming JSON data
     * @param objectMapper       the object mapper for JSON serialization and deserialization
     */
    public AdminServlet(AdminService adminService, UserService userService, TransactionService transactionService,
                        ValidationUtils validationUtils, ObjectMapper objectMapper) {
        super(validationUtils, objectMapper);
        this.adminService = adminService;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    /**
     * Handles GET requests for retrieving paginated user lists, user details, or paginated transactions for a specific user.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/".equals(pathInfo)) {
            handleGetPaginatedUsers(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/transactions/")) {
            handleGetPaginatedTransactionsForUser(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/")) {
            handleGetUserDetails(request, response);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    /**
     * Handles PATCH requests for blocking/unblocking users or updating user roles.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/block/")) {
            handleBlockUnblockUser(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/role/")) {
            handleUpdateUserRole(request, response);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    /**
     * Handles DELETE requests for deleting a user by their ID.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        handleDeleteUser(request, response, pathInfo);
    }

    /**
     * Overrides the default service method to handle PATCH requests due to Jetty's lack of native PATCH support.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException      if an I/O error occurs during request processing
     * @throws ServletException if a servlet-specific error occurs
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if ("PATCH".equals(request.getMethod())) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    /**
     * Handles GET requests for retrieving paginated user lists.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleGetPaginatedUsers(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String json = readRequestBody(request);
            PaginationParams paginationParams = objectMapper.readValue(json, PaginationParams.class);
            PaginationParams params = validationUtils.validatePaginationParams(
                    String.valueOf(paginationParams.page()),
                    String.valueOf(paginationParams.size())
            );
            PaginatedResponse<UserDto> paginatedResponse = userService.getPaginatedUsers(
                    params.page(),
                    params.size()
            );
            Map<String, Object> metadata = Map.of(
                    "totalItems", paginatedResponse.totalItems(),
                    "totalPages", paginatedResponse.totalPages(),
                    "currentPage", paginatedResponse.currentPage(),
                    "pageSize", paginatedResponse.pageSize()
            );
            sendPaginatedResponse(response, paginatedResponse.data(), metadata);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid pagination parameters: " + e.getMessage());
        }
    }

    /**
     * Handles GET requests for retrieving paginated transactions for a specific user.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleGetPaginatedTransactionsForUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            String userIdString = pathInfo.substring("/transactions/".length());
            String json = readRequestBody(request);
            PaginationParams paginationRequest = objectMapper.readValue(json, PaginationParams.class);
            Long userId = validationUtils.parseUserId(userIdString, Mode.GET);
            PaginationParams params = validationUtils.validatePaginationParams(
                    String.valueOf(paginationRequest.page()),
                    String.valueOf(paginationRequest.size())
            );
            PaginatedResponse<TransactionDto> paginatedResponse = transactionService
                    .getPaginatedTransactionsForUser(userId, params.page(), params.size());
            Map<String, Object> metadata = Map.of(
                    "user_id", userId,
                    "totalItems", paginatedResponse.totalItems(),
                    "totalPages", paginatedResponse.totalPages(),
                    "currentPage", paginatedResponse.currentPage(),
                    "pageSize", paginatedResponse.pageSize()
            );
            sendPaginatedResponse(response, paginatedResponse.data(), metadata);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid request parameters: " + e.getMessage());
        }
    }

    /**
     * Handles GET requests for fetching user details by ID.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleGetUserDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            String userIdString = pathInfo.substring(1);
            Long userId = validationUtils.parseUserId(userIdString, Mode.GET);
            User user = adminService.getUser(userId);
            if (user != null) {
                UserDto userDto = UserDto.removePassword(UserMapper.INSTANCE.toDto(user));
                Map<String, Object> responseBody = Map.of(
                        "message", "Authenticated user details",
                        "data", userDto,
                        "timestamp", java.time.Instant.now().toString()
                );
                sendSuccessResponse(response, HttpServletResponse.SC_OK, responseBody);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found.");
            }
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Handles PATCH requests for blocking/unblocking users.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleBlockUnblockUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            String userId = pathInfo.substring("/block/".length());
            String json = readRequestBody(request);
            UserDto userDto = validationUtils.validateUserJson(json, Mode.BLOCK_UNBLOCK, userId);
            boolean success = adminService.blockOrUnblockUser(userDto.getUserId(), userDto.isBlocked());
            if (success) {
                Map<String, Object> responseBody = Map.of(
                        "message", "User blocked/unblocked status changed successfully",
                        "data", UserDto.removePassword(userDto),
                        "timestamp", java.time.Instant.now().toString()
                );
                sendSuccessResponse(response, HttpServletResponse.SC_OK, responseBody);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Failed to block/unblock user.");
            }
        } catch (ValidationException | IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Handles PATCH requests for updating user roles.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleUpdateUserRole(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            String userId = pathInfo.substring("/role/".length());
            String json = readRequestBody(request);
            UserDto userDto = validationUtils.validateUserJson(json, Mode.UPDATE_ROLE, userId);
            boolean success = adminService.updateUserRole(userDto);
            if (success) {
                Map<String, Object> responseBody = Map.of(
                        "message", "User role updated successfully",
                        "data", UserDto.removePassword(userDto),
                        "timestamp", java.time.Instant.now().toString()
                );
                sendSuccessResponse(response, HttpServletResponse.SC_OK, responseBody);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Failed to update role.");
            }
        } catch (ValidationException | IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Handles DELETE requests for deleting a user by their ID.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @param pathInfo the path info from the request
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws IOException {
        try {
            String userIdString = pathInfo.substring(1);
            Long userId = validationUtils.parseUserId(userIdString, Mode.DELETE);
            boolean success = adminService.deleteUser(userId);
            if (success) {
                Map<String, Object> responseBody = Map.of(
                        "message", "Account deleted successfully",
                        "deleted user id", userId,
                        "timestamp", java.time.Instant.now().toString()
                );
                sendSuccessResponse(response, HttpServletResponse.SC_OK, responseBody);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found.");
            }
        } catch (ValidationException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID.");
        }
    }
}