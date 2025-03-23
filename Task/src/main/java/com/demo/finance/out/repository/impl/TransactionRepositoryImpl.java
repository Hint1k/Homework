package com.demo.finance.out.repository.impl;

import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.out.repository.TransactionRepository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code TransactionRepositoryImpl} class implements the {@link TransactionRepository} interface
 * and provides concrete implementations for transaction data persistence operations.
 * It interacts directly with the database using SQL queries to perform CRUD operations on transaction data.
 */
public class TransactionRepositoryImpl extends BaseRepository implements TransactionRepository {

    private static final String INSERT_SQL = "INSERT INTO finance.transactions (user_id, amount, category, date, "
            + "description, type) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE finance.transactions SET user_id = ?, amount = ?, category = ?, "
            + "date = ?, description = ?, type = ? WHERE transaction_id = ?";
    private static final String DELETE_SQL = "DELETE FROM finance.transactions WHERE transaction_id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM finance.transactions WHERE transaction_id = ?";
    private static final String FIND_BY_USER_ID_SQL = "SELECT * FROM finance.transactions WHERE user_id = ?";
    private static final String FIND_BY_USER_ID_SQL_PAGINATED = "SELECT * FROM finance.transactions WHERE user_id = ? "
            + "LIMIT ? OFFSET ?";
    private static final String FIND_BY_USER_AND_TRANSACTION_SQL = "SELECT * FROM finance.transactions WHERE "
            + "transaction_id = ? AND user_id = ?";
    private static final String COUNT_SQL = "SELECT COUNT(*) AS total FROM finance.transactions WHERE user_id = ?";

    /**
     * Saves a new transaction to the database by executing the corresponding SQL insert query.
     *
     * @param transaction the {@link Transaction} object to be saved
     * @return the unique identifier ({@code Long}) of the newly saved transaction
     */
    @Override
    public Long save(Transaction transaction) {
        return insertRecord(INSERT_SQL, stmt -> setTransactionParameters(stmt, transaction));
    }

    /**
     * Updates an existing transaction in the database by executing the corresponding SQL update query.
     *
     * @param transaction the {@link Transaction} object containing updated information
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    @Override
    public boolean update(Transaction transaction) {
        return updateRecord(UPDATE_SQL, stmt -> {
            setTransactionParameters(stmt, transaction);
            stmt.setLong(7, transaction.getTransactionId());
        });
    }

    /**
     * Deletes a transaction from the database based on its unique transaction ID.
     *
     * @param transactionId the unique identifier of the transaction to delete
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    @Override
    public boolean delete(Long transactionId) {
        return updateRecord(DELETE_SQL, stmt -> stmt.setLong(1, transactionId));
    }

    /**
     * Retrieves a specific transaction by its unique transaction ID.
     *
     * @param transactionId the unique identifier of the transaction
     * @return the {@link Transaction} object matching the provided transaction ID, or {@code null} if not found
     */
    @Override
    public Transaction findById(Long transactionId) {
        return findRecordByCriteria(FIND_BY_ID_SQL, stmt ->
                        stmt.setLong(1, transactionId),
                this::mapResultSetToTransaction).orElse(null);
    }

    /**
     * Retrieves all transactions associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @return a {@link List} of {@link Transaction} objects associated with the user
     */
    @Override
    public List<Transaction> findByUserId(Long userId) {
        return findAllRecordsByCriteria(FIND_BY_USER_ID_SQL, List.of(userId), this::mapResultSetToTransaction);
    }

    /**
     * Retrieves a paginated list of transactions associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @param offset the starting index for pagination (zero-based)
     * @param size   the maximum number of transactions to retrieve
     * @return a {@link List} of {@link Transaction} objects representing the paginated results
     */
    @Override
    public List<Transaction> findByUserId(Long userId, int offset, int size) {
        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(size);
        params.add(offset);
        return findAllRecordsByCriteria(FIND_BY_USER_ID_SQL_PAGINATED, params, this::mapResultSetToTransaction);
    }

