package com.demo.finance.in.controller;

import com.demo.finance.domain.mapper.TransactionMapper;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.out.service.TransactionService;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.utils.Mode;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.exception.custom.ValidationException;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.domain.utils.PaginationParams;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    private MockMvc mockMvc;
    @Mock
    private TransactionService transactionService;
    @Mock
    private ValidationUtils validationUtils;
    @Mock
    private TransactionMapper transactionMapper;
    @InjectMocks
    private TransactionController transactionController;
    private UserDto currentUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
        currentUser = Instancio.create(UserDto.class);
        currentUser.setUserId(1L);
    }

    private TransactionDto createTransactionDto(Long id, String description) {
        TransactionDto dto = Instancio.create(TransactionDto.class);
        dto.setTransactionId(id);
        dto.setDescription(description);
        return dto;
    }

    private Transaction createTransaction(String description) {
        Transaction transaction = Instancio.create(Transaction.class);
        transaction.setTransactionId(1L);
        transaction.setDescription(description);
        return transaction;
    }

    @Test
    @DisplayName("Create transaction - Success scenario")
    void testCreateTransaction_Success() throws Exception {
        TransactionDto validatedDto = createTransactionDto(null, "Test transaction");
        Transaction createdTransaction = createTransaction("Test transaction");
        TransactionDto responseDto = createTransactionDto(1L, "Test transaction");

        when(validationUtils.validateRequest(any(), eq(Mode.TRANSACTION_CREATE))).thenReturn(validatedDto);
        when(transactionService.createTransaction(validatedDto, 1L)).thenReturn(1L);
        when(transactionService.getTransaction(1L)).thenReturn(createdTransaction);
        when(transactionMapper.toDto(createdTransaction)).thenReturn(responseDto);

        mockMvc.perform(post("/api/transactions")
                        .requestAttr("currentUser", currentUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Test transaction\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Transaction created successfully"))
                .andExpect(jsonPath("$.data.description").value("Test transaction"));

        verify(validationUtils, times(1)).validateRequest(any(), eq(Mode.TRANSACTION_CREATE));
        verify(transactionService, times(1)).createTransaction(validatedDto, 1L);
        verify(transactionService, times(1)).getTransaction(1L);
        verify(transactionMapper, times(1)).toDto(createdTransaction);
    }

    @Test
    @DisplayName("Get paginated transactions - Success scenario")
    void testGetPaginatedTransactions_Success() throws Exception {
        PaginationParams params = new PaginationParams(0, 10);
        PaginatedResponse<TransactionDto> response = new PaginatedResponse<>(List.of(createTransactionDto(
                1L, "Test")), 0, 10, 1, 1);

        when(validationUtils.validateRequest(any(), eq(Mode.PAGE))).thenReturn(params);
        when(transactionService.getPaginatedTransactionsForUser(1L, 0, 10)).thenReturn(response);

        mockMvc.perform(get("/api/transactions?page=1&size=10")
                        .requestAttr("currentUser", currentUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].description").value("Test"));

        verify(validationUtils, times(1)).validateRequest(any(), eq(Mode.PAGE));
        verify(transactionService, times(1))
                .getPaginatedTransactionsForUser(1L, 0, 10);
    }

    @Test
    @DisplayName("Get transaction by ID - Success scenario")
    void testGetTransactionById_Success() throws Exception {
        Transaction transaction = createTransaction("Test transaction");
        TransactionDto responseDto = createTransactionDto(1L, "Test transaction");

        when(validationUtils.parseLong("1")).thenReturn(1L);
        when(transactionService.getTransactionByUserIdAndTransactionId(1L, 1L))
                .thenReturn(transaction);
        when(transactionMapper.toDto(transaction)).thenReturn(responseDto);

        mockMvc.perform(get("/api/transactions/1")
                        .requestAttr("currentUser", currentUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.description").value("Test transaction"));

        verify(validationUtils, times(1)).parseLong("1");
        verify(transactionService, times(1))
                .getTransactionByUserIdAndTransactionId(1L, 1L);
        verify(transactionMapper, times(1)).toDto(transaction);
    }

    @Test
    @DisplayName("Update transaction - Success scenario")
    void testUpdateTransaction_Success() throws Exception {
        TransactionDto validatedDto = createTransactionDto(1L, "Updated transaction");
        Transaction updatedTransaction = createTransaction("Updated transaction");
        TransactionDto responseDto = createTransactionDto(1L, "Updated transaction");

        when(validationUtils.parseLong("1")).thenReturn(1L);
        when(validationUtils.validateRequest(any(), eq(Mode.TRANSACTION_UPDATE))).thenReturn(validatedDto);
        when(transactionService.updateTransaction(validatedDto, 1L)).thenReturn(true);
        when(transactionService.getTransaction(1L)).thenReturn(updatedTransaction);
        when(transactionMapper.toDto(updatedTransaction)).thenReturn(responseDto);

        mockMvc.perform(put("/api/transactions/1")
                        .requestAttr("currentUser", currentUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionId\":1,\"description\":\"Updated transaction\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction updated successfully"));

        verify(validationUtils, times(1)).parseLong("1");
        verify(validationUtils, times(1)).validateRequest(any(), eq(Mode.TRANSACTION_UPDATE));
        verify(transactionService, times(1)).updateTransaction(validatedDto, 1L);
        verify(transactionService, times(1)).getTransaction(1L);
        verify(transactionMapper, times(1)).toDto(updatedTransaction);
    }

    @Test
    @DisplayName("Delete transaction - Success scenario")
    void testDeleteTransaction_Success() throws Exception {
        when(validationUtils.parseLong("1")).thenReturn(1L);
        when(transactionService.deleteTransaction(1L, 1L)).thenReturn(true);

        mockMvc.perform(delete("/api/transactions/1")
                        .requestAttr("currentUser", currentUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction deleted successfully"));

        verify(validationUtils, times(1)).parseLong("1");
        verify(transactionService, times(1)).deleteTransaction(1L, 1L);
    }

    @Test
    @DisplayName("Create transaction - Validation failure")
    void testCreateTransaction_ValidationFailure() throws Exception {
        when(validationUtils.validateRequest(any(), eq(Mode.TRANSACTION_CREATE)))
                .thenThrow(new ValidationException("Invalid transaction"));

        mockMvc.perform(post("/api/transactions")
                        .requestAttr("currentUser", currentUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid transaction"));

        verify(validationUtils, times(1)).validateRequest(any(), eq(Mode.TRANSACTION_CREATE));
        verify(transactionService, never()).createTransaction(any(), anyLong());
    }

    @Test
    @DisplayName("Get transaction - Not found")
    void testGetTransactionById_NotFound() throws Exception {
        when(validationUtils.parseLong("1")).thenReturn(1L);
        when(transactionService.getTransactionByUserIdAndTransactionId(1L, 1L)).thenReturn(null);

        mockMvc.perform(get("/api/transactions/1")
                        .requestAttr("currentUser", currentUser))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(
                        "Transaction not found or you are not the owner of the transaction."));

        verify(validationUtils, times(1)).parseLong("1");
        verify(transactionService, times(1))
                .getTransactionByUserIdAndTransactionId(1L, 1L);
        verify(transactionMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Update transaction - Update failed")
    void testUpdateTransaction_Failed() throws Exception {
        TransactionDto validatedDto = createTransactionDto(1L, "Updated transaction");

        when(validationUtils.parseLong("1")).thenReturn(1L);
        when(validationUtils.validateRequest(any(), eq(Mode.TRANSACTION_UPDATE))).thenReturn(validatedDto);
        when(transactionService.updateTransaction(validatedDto, 1L)).thenReturn(false);

        mockMvc.perform(put("/api/transactions/1")
                        .requestAttr("currentUser", currentUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"transactionId\":1,\"description\":\"Updated transaction\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "Failed to update transaction or you are not the owner of the transaction."));

        verify(validationUtils, times(1)).parseLong("1");
        verify(validationUtils, times(1)).validateRequest(any(), eq(Mode.TRANSACTION_UPDATE));
        verify(transactionService, times(1)).updateTransaction(validatedDto, 1L);
        verify(transactionService, never()).getTransaction(anyLong());
        verify(transactionMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Delete transaction - Failed")
    void testDeleteTransaction_Failed() throws Exception {
        when(validationUtils.parseLong("1")).thenReturn(1L);
        when(transactionService.deleteTransaction(1L, 1L)).thenReturn(false);

        mockMvc.perform(delete("/api/transactions/1")
                        .requestAttr("currentUser", currentUser))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "Failed to delete transaction or you are not the owner of the transaction."));

        verify(validationUtils, times(1)).parseLong("1");
        verify(transactionService, times(1)).deleteTransaction(1L, 1L);
    }
}