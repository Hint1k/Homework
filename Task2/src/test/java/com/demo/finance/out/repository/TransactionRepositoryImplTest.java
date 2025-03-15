package com.demo.finance.out.repository;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.impl.TransactionRepositoryImpl;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionRepositoryImplTest extends AbstractContainerBaseTest {

    private TransactionRepositoryImpl repository;

    @BeforeAll
    void setupRepository() {
        repository = new TransactionRepositoryImpl();
    }

    @BeforeEach
    void cleanDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection(
                POSTGRESQL_CONTAINER.getJdbcUrl(),
                POSTGRESQL_CONTAINER.getUsername(),
                POSTGRESQL_CONTAINER.getPassword());
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM finance.transactions");
        }
    }

    @Test
    @DisplayName("Save and find transaction by ID - Success scenario")
    void testSaveAndFindById() {
        Transaction transaction = new Transaction(null, 1L, new BigDecimal("500.00"),
                "Groceries", LocalDate.of(2025, 3, 10), "Supermarket",
                Type.EXPENSE);
        repository.save(transaction);

        Transaction found = repository.findById(transaction.getTransactionId());

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

        Transaction updatedTransaction = new Transaction(transaction.getTransactionId(), 2L,
                new BigDecimal("400.00"), "Transport", LocalDate.of(2025, 3, 15),
                "Taxi fare", Type.EXPENSE);
        boolean updated = repository.update(updatedTransaction);

        assertThat(updated).isTrue();
        Transaction found = repository.findById(transaction.getTransactionId());
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

        boolean deleted = repository.delete(transaction.getTransactionId());

        assertThat(deleted).isTrue();
        assertThat(repository.findById(transaction.getTransactionId())).isNull();
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
        Transaction transaction = new Transaction(null, 5L, new BigDecimal("300.00"),
                "Freelance", LocalDate.of(2025, 3, 10), "Project payment",
                Type.INCOME);
        repository.save(transaction);

        Optional<Transaction> found = repository.findByUserIdAndTransactionId(
                transaction.getTransactionId(), 5L);

        assertThat(found).isPresent();
        assertThat(found.get().getCategory()).isEqualTo("Freelance");
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

    @AfterAll
    static void stopContainer() {
        POSTGRESQL_CONTAINER.stop();
    }
}