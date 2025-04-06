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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.demo.finance.domain.utils.SwaggerExamples.Admin.INVALID_SIZE_RESPONSE;
import static com.demo.finance.domain.utils.SwaggerExamples.Transaction.CREATE_TRANSACTION_REQUEST;
import static com.demo.finance.domain.utils.SwaggerExamples.Transaction.CREATE_TRANSACTION_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Transaction.DELETE_TRANSACTION_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Transaction.GET_TRANSACTIONS_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Transaction.GET_TRANSACTION_SUCCESS;
import static com.demo.finance.domain.utils.SwaggerExamples.Transaction.INVALID_TRANSACTION_ID_RESPONSE;
import static com.demo.finance.domain.utils.SwaggerExamples.Transaction.INVALID_TRANSACTION_TYPE_RESPONSE;
import static com.demo.finance.domain.utils.SwaggerExamples.Transaction.MISSING_TRANSACTION_FIELD_RESPONSE;
import static com.demo.finance.domain.utils.SwaggerExamples.Transaction.TRANSACTION_NOT_FOUND_RESPONSE;
import static com.demo.finance.domain.utils.SwaggerExamples.Transaction.UPDATE_TRANSACTION_REQUEST;
import static com.demo.finance.domain.utils.SwaggerExamples.Transaction.UPDATE_TRANSACTION_SUCCESS;

