package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.TransactionMapper;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.domain.utils.PaginationParams;
import com.demo.finance.domain.utils.TranValidationUtils;
import com.demo.finance.exception.ValidationException;
import com.demo.finance.out.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/transactions/*")
public class TransactionServlet extends HttpServlet {

    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;
    private final TranValidationUtils validationUtils;

    public TransactionServlet(TransactionService transactionService, ObjectMapper objectMapper,
                              TranValidationUtils validationUtils) {
        this.transactionService = transactionService;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
        this.validationUtils = validationUtils;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/".equals(pathInfo)) {
            try {
                String json = readRequestBody(request);
                TransactionDto transactionDto = validationUtils.validateTransactionJson(json, Mode.CREATE);
                Long transactionId = transactionService.createTransaction(transactionDto);
                if (transactionId != null) {
                    Transaction transaction = transactionService.getTransaction(transactionId);
                    if (transaction != null) {
                        TransactionDto transactionDtoCreated = TransactionMapper.INSTANCE.toDto(transaction);
                        Map<String, Object> responseBody = Map.of(
                                "message", "Transaction created successfully",
                                "data", transactionDtoCreated,
                                "timestamp", java.time.Instant.now().toString()
                        );
                        response.setStatus(HttpServletResponse.SC_CREATED);
                        response.setContentType("application/json");
                        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        response.setContentType("application/json");
                        response.getWriter().write("Failed to retrieve transaction details.");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json");
                    response.getWriter().write("Failed to create transaction.");
                }
            } catch (ValidationException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write(e.getMessage());
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.getWriter().write("An error occurred while creating the transaction.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.getWriter().write("Endpoint not found.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo)) {
            try {
                UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
                Long userId = userDto.getUserId();
                String json = readRequestBody(request);
                PaginationParams paginationRequest = objectMapper.readValue(json, PaginationParams.class);
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
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                        "error", "Invalid request parameters",
                        "message", e.getMessage()
                )));
            }
        } else if (pathInfo.startsWith("/")) {
            try {
                UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
                Long userId = userDto.getUserId();
                Long transactionId = validationUtils.parseTransactionId(pathInfo.substring(1), Mode.GET);
                Transaction transaction = transactionService
                        .getTransactionByUserIdAndTransactionId(userId, transactionId);
                if (transaction != null) {
                    TransactionDto transactionDto = TransactionMapper.INSTANCE.toDto(transaction);
                    Map<String, Object> responseBody = Map.of(
                            "message", "Transaction found successfully",
                            "data", transactionDto,
                            "timestamp", java.time.Instant.now().toString()
                    );
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.setContentType("application/json");
                    response.getWriter().write("Transaction not found or you are not the owner of the transaction.");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("Invalid transaction ID.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.getWriter().write("Endpoint not found.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
                Long userId = userDto.getUserId();
                String transactionIdString = pathInfo.substring(1);
                String json = readRequestBody(request);
                TransactionDto transactionDto = validationUtils
                        .validateTransactionJson(json, Mode.UPDATE, transactionIdString);
                boolean success = transactionService.updateTransaction(transactionDto, userId);
                if (success) {
                    Transaction transaction = transactionService.getTransaction(transactionDto.getTransactionId());
                    if (transaction != null) {
                        TransactionDto transactionDtoUpdated = TransactionMapper.INSTANCE.toDto(transaction);
                        Map<String, Object> responseBody = Map.of(
                                "message", "Transaction updated successfully",
                                "data", transactionDtoUpdated,
                                "timestamp", java.time.Instant.now().toString()
                        );
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setContentType("application/json");
                        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        response.setContentType("application/json");
                        response.getWriter().write("Failed to retrieve transaction details.");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json");
                    response.getWriter()
                            .write("Failed to update transaction or you are not the owner of the transaction.");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("Invalid transaction ID.");
            } catch (ValidationException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write(e.getMessage());
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.getWriter().write("An error occurred while updating the transaction.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.getWriter().write("Endpoint not found.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
                Long userId = userDto.getUserId();
                String transactionIdString = pathInfo.substring(1);
                Long transactionId = validationUtils.parseTransactionId(transactionIdString, Mode.DELETE);
                boolean success = transactionService.deleteTransaction(userId, transactionId);
                if (success) {
                    Map<String, Object> responseBody = Map.of(
                            "message", "Transaction deleted successfully",
                            "transaction id", transactionId,
                            "timestamp", java.time.Instant.now().toString()
                    );
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json");
                    response.getWriter()
                            .write("Failed to delete transaction or you are not the owner of the transaction.");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("Invalid transaction ID");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.getWriter().write("Endpoint not found.");
        }
    }

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
}