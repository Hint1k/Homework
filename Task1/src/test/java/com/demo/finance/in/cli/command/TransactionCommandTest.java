package com.demo.finance.in.cli.command;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.MaxRetriesReachedException;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.domain.utils.ValidationUtils;
import com.demo.finance.in.cli.CommandContext;
import com.demo.finance.in.controller.TransactionController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class TransactionCommandTest {

    @Mock private CommandContext context;
    @Mock private ValidationUtils validationUtils;
    @Mock private TransactionController transactionController;
    @InjectMocks private TransactionCommand transactionCommand;
    @Mock private User currentUser;

    @BeforeEach
    void setUp() {
        lenient().when(context.getCurrentUser()).thenReturn(currentUser);
        lenient().when(currentUser.getUserId()).thenReturn(2L);
        when(context.getTransactionController()).thenReturn(transactionController);
    }

    @Test
    @DisplayName("Add transaction - Success")
    void testAddTransaction_Success() {
        when(validationUtils.promptForPositiveBigDecimal(any(), any())).thenReturn(new BigDecimal(100));
        when(validationUtils.promptForNonEmptyString(eq("Enter Category: "), any())).thenReturn("Food");
        when(validationUtils.promptForValidDate(any(), any()))
                .thenReturn(LocalDate.of(2025, 3, 10));
        when(validationUtils.promptForNonEmptyString(eq("Enter Description: "), any())).thenReturn("Lunch");
        when(validationUtils.promptForTransactionType(any())).thenReturn(Type.EXPENSE);

        transactionCommand.addTransaction();

        verify(transactionController, times(1))
                .addTransaction(eq(2L), eq(new BigDecimal(100)), eq("Food"),
                        eq("2025-03-10"), eq("Lunch"), eq(Type.EXPENSE));
    }

    @Test
    @DisplayName("Delete transaction - Success")
    void testDeleteTransaction_Success() {
        when(validationUtils.promptForPositiveLong(any(), any())).thenReturn(1L);
        when(transactionController.deleteTransaction(anyLong(), anyLong())).thenReturn(true);

        transactionCommand.deleteTransaction();

        verify(transactionController, times(1)).deleteTransaction(anyLong(), eq(1L));
    }

    @Test
    @DisplayName("View transactions by user ID - Success")
    void testViewTransactionsByUserId() {
        when(transactionController.getTransactionsByUserId(anyLong()))
                .thenReturn(List.of(new Transaction(1L, 1L, new BigDecimal(50),
                        "Groceries", LocalDate.now(), "Weekly shopping", Type.EXPENSE)));

        transactionCommand.viewTransactionsByUserId();

        verify(transactionController, times(1)).getTransactionsByUserId(anyLong());
    }

    @Test
    @DisplayName("Add transaction - Invalid amount logs error")
    void testAddTransaction_InvalidAmount_LogsError() {
        when(validationUtils.promptForPositiveBigDecimal(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid amount"));

        transactionCommand.addTransaction();

        verify(validationUtils).promptForPositiveBigDecimal(any(), any());
        verify(context.getTransactionController(), never())
                .addTransaction(anyLong(), any(BigDecimal.class), anyString(), anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Update transaction - Invalid transaction ID logs error")
    void testUpdateTransaction_InvalidTransactionId_LogsError() {
        when(validationUtils.promptForPositiveLong(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid transaction ID"));

        transactionCommand.updateTransaction();

        verify(validationUtils).promptForPositiveLong(any(), any());
        verify(context.getTransactionController(), never()).getTransaction(anyLong());
    }

    @Test
    @DisplayName("Update transaction - Transaction not found returns error")
    void testUpdateTransaction_TransactionNotFound_ReturnsError() {
        when(validationUtils.promptForPositiveLong(any(), any())).thenReturn(1L);
        when(transactionController.getTransaction(1L)).thenReturn(null);

        transactionCommand.updateTransaction();

        verify(transactionController, never())
                .updateTransaction(anyLong(), anyLong(), any(BigDecimal.class), anyString(), anyString());
    }

    @Test
    @DisplayName("Delete transaction - Invalid transaction ID logs error")
    void testDeleteTransaction_InvalidTransactionId_LogsError() {
        when(validationUtils.promptForPositiveLong(any(), any()))
                .thenThrow(new MaxRetriesReachedException("Invalid transaction ID"));

        transactionCommand.deleteTransaction();

        verify(validationUtils).promptForPositiveLong(any(), any());
        verify(context.getTransactionController(), never()).deleteTransaction(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Filter transactions - No filters returns all transactions")
    void testFilterTransactions_NoFilters_ReturnsAllTransactions() {
        when(validationUtils.promptForOptionalDate(any(), any())).thenReturn(null);
        when(validationUtils.promptForOptionalString(any(), any())).thenReturn(null);
        when(validationUtils.promptForOptionalTransactionType(any())).thenReturn(null);

        List<Transaction> transactions = List.of(
                new Transaction(1L, 2L, new BigDecimal(50), "Groceries",
                        LocalDate.now(), "Weekly shopping", Type.EXPENSE),
                new Transaction(2L, 2L, new BigDecimal(100), "Transport",
                        LocalDate.now(), "Taxi fare", Type.EXPENSE)
        );

        when(transactionController.filterTransactions(2L, null, null, null, null))
                .thenReturn(transactions);

        transactionCommand.filterTransactions();

        verify(transactionController, times(1))
                .filterTransactions(2L, null, null, null, null);
    }
}