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
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * The {@code TransactionServlet} class is a servlet that handles HTTP requests related to transaction operations,
 * such as creating, retrieving, updating, and deleting transactions. It extends the {@code BaseServlet} to reuse common functionality.
 */
@WebServlet("/api/transactions/*")
public class TransactionServlet extends BaseServlet {

    private final TransactionService transactionService;

    /**
     * Constructs a new instance of {@code TransactionServlet} with the required dependencies.
     *
     * @param transactionService the service responsible for transaction-related operations
     * @param objectMapper       the object mapper for JSON serialization and deserialization
     * @param validationUtils    the utility for validating incoming JSON data
     */
    public TransactionServlet(TransactionService transactionService, ObjectMapper objectMapper,
                              ValidationUtils validationUtils) {
        super(validationUtils, objectMapper);
        this.transactionService = transactionService;
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
            handleCreateTransaction(request, response);
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
            handleGetPaginatedTransactions(request, response);
        } else if (pathInfo.startsWith("/")) {
            handleGetTransactionById(request, response, pathInfo);
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
            handleUpdateTransaction(request, response, pathInfo);
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
            handleDeleteTransaction(request, response, pathInfo);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found.");
        }
    }

    /**
     * Handles POST requests for creating a new transaction.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleCreateTransaction(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String json = readRequestBody(request);
            Long userId = ((UserDto) request.getSession().getAttribute("currentUser")).getUserId();
            TransactionDto transactionDto = validationUtils
                    .validateJson(json, Mode.TRANSACTION_CREATE, TransactionDto.class);
            Long transactionId = transactionService.createTransaction(transactionDto, userId);
            if (transactionId != null) {
                Transaction transaction = transactionService.getTransaction(transactionId);
                if (transaction != null) {
                    TransactionDto transactionDtoCreated = TransactionMapper.INSTANCE.toDto(transaction);
                    sendSuccessResponse(response, HttpServletResponse.SC_CREATED,
                            "Transaction created successfully", transactionDtoCreated);
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
    }

    /**
     * Handles GET requests for retrieving paginated transactions.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleGetPaginatedTransactions(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Long userId = ((UserDto) request.getSession().getAttribute("currentUser")).getUserId();
            PaginationParams params = getValidatedPaginationParams(request);
            PaginatedResponse<TransactionDto> paginatedResponse = transactionService
                    .getPaginatedTransactionsForUser(userId, params.page(), params.size());
            sendPaginatedResponseWithMetadata(response, userId, paginatedResponse);
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request parameters",
                    Map.of("message", e.getMessage()));
        }
    }

    /**
     * Handles GET requests for retrieving a specific transaction by ID.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @param pathInfo the path info from the request
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleGetTransactionById(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws IOException {
        try {
            UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
            Long userId = userDto.getUserId();
            Long transactionId = validationUtils.parseLong(pathInfo.substring(1));
            Transaction transaction = transactionService
                    .getTransactionByUserIdAndTransactionId(userId, transactionId);
            if (transaction != null) {
                TransactionDto transactionDto = TransactionMapper.INSTANCE.toDto(transaction);
                sendSuccessResponse(response, HttpServletResponse.SC_OK,
                        "Transaction found successfully", transactionDto);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                        "Transaction not found or you are not the owner of the transaction.");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid transaction ID.");
        }
    }

    /**
     * Handles PUT requests for updating an existing transaction.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @param pathInfo the path info from the request
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleUpdateTransaction(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws IOException {
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
                    sendSuccessResponse(response, HttpServletResponse.SC_OK,
                            "Transaction updated successfully", transactionDtoUpdated);
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
    }

    /**
     * Handles DELETE requests for deleting a transaction by its ID.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @param pathInfo the path info from the request
     * @throws IOException if an I/O error occurs during request processing
     */
    private void handleDeleteTransaction(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws IOException {
        try {
            UserDto userDto = (UserDto) request.getSession().getAttribute("currentUser");
            Long userId = userDto.getUserId();
            String transactionIdString = pathInfo.substring(1);
            Long transactionId = validationUtils.parseLong(transactionIdString);
            boolean success = transactionService.deleteTransaction(userId, transactionId);
            if (success) {
                sendSuccessResponse(response, HttpServletResponse.SC_OK,
                        "Transaction deleted successfully", transactionId);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Failed to delete transaction or you are not the owner of the transaction.");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid transaction ID");
        }
    }
}