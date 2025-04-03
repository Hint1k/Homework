package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.app.config.DatabaseConfig;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionRepositoryImplTest extends AbstractContainerBaseSetup {

    private TransactionRepositoryImpl repository;

    @BeforeAll
    void setupRepository() {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.init();
        DataSourceManager dataSourceManager = new DataSourceManager(databaseConfig);
        repository = new TransactionRepositoryImpl(dataSourceManager);
    }

    @Test
    @DisplayName("Save and find transaction by ID - Success scenario")
    void testSaveAndFindById() {
        Transaction transaction = new Transaction(null, 1L, new BigDecimal("500.00"),
                "Groceries", LocalDate.of(2025, 3, 10), "Supermarket",
                Type.EXPENSE);
        repository.save(transaction);
        List<Transaction> savedTransactions = repository.findByUserId(1L);
        assertThat(savedTransactions).isNotEmpty();

        Transaction savedTransaction = savedTransactions.get(0);
        Long transactionId = savedTransaction.getTransactionId();

        assertThat(transactionId).isNotNull();

        Transaction found = repository.findById(transactionId);

        assertThat(found).isNotNull();
        assertThat(found.getAmount()).isEqualTo(new BigDecimal("500.00"));
        assertThat(found.getCategory()).isEqualTo("Groceries");
    }

    @Test
    @DisplayName("Update transaction - Success scenario")
    void testUpdateTransaction() {
        Transaction transaction = new Transaction(null, 2L, new BigDecimal("300.00"),
                "Transport", LocalDate.of(2025, 3, 15), "Bus fare",
                Type.EXPENSE);
        repository.save(transaction);

        List<Transaction> savedTransactions = repository.findByUserId(2L);
        assertThat(savedTransactions).isNotEmpty();

        Transaction savedTransaction = savedTransactions.get(0);
        Long transactionId = savedTransaction.getTransactionId();
        assertThat(transactionId).isNotNull();

        Transaction updatedTransaction = new Transaction(transactionId, 2L,
                new BigDecimal("400.00"), "Transport", LocalDate.of(2025, 3, 15),
                "Taxi fare", Type.EXPENSE);
        boolean updated = repository.update(updatedTransaction);

        assertThat(updated).isTrue();
        Transaction found = repository.findById(transactionId);
        assertThat(found).isNotNull();
        assertThat(found.getAmount()).isEqualTo(new BigDecimal("400.00"));
        assertThat(found.getDescription()).isEqualTo("Taxi fare");
    }

    @Test
    @DisplayName("Delete transaction - Success scenario")
    void testDeleteTransaction() {
        Transaction transaction = new Transaction(null, 3L, new BigDecimal("200.00"),
                "Bills", LocalDate.of(2025, 3, 20), "Electricity",
                Type.EXPENSE);
        repository.save(transaction);
        List<Transaction> transactions = repository.findByUserId(3L);
        assertThat(transactions).isNotEmpty();

        Transaction savedTransaction = transactions.get(0);
        Long transactionId = savedTransaction.getTransactionId();

        assertThat(transactionId).isNotNull();

        boolean deleted = repository.delete(transactionId);

        assertThat(deleted).isTrue();
        assertThat(repository.findById(transactionId)).isNull();
    }

    @Test
    @DisplayName("Find by user ID - Transactions exist returns transactions")
    void testFindByUserId_TransactionsExist_ReturnsTransactions() {
        repository.save(new Transaction(null, 4L, new BigDecimal("150.00"), "Dining",
                LocalDate.of(2025, 3, 5), "Restaurant", Type.EXPENSE));
        repository.save(new Transaction(null, 4L, new BigDecimal("80.00"), "Transport",
                LocalDate.of(2025, 3, 7), "Bus fare", Type.EXPENSE));

        List<Transaction> transactions = repository.findByUserId(4L);

        assertThat(transactions).hasSize(2);
    }

    @Test
    @DisplayName("Find by user ID - No transactions returns empty list")
    void testFindByUserId_NoTransactions_ReturnsEmptyList() {
        List<Transaction> transactions = repository.findByUserId(999L);
        assertThat(transactions).isEmpty();
    }

    @Test
    @DisplayName("Find by user and transaction ID - Success scenario")
    void testFindByUserIdAndTransactionId() {
        Transaction transaction = new Transaction(5L, 5L, new BigDecimal("300.00"),
                "Freelance", LocalDate.of(2025, 3, 10), "Project payment",
                Type.INCOME);
        repository.save(transaction);
        List<Transaction> savedTransactions = repository.findByUserId(5L);
        assertThat(savedTransactions).isNotEmpty();

        Transaction savedTransaction = savedTransactions.get(0);
        Long transactionId = savedTransaction.getTransactionId();

        assertThat(transactionId).isNotNull();

        Transaction found = repository.findByUserIdAndTransactionId(5L, transactionId);

        assertThat(found).isNotNull();
        assertThat(found.getCategory()).isEqualTo("Freelance");
    }

    @Test
    @DisplayName("Find filtered transactions - Success scenario")
    void testFindFilteredTransactions() {
        repository.save(new Transaction(null, 6L, new BigDecimal("200.00"), "Dining",
                LocalDate.of(2025, 3, 5), "Restaurant", Type.EXPENSE));
        repository.save(new Transaction(null, 6L, new BigDecimal("100.00"), "Transport",
                LocalDate.of(2025, 3, 10), "Taxi", Type.EXPENSE));

        LocalDate from = LocalDate.of(2025, 3, 1);
        LocalDate to = LocalDate.of(2025, 3, 31);
        List<Transaction> transactions = repository.findFiltered(6L, from, to, "Dining", Type.EXPENSE);

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getCategory()).isEqualTo("Dining");
    }

    @Test
    @DisplayName("Find filtered transactions - No matches return empty list")
    void testFindFilteredTransactions_NoMatches_ReturnsEmptyList() {
        LocalDate from = LocalDate.of(2025, 3, 1);
        LocalDate to = LocalDate.of(2025, 3, 31);
        List<Transaction> transactions =
                repository.findFiltered(7L, from, to, "NonExistent", Type.INCOME);

        assertThat(transactions).isEmpty();
    }

    @Test
    @DisplayName("Find filtered transactions - Cover all filter combinations")
    void testFindFilteredTransactions_AllFilterCombinations() {
        repository.save(new Transaction(null, 8L, new BigDecimal("150.00"), "Food",
                LocalDate.of(2025, 3, 5), "Lunch", Type.EXPENSE));
        repository.save(new Transaction(null, 8L, new BigDecimal("250.00"), "Transport",
                LocalDate.of(2025, 3, 10), "Uber", Type.EXPENSE));
        repository.save(new Transaction(null, 8L, new BigDecimal("500.00"), "Freelance",
                LocalDate.of(2025, 3, 15), "Project", Type.INCOME));

        // Case 1: Filter by userId only (should return all transactions for user 8)
        List<Transaction> result1 = repository.findFiltered(8L, null, null, null, null);
        assertThat(result1).hasSize(3);

        // Case 2: Filter by category
        List<Transaction> result2 = repository.findFiltered(8L, null, null, "Food", null);
        assertThat(result2).hasSize(1);
        assertThat(result2.get(0).getCategory()).isEqualTo("Food");

        // Case 3: Filter by type
        List<Transaction> result3 = repository.findFiltered(8L, null, null, null, Type.INCOME);
        assertThat(result3).hasSize(1);
        assertThat(result3.get(0).getType()).isEqualTo(Type.INCOME);

        // Case 4: Filter by date range
        List<Transaction> result4 = repository.findFiltered(8L, LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 10), null, null);
        assertThat(result4).hasSize(2);
    }
}