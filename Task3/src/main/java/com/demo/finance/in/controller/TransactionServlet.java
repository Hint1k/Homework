package com.demo.finance.in.controller;

import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.mapper.TransactionMapper;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The {@code TransactionServlet} class handles incoming HTTP requests related to transaction management.
 * It provides endpoints for adding, retrieving, updating, deleting, and filtering transactions.
 */
@WebServlet("/api/transactions/*")
public class TransactionServlet extends HttpServlet {

    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a {@code TransactionServlet} with the specified {@code TransactionService}.
     */
    public TransactionServlet(TransactionService transactionService) {
        this.transactionService = transactionService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Handles POST requests to add a new transaction.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/".equals(pathInfo)) {
            try {
                StringBuilder jsonBody = new StringBuilder();
                String line;
                try (BufferedReader reader = request.getReader()) {
                    while ((line = reader.readLine()) != null) {
                        jsonBody.append(line);
                    }
                }
                TransactionDto transactionDto = objectMapper.readValue(jsonBody.toString(), TransactionDto.class);
                Transaction transaction = TransactionMapper.INSTANCE.toEntity(transactionDto);
                transactionService.createTransaction(
                        transaction.getUserId(),
                        transaction.getAmount(),
                        transaction.getCategory(),
                        transaction.getDate().toString(),
                        transaction.getDescription(),
                        transaction.getType()
                );
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(transactionDto));
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid JSON format.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }

    /**
     * Handles GET requests to retrieve a transaction by ID.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                Long transactionId = Long.parseLong(pathInfo.substring(1));
                Transaction transaction = transactionService.getTransaction(transactionId);
                if (transaction != null) {
                    TransactionDto transactionDto = TransactionMapper.INSTANCE.toDto(transaction);
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(transactionDto));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("Transaction not found.");
                }
                return;
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid transaction ID.");
                return;
            }
        }
        if ("/transactions".equals(pathInfo)) {
            try {
                int page = Integer.parseInt(request.getParameter("page") != null
                        ? request.getParameter("page") : "1");
                int size = Integer.parseInt(request.getParameter("size") != null
                        ? request.getParameter("size") : "10");
                if (page <= 0 || size <= 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter()
                            .write("Invalid pagination parameters. Page and size must be positive integers.");
                    return;
                }
                Long userId = Long.parseLong(request.getParameter("userId"));
                LocalDate fromDate = request.getParameter("from") != null ?
                        LocalDate.parse(request.getParameter("from")) : null;
                LocalDate toDate = request.getParameter("to") != null ?
                        LocalDate.parse(request.getParameter("to")) : null;
                String category = request.getParameter("category");
                Type type = request.getParameter("type") != null ?
                        Type.valueOf(request.getParameter("type")) : null;
                List<Transaction> transactions = transactionService
                        .getFilteredTransactionsWithPagination(
                        userId, fromDate, toDate, category, type, page, size);
                int totalItems = transactionService
                        .getTotalFilteredTransactionsCount(userId, fromDate, toDate, category, type);
                int totalPages = (int) Math.ceil((double) totalItems / size);
                List<TransactionDto> transactionDtos = transactions.stream()
                        .map(TransactionMapper.INSTANCE::toDto)
                        .collect(Collectors.toList());
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("data", transactionDtos);
                responseMap.put("metadata", Map.of(
                        "totalItems", totalItems,
                        "totalPages", totalPages,
                        "currentPage", page,
                        "pageSize", size
                ));
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(responseMap));
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid filter or pagination parameters.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }

    /**
     * Handles PUT requests to update an existing transaction.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                Long transactionId = Long.parseLong(pathInfo.substring(1));
                StringBuilder jsonBody = new StringBuilder();
                String line;
                try (BufferedReader reader = request.getReader()) {
                    while ((line = reader.readLine()) != null) {
                        jsonBody.append(line);
                    }
                }
                TransactionDto transactionDto = objectMapper.readValue(jsonBody.toString(), TransactionDto.class);
                transactionDto.setTransactionId(transactionId);
                Transaction transaction = TransactionMapper.INSTANCE.toEntity(transactionDto);
                boolean success = transactionService.updateTransaction(
                        transaction.getTransactionId(),
                        transaction.getUserId(),
                        transaction.getAmount(),
                        transaction.getCategory(),
                        transaction.getDescription()
                );
                if (success) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(objectMapper.writeValueAsString(transactionDto));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Failed to update transaction.");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid transaction ID.");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid JSON format.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }

    /**
     * Handles DELETE requests to delete a transaction.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                Long transactionId = Long.parseLong(pathInfo.substring(1));
                Long userId = Long.parseLong(request.getParameter("userId"));
                boolean success = transactionService.deleteTransaction(userId, transactionId);
                if (success) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Transaction deleted successfully.");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("Failed to delete transaction.");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid transaction ID or user ID.");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Endpoint not found.");
        }
    }
}