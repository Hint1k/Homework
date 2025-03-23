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
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code TransactionServlet} class is a servlet that handles HTTP requests related to transaction operations,
 * such as creating, retrieving, updating, and deleting transactions. It validates incoming JSON data,
 * interacts with services for business logic, and returns appropriate responses.
 */
@WebServlet("/api/transactions/*")
public class TransactionServlet extends HttpServlet {

    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;
    private final ValidationUtils validationUtils;

    /**
     * Constructs a new instance of {@code TransactionServlet} with the required dependencies.
     *
     * @param transactionService the service responsible for transaction-related operations
     * @param objectMapper       the object mapper for JSON serialization and deserialization
     * @param validationUtils    the utility for validating incoming JSON data
     */
    public TransactionServlet(TransactionService transactionService, ObjectMapper objectMapper,
                              ValidationUtils validationUtils) {
        this.transactionService = transactionService;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
        this.validationUtils = validationUtils;
    }

    /**
     * Handles POST requests for creating a new transaction.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if ("/".equals(pathInfo)) {
            try {
                String json = readRequestBody(request);
                TransactionDto transactionDto = validationUtils.validateTransactionJson(json, Mode.TRANSACTION_CREATE);
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
                        sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                "Failed to retrieve transaction details.");
                    }
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                            "Failed to create transaction.");
                }
            } catch (ValidationException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            } catch (Exception e) {
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "An error occurred while creating the transaction.");
            }
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    /**
     * Handles GET requests for retrieving paginated transactions or a specific transaction by ID.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
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
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, objectMapper.writeValueAsString(Map.of(
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
                    sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                            "Transaction not found or you are not the owner of the transaction.");
                }
            } catch (NumberFormatException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid transaction ID.");
            }
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    /**
     * Handles PUT requests for updating an existing transaction.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
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
                        .validateTransactionJson(json, Mode.TRANSACTION_UPDATE, transactionIdString);
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
                        sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                "Failed to retrieve transaction details.");
                    }
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                            "Failed to update transaction or you are not the owner of the transaction.");
                }
            } catch (NumberFormatException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid transaction ID.");
            } catch (ValidationException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            } catch (Exception e) {
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "An error occurred while updating the transaction.");
            }
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    /**
     * Handles DELETE requests for deleting a transaction by its ID.
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
                UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
                Long userId = userDto.getUserId();
                String transactionIdString = pathInfo.substring(1);
                Long transactionId = validationUtils.parseTransactionId(transactionIdString, Mode.TRANSACTION_DELETE);
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
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                            "Failed to delete transaction or you are not the owner of the transaction.");
                }
            } catch (NumberFormatException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid transaction ID");
            }
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
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
        StringBuilder json = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
        }
        return json.toString();
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