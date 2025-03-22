package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.domain.utils.PaginationParams;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.out.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.demo.finance.domain.dto.UserDto.removePassword;

@WebServlet("/api/admin/users/*")
public class AdminServlet extends HttpServlet {

    private final AdminService adminService;
    private final ObjectMapper objectMapper;
    private final ValidationUtils validationUtils;

    public AdminServlet(AdminService adminService, ObjectMapper objectMapper, ValidationUtils validationUtils) {
        this.adminService = adminService;
        this.objectMapper = objectMapper;
        this.validationUtils = validationUtils;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/".equals(pathInfo)) {
            try {
                String page = request.getParameter("page");
                String size = request.getParameter("size");
                PaginationParams params = validationUtils.validatePaginationParams(page, size);
                PaginatedResponse<UserDto> paginatedResponse = adminService
                        .getPaginatedUsers(params.page(), params.size());
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("data", paginatedResponse.data());
                responseMap.put("metadata", Map.of("totalItems", paginatedResponse.totalItems(),
                        "totalPages", paginatedResponse.totalPages(), "currentPage",
                        paginatedResponse.currentPage(), "pageSize", paginatedResponse.pageSize()));
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(responseMap));
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(Map.of("error",
                        "Invalid pagination parameters", "message", e.getMessage())));
            }
        } else if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                String userId = pathInfo.substring(1);
                UserDto userDto = validationUtils.validateUserJson(userId, Mode.GET, userId);
                User user = adminService.getUser(userDto.getUserId());
                if (user != null) {
                    userDto = UserMapper.INSTANCE.toDto(user);
                    Map<String, Object> responseBody = Map.of(
                            "message","Authenticated user details",
                            "data", UserDto.removePassword(userDto),
                            "timestamp", java.time.Instant.now().toString()
                    );
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("User not found.");
                }
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(e.getMessage());
            }
        } else if (pathInfo != null && pathInfo.startsWith("/transactions/")) {
            try {
                String userId = pathInfo.substring("/transactions/".length());
                UserDto userDto = validationUtils
                        .validateUserJson(userId, Mode.GET_USER_TRANSACTIONS, userId);
                String page = request.getParameter("page");
                String size = request.getParameter("size");
                PaginationParams params = validationUtils.validatePaginationParams(page, size);
                PaginatedResponse<TransactionDto> paginatedResponse = adminService.getPaginatedTransactionsForUser(
                        userDto.getUserId(), params.page(), params.size());
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("data", paginatedResponse.data());
                responseMap.put("metadata", Map.of("totalItems", paginatedResponse.totalItems(),
                        "totalPages", paginatedResponse.totalPages(),
                        "currentPage", paginatedResponse.currentPage(),
                        "pageSize", paginatedResponse.pageSize()
                ));
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(responseMap));
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                        "error", "Invalid request parameters",
                        "message", e.getMessage()
                )));
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                String userId = pathInfo.substring(1);
                String json = readRequestBody(request);
                UserDto userDto = validationUtils.validateUserJson(json, Mode.UPDATE_ROLE, userId);
                boolean success = adminService.updateUserRole(userDto);
                if (success) {
                    Map<String, Object> responseBody = Map.of(
                            "message","User role updated successfully",
                            "data", UserDto.removePassword(userDto),
                            "timestamp", java.time.Instant.now().toString()
                    );
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Failed to update role.");
                }
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(e.getMessage());
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                String userId = pathInfo.substring(1);
                String json = readRequestBody(request);
                UserDto userDto = validationUtils.validateUserJson(json, Mode.BLOCK_UNBLOCK, userId);
                boolean success = adminService.blockOrUnblockUser(userDto.getUserId());
                if (success) {
                    Map<String, Object> responseBody = Map.of(
                            "message","User blocked/unblocked status changed successfully",
                            "data", UserDto.removePassword(userDto),
                            "timestamp", java.time.Instant.now().toString()
                    );
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Failed to block/unblock user.");
                }
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(e.getMessage());
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                String userId = pathInfo.substring(1);
                String json = readRequestBody(request);
                UserDto userDto = validationUtils.validateUserJson(json, Mode.DELETE, userId);
                boolean success = adminService.deleteUser(userDto.getUserId());
                if (success) {
                    Map<String, Object> responseBody = Map.of(
                            "message", "Account deleted successfully",
                            "data",userDto.getUserId(),
                            "timestamp", java.time.Instant.now().toString()
                    );
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("User not found.");
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
}