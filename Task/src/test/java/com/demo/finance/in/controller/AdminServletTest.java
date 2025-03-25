package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.dto.UserDto;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServletTest {

    @Mock private AdminService adminService;
    @Mock private UserService userService;
    @Mock private TransactionService transactionService;
    @Mock private ValidationUtils validationUtils;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private PrintWriter printWriter;
    @Spy private ObjectMapper objectMapper = new ObjectMapper();
    private AdminServlet adminServlet;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        adminServlet = new AdminServlet(adminService, userService, transactionService, validationUtils, objectMapper);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    @DisplayName("Get paginated users - Success scenario")
    void testGetPaginatedUsers_Success() throws Exception {
        String requestBody = "{\"page\": 1, \"size\": 10}";
        PaginationParams paginationParams = new PaginationParams(1, 10);
        PaginatedResponse<UserDto> paginatedResponse = new PaginatedResponse<>(List.of(new UserDto()),
                1, 1, 1, 10);

        when(request.getPathInfo()).thenReturn("/");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(validationUtils.validatePaginationParams(requestBody, Mode.PAGE)).thenReturn(paginationParams);
        when(userService.getPaginatedUsers(1, 10)).thenReturn(paginatedResponse);

        adminServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("data"));
    }

    @Test
    @DisplayName("Get transactions for user - Success scenario")
    void testGetUserTransactions_Success() throws Exception {
        String requestBody = "{\"page\": 1, \"size\": 10}";
        String pathInfo = "/transactions/2";
        PaginationParams paginationParams = new PaginationParams(1, 10);
        PaginatedResponse<TransactionDto> paginatedResponse = new PaginatedResponse<>(List.of(new TransactionDto()),
                1, 1, 1, 10);

        when(request.getPathInfo()).thenReturn(pathInfo);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(validationUtils.parseUserId("2", Mode.GET)).thenReturn(2L);
        when(validationUtils.validatePaginationParams(requestBody, Mode.PAGE)).thenReturn(paginationParams);
        when(transactionService.getPaginatedTransactionsForUser(2L, 1, 10))
                .thenReturn(paginatedResponse);

        adminServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("user_id"));
    }

    @Test
    @DisplayName("Block/unblock user - Success scenario")
    void testBlockOrUnblockUser_Success() throws Exception {
        String requestBody = "{\"userId\": 2, \"blocked\": true}";
        String pathInfo = "/block/2";
        UserDto userDto = new UserDto();
        userDto.setUserId(2L);
        userDto.setBlocked(true);

        when(request.getPathInfo()).thenReturn(pathInfo);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(validationUtils.validateUserJson(any(), eq(Mode.BLOCK_UNBLOCK), eq("2"))).thenReturn(userDto);
        when(adminService.blockOrUnblockUser(2L, true)).thenReturn(true);

        adminServlet.doPatch(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("User blocked/unblocked status changed successfully"));
    }

    @Test
    @DisplayName("Update user role - Success scenario")
    void testUpdateUserRole_Success() throws Exception {
        String requestBody = "{\"userId\": 2, \"role\": \"ADMIN\"}";
        String pathInfo = "/role/2";
        UserDto userDto = new UserDto();
        userDto.setUserId(2L);

        when(request.getPathInfo()).thenReturn(pathInfo);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(validationUtils.validateUserJson(any(), eq(Mode.UPDATE_ROLE), eq("2"))).thenReturn(userDto);
        when(adminService.updateUserRole(userDto)).thenReturn(true);

        adminServlet.doPatch(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("User role updated successfully"));
    }

    @Test
    @DisplayName("Delete user account - Success scenario")
    void testDeleteUserAccount_Success() throws Exception {
        String pathInfo = "/2";

        when(request.getPathInfo()).thenReturn(pathInfo);
        when(validationUtils.parseUserId("2", Mode.DELETE)).thenReturn(2L);
        when(adminService.deleteUser(2L)).thenReturn(true);

        adminServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("Account deleted successfully"));
    }

    @Test
    @DisplayName("Invalid pagination request - Throws ValidationException")
    void testGetPaginatedUsers_InvalidPagination() throws Exception {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getReader()).thenReturn(new BufferedReader(
                new StringReader("{\"page\": \"invalid\", \"size\": 10}")));
        doThrow(new ValidationException("Invalid page number."))
                .when(validationUtils).validatePaginationParams(anyString(), eq(Mode.PAGE));

        adminServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(printWriter).write(contains("Invalid pagination parameters"));
    }

    @Test
    @DisplayName("Block/unblock user - ValidationException")
    void testBlockOrUnblockUser_ValidationException() throws Exception {
        String requestBody = "{\"userId\": 2, \"blocked\": true}";
        String pathInfo = "/block/2";

        when(request.getPathInfo()).thenReturn(pathInfo);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        doThrow(new ValidationException("Invalid user data")).when(validationUtils)
                .validateUserJson(any(), eq(Mode.BLOCK_UNBLOCK), eq("2"));

        adminServlet.doPatch(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(printWriter).write(contains("Invalid user data"));
    }

    @Test
    @DisplayName("User not found - GET request")
    void testDoGet_UserNotFound() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(request.getPathInfo()).thenReturn("/does-not-exist");

        adminServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(printWriter).write(contains("User not found"));
    }

    @Test
    @DisplayName("Endpoint not found - GET request")
    void testDoGet_EndpointNotFound() throws Exception {
        when(request.getPathInfo()).thenReturn("");

        adminServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(printWriter).write(contains("Endpoint not found"));
    }
}