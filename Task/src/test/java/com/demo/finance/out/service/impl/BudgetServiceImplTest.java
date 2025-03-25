package com.demo.finance.out.service.impl;

import com.demo.finance.domain.model.Budget;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.BudgetRepository;
import com.demo.finance.out.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetServiceImplTest {

    @Mock private BudgetRepository budgetRepository;
    @Mock private TransactionRepository transactionRepository;
    @InjectMocks private BudgetServiceImpl budgetService;

    @Test
    @DisplayName("Set monthly budget - new budget - returns budget")
    void testSetMonthlyBudget_newBudget_returnsBudget() {
        Long userId = 1L;
        BigDecimal limit = new BigDecimal(1000);
        Budget newBudget = new Budget(userId, limit);
        newBudget.setBudgetId(1L);

        when(budgetRepository.findByUserId(userId)).thenReturn(null).thenReturn(newBudget);
        when(budgetRepository.save(any(Budget.class))).thenReturn(true);
        Budget result = budgetService.setMonthlyBudget(userId, limit);

        verify(budgetRepository, times(2)).findByUserId(userId);
        verify(budgetRepository).save(any(Budget.class));

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getMonthlyLimit()).isEqualTo(limit);
    }

    @Test
    @DisplayName("Set monthly budget - update existing - returns updated budget")
    void testSetMonthlyBudget_updateExisting_returnsUpdatedBudget() {
        Long userId = 1L;
        BigDecimal newLimit = new BigDecimal(1500);
        Budget existing = new Budget(userId, new BigDecimal(1000));

        when(budgetRepository.findByUserId(userId)).thenReturn(existing);
        when(budgetRepository.update(existing)).thenReturn(true);
        when(budgetRepository.findByUserId(userId)).thenReturn(existing);

        Budget result = budgetService.setMonthlyBudget(userId, newLimit);

        assertThat(result).isNotNull();
        assertThat(result.getMonthlyLimit()).isEqualTo(newLimit);
        verify(budgetRepository).update(existing);
    }

    @Test
    @DisplayName("Get budget - existing budget - returns budget")
    void testGetBudget_existingBudget_returnsBudget() {
        Long userId = 1L;
        Budget budget = new Budget(userId, new BigDecimal(1000));

        when(budgetRepository.findByUserId(userId)).thenReturn(budget);

        Budget result = budgetService.getBudget(userId);

        assertThat(result).isEqualTo(budget);
    }

    @Test
    @DisplayName("Calculate expenses for month - has transactions - returns sum")
    void testCalculateExpensesForMonth_hasTransactions_returnsSum() {
        Long userId = 1L;
        YearMonth month = YearMonth.of(2025, 3);
        List<Transaction> transactions = List.of(
                new Transaction(1L, userId, new BigDecimal(100), "Food",
                        LocalDate.of(2025, 3, 5), "Groceries", Type.EXPENSE),
                new Transaction(2L, userId, new BigDecimal(200), "Transport",
                        LocalDate.of(2025, 3, 10), "Taxi", Type.EXPENSE)
        );

        when(transactionRepository.findFiltered(
                userId,
                month.atDay(1),
                month.atEndOfMonth(),
                null,
                Type.EXPENSE
        )).thenReturn(transactions);

        BigDecimal result = budgetService.calculateExpensesForMonth(userId, month);

        assertThat(result).isEqualTo(new BigDecimal(300));
    }

    @Test
    @DisplayName("Get budget data - no budget - returns message")
    void testGetBudgetData_noBudget_returnsMessage() {
        Long userId = 1L;
        YearMonth month = YearMonth.now();
        List<Transaction> transactions = List.of(
                new Transaction(1L, userId, new BigDecimal(100), "Food",
                        month.atDay(5), "Groceries", Type.EXPENSE)
        );

        when(budgetRepository.findByUserId(userId)).thenReturn(null);
        when(transactionRepository.findFiltered(
                userId,
                month.atDay(1),
                month.atEndOfMonth(),
                null,
                Type.EXPENSE
        )).thenReturn(transactions);

        Map<String, Object> result = budgetService.getBudgetData(userId);

        assertThat(result.get("message")).isEqualTo("Budget is not set");
        assertThat(((Map<?, ?>) result.get("data")).get("monthlyLimit"))
                .isEqualTo(BigDecimal.ZERO);
        assertThat(((Map<?, ?>) result.get("data")).get("currentExpenses"))
                .isEqualTo(new BigDecimal(100));
    }

    @Test
    @DisplayName("Get budget data - with budget - returns formatted data")
    void testGetBudgetData_withBudget_returnsFormattedData() {
        Long userId = 1L;
        YearMonth month = YearMonth.now();
        Budget budget = new Budget(userId, new BigDecimal(1000));
        List<Transaction> transactions = List.of(
                new Transaction(1L, userId, new BigDecimal(100), "Food",
                        month.atDay(5), "Groceries", Type.EXPENSE)
        );

        when(budgetRepository.findByUserId(userId)).thenReturn(budget);
        when(transactionRepository.findFiltered(
                userId,
                month.atDay(1),
                month.atEndOfMonth(),
                null,
                Type.EXPENSE
        )).thenReturn(transactions);

        Map<String, Object> result = budgetService.getBudgetData(userId);

        assertThat(result.get("formattedBudget")).isEqualTo("Budget: 100.00/1000.00");
        assertThat(((Map<?, ?>) result.get("budgetData")).get("monthlyLimit"))
                .isEqualTo(new BigDecimal(1000));
        assertThat(((Map<?, ?>) result.get("budgetData")).get("currentExpenses"))
                .isEqualTo(new BigDecimal(100));
    }
}