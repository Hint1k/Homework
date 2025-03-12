package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock private TransactionService transactionService;
    @InjectMocks private TransactionController transactionController;

    @Test
    @DisplayName("Add transaction - Success scenario")
    void testAddTransaction() {
        Long userId = 1L;
        BigDecimal amount = new BigDecimal(100);
        String category = "Groceries";
        String date = "2023-10-01";
        String description = "Weekly shopping";
        Type type = Type.EXPENSE;

        transactionController.addTransaction(userId, amount, category, date, description, type);

        verify(transactionService, times(1))
                .createTransaction(userId, amount, category, date, description, type);
    }

    @Test
    @DisplayName("Get transaction - Success scenario")
    void testGetTransaction() {
        Long transactionId = 1L;
        Transaction mockTransaction = new Transaction(1L, 1L, new BigDecimal(100),
                "Groceries", LocalDate.now(), "Weekly shopping", Type.EXPENSE);

        when(transactionService.getTransaction(transactionId)).thenReturn(mockTransaction);

        Transaction transaction = transactionController.getTransaction(transactionId);

        assertThat(transaction).isEqualTo(mockTransaction);
        verify(transactionService, times(1)).getTransaction(transactionId);
    }

    @Test
    @DisplayName("Get transactions by user ID - Success scenario")
    void testGetTransactionsByUserId() {
        Long userId = 1L;
        List<Transaction> mockTransactions = Arrays.asList(
                new Transaction(1L, 1L, new BigDecimal(100), "Groceries",
                        LocalDate.now(), "Weekly shopping", Type.EXPENSE),
                new Transaction(2L, 1L, new BigDecimal(200), "Salary",
                        LocalDate.now(), "Monthly income", Type.INCOME)
        );

        when(transactionService.getTransactionsByUserId(userId)).thenReturn(mockTransactions);

        List<Transaction> transactions = transactionController.getTransactionsByUserId(userId);

        assertThat(transactions).hasSize(2).containsExactlyElementsOf(mockTransactions);
        verify(transactionService, times(1)).getTransactionsByUserId(userId);
    }

    @Test
    @DisplayName("Update transaction - Success scenario")
    void testUpdateTransaction_Success() {
        Long transactionId = 1L;
        Long userId = 1L;
        BigDecimal amount = new BigDecimal(150);
        String category = "Utilities";
        String description = "Electricity bill";

        when(transactionService.updateTransaction(transactionId, userId, amount, category, description))
                .thenReturn(true);

        boolean updated = transactionController
                .updateTransaction(transactionId, userId, amount, category, description);

        assertThat(updated).isTrue();
        verify(transactionService, times(1))
                .updateTransaction(transactionId, userId, amount, category, description);
    }

    @Test
    @DisplayName("Delete transaction - Success scenario")
    void testDeleteTransaction_Success() {
        Long userId = 1L;
        Long transactionId = 1L;

        when(transactionService.deleteTransaction(userId, transactionId)).thenReturn(true);

        boolean deleted = transactionController.deleteTransaction(userId, transactionId);

        assertThat(deleted).isTrue();
        verify(transactionService, times(1)).deleteTransaction(userId, transactionId);
    }

    @Test
    @DisplayName("Filter transactions - Success scenario")
    void testFilterTransactions() {
        Long userId = 1L;
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 12, 31);
        String category = "Groceries";
        Type type = Type.EXPENSE;

        List<Transaction> mockTransactions = List.of(
                new Transaction(1L, 1L, new BigDecimal(100), "Groceries",
                        LocalDate.of(2023, 5, 1), "Weekly shopping", Type.EXPENSE)
        );

        when(transactionService.getFilteredTransactions(userId, from, to, category, type))
                .thenReturn(mockTransactions);

        List<Transaction> filteredTransactions = transactionController
                .filterTransactions(userId, from, to, category, type);

        assertThat(filteredTransactions).hasSize(1).containsExactlyElementsOf(mockTransactions);
        verify(transactionService, times(1))
                .getFilteredTransactions(userId, from, to, category, type);
    }
}