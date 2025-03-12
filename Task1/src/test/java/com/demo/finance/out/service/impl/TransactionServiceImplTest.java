package com.demo.finance.out.service.impl;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock private TransactionRepository transactionRepository;
    @InjectMocks private TransactionServiceImpl transactionService;

    @Test
    @DisplayName("Create transaction - valid transaction - saves successfully")
    void testCreateTransaction_validTransaction_savesSuccessfully() {
        Long userId = 1L;
        BigDecimal amount = new BigDecimal(100);
        String category = "Food";
        String date = "2025-03-05";
        String description = "Dinner";
        Type type = Type.EXPENSE;
        Long transactionId = 10L;

        when(transactionRepository.generateNextId()).thenReturn(transactionId);
        doNothing().when(transactionRepository).save(any());

        assertThatCode(() -> transactionService.createTransaction(userId, amount, category, date, description, type))
                .doesNotThrowAnyException();

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Get transaction - existing transaction - returns transaction")
    void testGetTransaction_existingTransaction_returnsTransaction() {
        Long transactionId = 1L;
        Transaction transaction = new Transaction(transactionId, 2L, new BigDecimal(100),
                "Groceries", LocalDate.now(), "Shopping", Type.EXPENSE);
        when(transactionRepository.findByTransactionId(transactionId)).thenReturn(transaction);

        Transaction result = transactionService.getTransaction(transactionId);

        assertThat(result).isEqualTo(transaction);
    }

    @Test
    @DisplayName("Delete transaction - transaction exists - deletes successfully")
    void testDeleteTransaction_transactionExists_deletesSuccessfully() {
        Long transactionId = 1L;
        Long userId = 2L;
        when(transactionRepository.findByUserIdAndTransactionId(userId, transactionId))
                .thenReturn(Optional.of(new Transaction(transactionId, userId, new BigDecimal(100),
                        "Groceries", LocalDate.now(), "Shopping", Type.EXPENSE)));
        when(transactionRepository.delete(transactionId)).thenReturn(true);

        boolean result = transactionService.deleteTransaction(userId, transactionId);

        assertThat(result).isTrue();
        verify(transactionRepository).delete(transactionId);
    }

    @Test
    @DisplayName("Create transaction - negative amount - throws exception")
    void testCreateTransaction_negativeAmount_throwsException() {
        Long userId = 1L;
        BigDecimal amount = new BigDecimal(-50);
        String category = "Food";
        String date = "2025-03-05";
        String description = "Dinner";
        Type type = Type.EXPENSE;

        assertThatThrownBy(() -> transactionService
                .createTransaction(userId, amount, category, date, description, type))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Amount must be positive.");
    }

    @Test
    @DisplayName("Get transaction - non-existing transaction - returns null")
    void testGetTransaction_nonExistingTransaction_returnsNull() {
        Long transactionId = 99L;
        when(transactionRepository.findByTransactionId(transactionId)).thenReturn(null);

        Transaction result = transactionService.getTransaction(transactionId);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Update transaction - transaction does not exist - returns false")
    void testUpdateTransaction_transactionDoesNotExist_returnsFalse() {
        Long transactionId = 1L;
        Long userId = 2L;
        when(transactionRepository.findByUserIdAndTransactionId(transactionId, userId))
                .thenReturn(Optional.empty());

        boolean result = transactionService.updateTransaction(transactionId, userId,
                new BigDecimal(500), "Bills", "Electricity");

        assertThat(result).isFalse();
        verify(transactionRepository, never()).update(any());
    }

    @Test
    @DisplayName("Delete transaction - transaction does not exist - returns false")
    void testDeleteTransaction_transactionDoesNotExist_returnsFalse() {
        Long transactionId = 99L;
        Long userId = 2L;
        when(transactionRepository.findByUserIdAndTransactionId(userId, transactionId))
                .thenReturn(Optional.empty());

        boolean result = transactionService.deleteTransaction(userId, transactionId);

        assertThat(result).isFalse();
        verify(transactionRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Get filtered transactions - matching transactions - returns list")
    void testGetFilteredTransactions_matchingTransactions_returnsList() {
        Long userId = 1L;
        LocalDate from = LocalDate.of(2025, 3, 1);
        LocalDate to = LocalDate.of(2025, 3, 31);
        String category = "Food";
        Type type = Type.EXPENSE;

        List<Transaction> transactions = List.of(
                new Transaction(1L, userId, new BigDecimal(50), "Food",
                        LocalDate.of(2025, 3, 10), "Lunch", Type.EXPENSE),
                new Transaction(2L, userId, new BigDecimal(80), "Food",
                        LocalDate.of(2025, 3, 15), "Dinner", Type.EXPENSE)
        );

        when(transactionRepository.findFiltered(userId, from, to, category, type)).thenReturn(transactions);

        List<Transaction> result = transactionService.getFilteredTransactions(userId, from, to, category, type);

        assertThat(result).hasSize(2).containsAll(transactions);
    }

    @Test
    @DisplayName("Get filtered transactions - no matching transactions - returns empty list")
    void testGetFilteredTransactions_noMatchingTransactions_returnsEmptyList() {
        Long userId = 1L;
        LocalDate from = LocalDate.of(2025, 3, 1);
        LocalDate to = LocalDate.of(2025, 3, 31);
        String category = "Entertainment";
        Type type = Type.EXPENSE;

        when(transactionRepository.findFiltered(userId, from, to, category, type)).thenReturn(List.of());

        List<Transaction> result = transactionService.getFilteredTransactions(userId, from, to, category, type);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Update transaction - transaction exists - updates successfully")
    void testUpdateTransaction_transactionExists_updatesSuccessfully() {
        Long transactionId = 1L;
        Long userId = 2L;
        BigDecimal newAmount = new BigDecimal(200);
        String newCategory = "Bills";
        String newDescription = "Electricity";

        Transaction existingTransaction = new Transaction(transactionId, userId, new BigDecimal(100),
                "Groceries", LocalDate.now(), "Shopping", Type.EXPENSE);

        when(transactionRepository.findByUserIdAndTransactionId(transactionId, userId))
                .thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.update(any(Transaction.class))).thenReturn(true);

        boolean result = transactionService
                .updateTransaction(transactionId, userId, newAmount, newCategory, newDescription);

        assertThat(result).isTrue();
        assertThat(existingTransaction.getAmount()).isEqualTo(newAmount);
        assertThat(existingTransaction.getCategory()).isEqualTo(newCategory);
        assertThat(existingTransaction.getDescription()).isEqualTo(newDescription);
        verify(transactionRepository).update(existingTransaction);
    }
}