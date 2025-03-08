package com.demo.finance.out.service;

import com.demo.finance.domain.model.Budget;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.BudgetRepository;
import com.demo.finance.out.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceImplTest {

    @Mock private BudgetRepository budgetRepository;
    @Mock private TransactionRepository transactionRepository;
    @InjectMocks private BudgetServiceImpl budgetService;

    @Test
    void setMonthlyBudget_savesSuccessfully() {
        Long userId = 1L;
        double limit = 1000.0;
        Budget budget = new Budget(userId, limit);

        when(budgetRepository.save(budget)).thenReturn(true);

        boolean result = budgetService.setMonthlyBudget(userId, limit);

        assertThat(result).isTrue();
        verify(budgetRepository).save(budget);
    }

    @Test
    void getBudget_existingBudget_returnsBudget() {
        Long userId = 1L;
        Budget budget = new Budget(userId, 1000.0);
        when(budgetRepository.findByUserId(userId)).thenReturn(Optional.of(budget));

        Optional<Budget> result = budgetService.getBudget(userId);

        assertThat(result).contains(budget);
    }

    @Test
    void calculateExpensesForMonth_sumsTransactionsCorrectly() {
        Long userId = 1L;
        YearMonth currentMonth = YearMonth.of(2025, 3);
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();
        List<Transaction> transactions = List.of(
                new Transaction(1L, userId, 50.0, "Groceries", LocalDate.of(2025,
                        3, 5), "Food shopping", Type.EXPENSE),
                new Transaction(2L, userId, 100.0, "Transport", LocalDate.of(2025,
                        3, 10), "Taxi fare", Type.EXPENSE)
        );

        when(transactionRepository.findFiltered(userId, startOfMonth, endOfMonth, null, Type.EXPENSE))
                .thenReturn(transactions);

        double result = budgetService.calculateExpensesForMonth(userId, currentMonth);

        assertThat(result).isEqualTo(150.0);
    }
}