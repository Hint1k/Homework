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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code AdminServlet} class is a servlet that handles HTTP requests related to admin operations,
 * such as retrieving paginated user lists, fetching user details, blocking/unblocking users, updating user roles,
 * and deleting users. It validates incoming JSON data, interacts with services for business logic,
 * and returns appropriate responses.
 */
@WebServlet("/api/admin/users/*")
public class AdminServlet extends HttpServlet {

    private final AdminService adminService;
    private final UserService userService;
    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;
    private final ValidationUtils validationUtils;

    /**
     * Constructs a new instance of {@code AdminServlet} with the required dependencies.
     *
     * @param adminService       the service responsible for admin-specific operations
     * @param userService         the service responsible for user-related operations
     * @param transactionService  the service responsible for transaction-related operations
     * @param objectMapper        the object mapper for JSON serialization and deserialization
     * @param validationUtils     the utility for validating incoming JSON data
     */
    public AdminServlet(AdminService adminService, UserService userService, TransactionService transactionService,
                        ObjectMapper objectMapper, ValidationUtils validationUtils) {
        this.adminService = adminService;
        this.userService = userService;
        this.transactionService = transactionService;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
        this.validationUtils = validationUtils;
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
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("data", paginatedResponse.data());
                responseMap.put("metadata", Map.of(
                        "totalItems", paginatedResponse.totalItems(),
                        "totalPages", paginatedResponse.totalPages(),
                        "currentPage", paginatedResponse.currentPage(),
                        "pageSize", paginatedResponse.pageSize()
                ));
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(responseMap));
            } catch (IllegalArgumentException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid pagination parameters: " + e.getMessage());
            }
        } else if (pathInfo != null && pathInfo.startsWith("/transactions/")) {
            try {
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
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("data", paginatedResponse.data());
                responseMap.put("metadata", Map.of(
                        "user_id", userId,
                        "totalItems", paginatedResponse.totalItems(),
                        "totalPages", paginatedResponse.totalPages(),
                        "currentPage", paginatedResponse.currentPage(),
                        "pageSize", paginatedResponse.pageSize()
                ));
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(responseMap));
            } catch (IllegalArgumentException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid request parameters: " + e.getMessage());
            }
        } else if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
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
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found.");
                }
            } catch (IllegalArgumentException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
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
            try {
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
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                            "Failed to block/unblock user.");
                }
            } catch (ValidationException | IllegalArgumentException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        } else if (pathInfo != null && pathInfo.startsWith("/role/")) {
            try {
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
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                            "Failed to update role.");
                }
            } catch (ValidationException | IllegalArgumentException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
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
        if (pathInfo != null && pathInfo.startsWith("/")) {
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
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found.");
                }
            } catch (ValidationException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            } catch (NumberFormatException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID.");
            }
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
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
        if ("PATCH".equals(request.getMethod())) { // Jetty bug, refused to support patch method
            doPatch(request, response);
        } else {
            super.service(request, response);
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
        StringBuilder jsonBody = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBody.append(line);
            }
        }
        return jsonBody.toString();
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