package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.TransactionMapper;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.domain.utils.PaginationParams;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.ValidationException;
import com.demo.finance.out.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServletTest {

    @Mock private TransactionService transactionService;
    @Mock private ValidationUtils validationUtils;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private PrintWriter printWriter;
    @Spy private TransactionMapper transactionMapper = TransactionMapper.INSTANCE;
    private TransactionServlet transactionServlet;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        transactionServlet = new TransactionServlet(transactionService, objectMapper, validationUtils);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    @DisplayName("Create transaction - Success scenario")
    void testCreateTransaction_Success() throws Exception {
        String requestBody = "{\"userId\": 1, \"amount\": 100.0, \"category\": \"Food\", \"date\": \"2023-10-01\", "
                + "\"description\": \"Lunch\", \"type\": \"EXPENSE\"}";
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setUserId(1L);
        transactionDto.setAmount(BigDecimal.valueOf(100.0));

        when(request.getPathInfo()).thenReturn("/");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(validationUtils.validateTransactionJson(any(), eq(Mode.TRANSACTION_CREATE))).thenReturn(transactionDto);
        when(transactionService.createTransaction(any())).thenReturn(1L);
        when(transactionService.getTransaction(1L)).thenReturn(new Transaction());

        transactionServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(printWriter).write(contains("Transaction created successfully"));
    }

    @Test
    @DisplayName("Get paginated transactions - Success scenario")
    void testGetPaginatedTransactions_Success() throws Exception {
        String requestBody = "{\"page\": 1, \"size\": 10}";
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        when(request.getPathInfo()).thenReturn("/");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(validationUtils.validatePaginationParams("1", "10"))
                .thenReturn(new PaginationParams(1, 10));
        when(transactionService.getPaginatedTransactionsForUser(1L, 1, 10))
                .thenReturn(new PaginatedResponse<>(Collections
                        .emptyList(), 10, 0, 1, 10));

        transactionServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("data"));
    }

    @Test
    @DisplayName("Get transaction by ID - Success scenario")
    void testGetTransactionById_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        when(request.getPathInfo()).thenReturn("/1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(validationUtils.parseTransactionId("1", Mode.GET)).thenReturn(1L);
        when(transactionService.getTransactionByUserIdAndTransactionId(1L, 1L))
                .thenReturn(new Transaction());

        transactionServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("Transaction found successfully"));
    }

    @Test
    @DisplayName("Update transaction - Success scenario")
    void testUpdateTransaction_Success() throws Exception {
        String requestBody = "{\"userId\": 1, \"amount\": 150.0, \"category\": \"Food\", \"description\": \"Dinner\"}";
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        when(request.getPathInfo()).thenReturn("/1");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(validationUtils.validateTransactionJson(any(), eq(Mode.TRANSACTION_UPDATE), eq("1")))
                .thenReturn(new TransactionDto());
        when(transactionService.updateTransaction(any(), eq(1L))).thenReturn(true);
        when(transactionService.getTransaction(any())).thenReturn(new Transaction());

        transactionServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("Transaction updated successfully"));
    }

    @Test
    @DisplayName("Delete transaction - Success scenario")
    void testDeleteTransaction_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        when(request.getPathInfo()).thenReturn("/1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(validationUtils.parseTransactionId("1", Mode.TRANSACTION_DELETE)).thenReturn(1L);
        when(transactionService.deleteTransaction(1L, 1L)).thenReturn(true);

        transactionServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(printWriter).write(contains("Transaction deleted successfully"));
    }

    @Test
    @DisplayName("Create transaction - ValidationException")
    void testCreateTransaction_ValidationException() throws Exception {
        String requestBody = "{\"userId\": 1, \"amount\": -100.0}";

        when(request.getPathInfo()).thenReturn("/");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(validationUtils.validateTransactionJson(any(), eq(Mode.TRANSACTION_CREATE)))
                .thenThrow(new ValidationException("Amount must be positive"));

        transactionServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(printWriter).write(contains("Amount must be positive"));
    }

    @Test
    @DisplayName("Get transaction by ID - Not found")
    void testGetTransactionById_NotFound() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserId(1L);

        when(request.getPathInfo()).thenReturn("/1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(userDto);
        when(validationUtils.parseTransactionId("1", Mode.GET)).thenReturn(1L);
        when(transactionService.getTransactionByUserIdAndTransactionId(1L, 1L))
                .thenReturn(null);

        transactionServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(printWriter).write(contains("Transaction not found or you are not the owner of the transaction"));
    }

    @Test
    @DisplayName("Update transaction - ValidationException")
    void testUpdateTransaction_ValidationException() throws Exception {
        String requestBody = "{\"userId\": 1, \"amount\": -150.0}";

        when(request.getPathInfo()).thenReturn("/1");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(new UserDto());
        when(validationUtils.validateTransactionJson(any(), eq(Mode.TRANSACTION_UPDATE), eq("1")))
                .thenThrow(new ValidationException("Amount must be positive"));

        transactionServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(printWriter).write(contains("Amount must be positive"));
    }

    @Test
    @DisplayName("Delete transaction - Invalid transaction ID")
    void testDeleteTransaction_InvalidTransactionId() throws Exception {
        when(request.getPathInfo()).thenReturn("/invalid");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(new UserDto());
        when(validationUtils.parseTransactionId("invalid", Mode.TRANSACTION_DELETE))
                .thenThrow(new NumberFormatException("Invalid transaction ID"));

        transactionServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(printWriter).write(contains("Invalid transaction ID"));
    }

    @Test
    @DisplayName("Endpoint not found - GET request")
    void testDoGet_EndpointNotFound() throws Exception {
        when(request.getPathInfo()).thenReturn("");

        transactionServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(printWriter).write(contains("Endpoint not found"));
    }
}