/**
 * REST controller for managing user transactions.
 * <p>
 * Provides endpoints to create, retrieve, update, and delete transactions
 * for the currently authenticated user. All operations validate input data,
 * enforce ownership constraints, and return standardized responses.
 * <p>
 * This controller uses the {@link TransactionService} to perform business logic,
 * {@link ValidationUtils} to validate inputs, and {@link TransactionMapper} to map
 * between domain and DTO objects. Responses are built using helper methods inherited
 * from {@link BaseController}.
 * <p>
 * Accessible under the path <code>/api/transactions</code>.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController extends BaseController {

    private final TransactionService transactionService;
    private final ValidationUtils validationUtils;
    private final TransactionMapper transactionMapper;

    /**
     * Creates a new transaction for the currently authenticated user.
     * <p>
     * This method validates the input transaction data, creates the transaction
     * using the {@link TransactionService}, and returns a success or error response
     * depending on the outcome. If successful, the created transaction details are
     * returned in the response.
     * </p>
     *
     * @param transactionDtoNew the transaction data to be created
     * @param currentUser       the currently authenticated user
     * @return a {@link ResponseEntity} containing the result of the operation
     */
    @PostMapping
    @Operation(summary = "Create transaction", description = "Creates a new transaction")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Transaction data", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TransactionDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = CREATE_TRANSACTION_REQUEST)))
    @ApiResponse(responseCode = "201", description = "Transaction created successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TransactionDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = CREATE_TRANSACTION_SUCCESS)))
    @ApiResponse(responseCode = "400", description = "Bad request - Invalid transaction type", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "InvalidTransactionType",
            value = INVALID_TRANSACTION_TYPE_RESPONSE)))
    public ResponseEntity<Map<String, Object>> createTransaction(
            @RequestBody TransactionDto transactionDtoNew, @RequestAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            TransactionDto transactionDto =
                    validationUtils.validateRequest(transactionDtoNew, Mode.TRANSACTION_CREATE);
            Long transactionId = transactionService.createTransaction(transactionDto, userId);
            if (transactionId != null) {
                Transaction transaction = transactionService.getTransaction(transactionId);
                if (transaction != null) {
                    TransactionDto transactionDtoCreated = transactionMapper.toDto(transaction);
                    return buildSuccessResponse(
                            HttpStatus.CREATED, "Transaction created successfully", transactionDtoCreated);
                }
                return buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve transaction details.");
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to create transaction.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Retrieves paginated transactions for the currently authenticated user.
     * <p>
     * This method accepts pagination parameters (page size and number), validates them,
     * and returns a paginated list of transactions using the {@link TransactionService}.
     * </p>
     *
     * @param paramsNew   the pagination parameters
     * @param currentUser the currently authenticated user
     * @return a {@link ResponseEntity} containing the paginated transactions
     */
    @GetMapping
    @Operation(summary = "Get transactions", description = "Returns paginated transactions")
    @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PaginatedResponse.class),
            examples = @ExampleObject(name = "SuccessResponse", value = GET_TRANSACTIONS_SUCCESS)))
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid size parameter", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "InvalidSize",
            value = INVALID_SIZE_RESPONSE)))
    public ResponseEntity<Map<String, Object>> getPaginatedTransactions(
            @ParameterObject @ModelAttribute PaginationParams paramsNew,
            @Parameter(hidden = true) @RequestAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            PaginationParams params = validationUtils.validateRequest(paramsNew, Mode.PAGE);
            PaginatedResponse<TransactionDto> paginatedResponse =
                    transactionService.getPaginatedTransactionsForUser(userId, params.page(), params.size());
            return buildPaginatedResponse(userId, paginatedResponse);
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request parameters",
                    Map.of("message", e.getMessage()));
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Retrieves a specific transaction by its ID for the currently authenticated user.
     * <p>
     * This method checks if the transaction exists and belongs to the authenticated user.
     * If the transaction is found, its details are returned. Otherwise, an error response
     * is returned indicating that the transaction was not found or the user is not authorized.
     * </p>
     *
     * @param transactionId the ID of the transaction to retrieve
     * @param currentUser   the currently authenticated user
     * @return a {@link ResponseEntity} containing the transaction details or error message
     */
    @GetMapping("/{transactionId}")
    @Operation(summary = "Get transaction", description = "Returns transaction by ID")
    @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TransactionDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = GET_TRANSACTION_SUCCESS)))
    @ApiResponse(responseCode = "404", description = "Not Found - Transaction not found", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "TransactionNotFound",
            value = TRANSACTION_NOT_FOUND_RESPONSE)))
    public ResponseEntity<Map<String, Object>> getTransactionById(
            @PathVariable("transactionId") String transactionId,
            @Parameter(hidden = true) @RequestAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            Long transactionIdLong = validationUtils.parseLong(transactionId);
            Transaction transaction =
                    transactionService.getTransactionByUserIdAndTransactionId(userId, transactionIdLong);
            if (transaction != null) {
                TransactionDto transactionDto = transactionMapper.toDto(transaction);
                return buildSuccessResponse(HttpStatus.OK, "Transaction found successfully", transactionDto);
            }
            return buildErrorResponse(HttpStatus.NOT_FOUND,
                    "Transaction not found or you are not the owner of the transaction.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Updates an existing transaction for the currently authenticated user.
     * <p>
     * This method validates the input transaction data, checks ownership of the transaction,
     * and updates it using the {@link TransactionService}. If successful, the updated transaction
     * details are returned. If the update fails, an error response is returned.
     * </p>
     *
     * @param transactionId     the ID of the transaction to update
     * @param transactionDtoNew the updated transaction data
     * @param currentUser       the currently authenticated user
     * @return a {@link ResponseEntity} containing the result of the update operation
     */
    @PutMapping("/{transactionId}")
    @Operation(summary = "Update transaction", description = "Updates existing transaction")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated transaction data", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TransactionDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = UPDATE_TRANSACTION_REQUEST)))
    @ApiResponse(responseCode = "200", description = "Transaction updated successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TransactionDto.class),
            examples = @ExampleObject(name = "SuccessResponse", value = UPDATE_TRANSACTION_SUCCESS)))
    @ApiResponse(responseCode = "400", description = "Bad Request - Missing transaction field ", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "ValidationError",
            value = MISSING_TRANSACTION_FIELD_RESPONSE)))
    public ResponseEntity<Map<String, Object>> updateTransaction(
            @PathVariable("transactionId") String transactionId, @RequestBody TransactionDto transactionDtoNew,
            @RequestAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            Long transactionIdLong = validationUtils.parseLong(transactionId);
            TransactionDto transactionDto =
                    validationUtils.validateRequest(transactionDtoNew, Mode.TRANSACTION_UPDATE);
            transactionDto.setTransactionId(transactionIdLong);
            boolean success = transactionService.updateTransaction(transactionDto, userId);
            if (success) {
                Transaction transaction = transactionService.getTransaction(transactionDto.getTransactionId());
                if (transaction != null) {
                    TransactionDto transactionDtoUpdated = transactionMapper.toDto(transaction);
                    return buildSuccessResponse(
                            HttpStatus.OK, "Transaction updated successfully", transactionDtoUpdated);
                }
                return buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve transaction details.");
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST,
                    "Failed to update transaction or you are not the owner of the transaction.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Deletes a specific transaction by its ID for the currently authenticated user.
     * <p>
     * This method validates the transaction ID, checks ownership of the transaction,
     * and deletes it using the {@link TransactionService}. If the transaction is successfully
     * deleted, a success response is returned. If not, an error response is returned.
     * </p>
     *
     * @param transactionId the ID of the transaction to delete
     * @param currentUser   the currently authenticated user
     * @return a {@link ResponseEntity} containing the result of the delete operation
     */
    @DeleteMapping("/{transactionId}")
    @Operation(summary = "Delete transaction", description = "Deletes transaction by ID")
    @ApiResponse(responseCode = "200", description = "Transaction deleted successfully", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Long.class),
            examples = @ExampleObject(name = "SuccessResponse", value = DELETE_TRANSACTION_SUCCESS)))
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid transaction ID format", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "InvalidTransactionId",
            value = INVALID_TRANSACTION_ID_RESPONSE)))
    public ResponseEntity<Map<String, Object>> deleteTransaction(
            @PathVariable("transactionId") String transactionId,
            @Parameter(hidden = true) @RequestAttribute("currentUser") UserDto currentUser) {
        try {
            Long userId = currentUser.getUserId();
            Long transactionIdLong = validationUtils.parseLong(transactionId);
            boolean success = transactionService.deleteTransaction(userId, transactionIdLong);
            if (success) {
                return buildSuccessResponse(HttpStatus.OK, "Transaction deleted successfully",
                        Map.of("transactionId", transactionIdLong));
            }
            return buildErrorResponse(HttpStatus.BAD_REQUEST,
                    "Failed to delete transaction or you are not the owner of the transaction.");
        } catch (ValidationException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}