    /**
     * Retrieves a specific transaction associated with a user by their user ID and transaction ID.
     *
     * @param userId        the unique identifier of the user
     * @param transactionId the unique identifier of the transaction
     * @return the {@link Transaction} object matching the provided user ID and transaction ID, or {@code null} if not found
     */
    @Override
    public Transaction findByUserIdAndTransactionId(Long userId, Long transactionId) {
        return findRecordByCriteria(FIND_BY_USER_AND_TRANSACTION_SQL, stmt -> {
            stmt.setLong(1, transactionId);
            stmt.setLong(2, userId);
        }, this::mapResultSetToTransaction).orElse(null);
    }

    /**
     * Retrieves the total count of transactions associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @return the total number of transactions as an integer
     */
    @Override
    public int getTotalTransactionCountForUser(Long userId) {
        return queryDatabase(COUNT_SQL, stmt -> stmt.setLong(1, userId), rs -> {
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        });
    }

    /**
     * Retrieves a filtered list of transactions based on user ID, date range, category, and transaction type.
     *
     * @param userId   the unique identifier of the user
     * @param from     the start date of the filter period (inclusive, optional)
     * @param to       the end date of the filter period (inclusive, optional)
     * @param category the category of the transactions to filter by (optional, can be {@code null})
     * @param type     the type of the transactions to filter by (optional, can be {@code null})
     * @return a {@link List} of {@link Transaction} objects matching the filter criteria
     */
    @Override
    public List<Transaction> findFiltered(Long userId, LocalDate from, LocalDate to, String category, Type type) {
        String sql = buildFilteredQuery(from, to, category, type);
        List<Object> params = getFilterParameters(userId, from, to, category, type);
        return findAllRecordsByCriteria(sql, params, this::mapResultSetToTransaction);
    }

    /**
     * Builds a dynamic SQL query string for filtering transactions based on the provided criteria.
     *
     * @param from     the start date of the filter period (optional)
     * @param to       the end date of the filter period (optional)
     * @param category the category of the transactions to filter by (optional)
     * @param type     the type of the transactions to filter by (optional)
     * @return the constructed SQL query string
     */
    private String buildFilteredQuery(LocalDate from, LocalDate to, String category, Type type) {
        StringBuilder sql = new StringBuilder(FIND_BY_USER_ID_SQL);

        if (from != null) sql.append(" AND date >= ?");
        if (to != null) sql.append(" AND date <= ?");
        if (category != null) sql.append(" AND category = ?");
        if (type != null) sql.append(" AND type = ?");

        return sql.toString();
    }

    /**
     * Constructs a list of parameters for the filtered query based on the provided criteria.
     *
     * @param userId   the unique identifier of the user
     * @param from     the start date of the filter period (optional)
     * @param to       the end date of the filter period (optional)
     * @param category the category of the transactions to filter by (optional)
     * @param type     the type of the transactions to filter by (optional)
     * @return a {@link List} of parameters to be used in the SQL query
     */
    private List<Object> getFilterParameters(Long userId, LocalDate from, LocalDate to, String category, Type type) {
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (from != null) params.add(Date.valueOf(from));
        if (to != null) params.add(Date.valueOf(to));
        if (category != null) params.add(category);
        if (type != null) params.add(type.name());

        return params;
    }

    /**
     * Sets the parameters for a prepared SQL statement based on the provided transaction data.
     *
     * @param stmt         the {@link PreparedStatement} to populate with parameters
     * @param transaction  the {@link Transaction} object containing the data to set
     * @throws SQLException if an error occurs while setting parameters
     */
    private void setTransactionParameters(PreparedStatement stmt, Transaction transaction) throws SQLException {
        stmt.setLong(1, transaction.getUserId());
        stmt.setBigDecimal(2, transaction.getAmount());
        stmt.setString(3, transaction.getCategory());
        stmt.setDate(4, Date.valueOf(transaction.getDate()));
        stmt.setString(5, transaction.getDescription());
        stmt.setString(6, transaction.getType().name());
    }

    /**
     * Maps a database result set row to a {@link Transaction} object.
     *
     * @param rs the {@link ResultSet} containing the transaction data
     * @return a {@link Transaction} object populated with data from the result set
     * @throws SQLException if an error occurs while accessing the result set
     */
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getLong("transaction_id"),
                rs.getLong("user_id"),
                rs.getBigDecimal("amount"),
                rs.getString("category"),
                rs.getDate("date").toLocalDate(),
                rs.getString("description"),
                Type.valueOf(rs.getString("type"))
        );
    }
}