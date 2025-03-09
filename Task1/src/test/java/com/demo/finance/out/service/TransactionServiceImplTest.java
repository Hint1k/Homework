package com.demo.finance.out.service;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock private TransactionRepository transactionRepository;
    @InjectMocks private TransactionServiceImpl transactionService;

    @Test
    void testCreateTransaction_validTransaction_savesSuccessfully() {
        Long userId = 1L;
        double amount = 100.0;
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
    void testGetTransaction_existingTransaction_returnsTransaction() {
        Long transactionId = 1L;
        Transaction transaction = new Transaction(transactionId, 2L, 100.0, "Groceries",
                LocalDate.now(), "Shopping", Type.EXPENSE);
        when(transactionRepository.findByTransactionId(transactionId)).thenReturn(transaction);

        Transaction result = transactionService.getTransaction(transactionId);

        assertThat(result).isEqualTo(transaction);
    }

    @Test
    void testDeleteTransaction_transactionExists_deletesSuccessfully() {
        Long transactionId = 1L;
        Long userId = 2L;
        when(transactionRepository.findByUserIdAndTransactionId(userId, transactionId))
                .thenReturn(Optional.of(new Transaction(transactionId, userId, 100.0, "Groceries",
                        LocalDate.now(), "Shopping", Type.EXPENSE)));
        when(transactionRepository.delete(transactionId)).thenReturn(true);

        boolean result = transactionService.deleteTransaction(userId, transactionId);

        assertThat(result).isTrue();
        verify(transactionRepository).delete(transactionId);
    }

    @Test
    void testCreateTransaction_negativeAmount_throwsException() {
        Long userId = 1L;
        double amount = -50.0;
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
    void testGetTransaction_nonExistingTransaction_returnsNull() {
        Long transactionId = 99L;
        when(transactionRepository.findByTransactionId(transactionId)).thenReturn(null);

        Transaction result = transactionService.getTransaction(transactionId);

        assertThat(result).isNull();
    }

    @Test
    void testUpdateTransaction_transactionDoesNotExist_returnsFalse() {
        Long transactionId = 1L;
        Long userId = 2L;
        when(transactionRepository.findByUserIdAndTransactionId(transactionId, userId))
                .thenReturn(Optional.empty());

        boolean result = transactionService
                .updateTransaction(transactionId, userId, 500.0, "Bills", "Electricity");

        assertThat(result).isFalse();
        verify(transactionRepository, never()).update(any());
    }

    @Test
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
    void testGetFilteredTransactions_matchingTransactions_returnsList() {
        Long userId = 1L;
        LocalDate from = LocalDate.of(2025, 3, 1);
        LocalDate to = LocalDate.of(2025, 3, 31);
        String category = "Food";
        Type type = Type.EXPENSE;

        List<Transaction> transactions = List.of(
                new Transaction(1L, userId, 50.0, "Food",
                        LocalDate.of(2025, 3, 10), "Lunch", Type.EXPENSE),
                new Transaction(2L, userId, 80.0, "Food",
                        LocalDate.of(2025, 3, 15), "Dinner", Type.EXPENSE)
        );

        when(transactionRepository.findFiltered(userId, from, to, category, type)).thenReturn(transactions);

        List<Transaction> result = transactionService.getFilteredTransactions(userId, from, to, category, type);

        assertThat(result).hasSize(2).containsAll(transactions);
    }

    @Test
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
    void testUpdateTransaction_transactionExists_updatesSuccessfully() {
        Long transactionId = 1L;
        Long userId = 2L;
        double newAmount = 200.0;
        String newCategory = "Bills";
        String newDescription = "Electricity";

        Transaction existingTransaction = new Transaction(transactionId, userId, 100.0, "Groceries",
                LocalDate.now(), "Shopping", Type.EXPENSE);

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