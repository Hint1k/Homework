package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.exception.DatabaseException;
import com.demo.finance.out.repository.TransactionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionRepositoryImpl implements TransactionRepository {

    private static final Logger log = Logger.getLogger(TransactionRepositoryImpl.class.getName());

    private static final String INSERT_TRANSACTION_SQL = "INSERT INTO finance.transactions "
            + "(user_id, amount, category, date, description, type) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_TRANSACTION_SQL = "UPDATE finance.transactions "
            + "SET user_id = ?, amount = ?, category = ?, date = ?, description = ?, type = ? "
            + "WHERE transaction_id = ?";
    private static final String DELETE_TRANSACTION_SQL = "DELETE FROM finance.transactions WHERE transaction_id = ?";
    private static final String FIND_TRANSACTION_BY_ID_SQL
            = "SELECT * FROM finance.transactions WHERE transaction_id = ?";
    private static final String FIND_TRANSACTIONS_BY_USER_ID_SQL
            = "SELECT * FROM finance.transactions WHERE user_id = ?";
    private static final String FIND_TRANSACTION_BY_USER_ID_AND_TRANSACTION_ID_SQL
            = "SELECT * FROM finance.transactions WHERE transaction_id = ? AND user_id = ?";

    @Override
    public void save(Transaction transaction) {
        executeInTransaction(getConnection(), conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_TRANSACTION_SQL,
                    Statement.RETURN_GENERATED_KEYS)) {
                setTransactionParameters(stmt, transaction);
                stmt.executeUpdate();
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transaction.setTransactionId(generatedKeys.getLong(1));
                    }
                }
            }
            return null;
        });
    }

    @Override
    public boolean update(Transaction transaction) {
        return executeUpdate(UPDATE_TRANSACTION_SQL, stmt -> {
            setTransactionParameters(stmt, transaction);
            stmt.setLong(7, transaction.getTransactionId());
        });
    }

    @Override
    public boolean delete(Long transactionId) {
        return executeUpdate(DELETE_TRANSACTION_SQL, stmt ->
                stmt.setLong(1, transactionId));
    }

    @Override
    public Transaction findById(Long transactionId) {
        return executeInTransaction(getConnection(), conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(FIND_TRANSACTION_BY_ID_SQL)) {
                stmt.setLong(1, transactionId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToTransaction(rs);
                    }
                }
            }
            return null;
        });
    }

    @Override
    public List<Transaction> findByUserId(Long userId) {
        return executeInTransaction(getConnection(), conn -> {
            List<Transaction> transactions = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(FIND_TRANSACTIONS_BY_USER_ID_SQL)) {
                stmt.setLong(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        transactions.add(mapResultSetToTransaction(rs));
                    }
                }
            }
            return transactions;
        });
    }

    @Override
    public List<Transaction> findFiltered(Long userId, LocalDate from, LocalDate to, String category, Type type) {
        return executeInTransaction(getConnection(), conn -> {
            StringBuilder sql = new StringBuilder("SELECT * FROM finance.transactions WHERE user_id = ?");
            List<Object> params = new ArrayList<>();
            params.add(userId);
            if (from != null) {
                sql.append(" AND date >= ?");
                params.add(Date.valueOf(from));
            }
            if (to != null) {
                sql.append(" AND date <= ?");
                params.add(Date.valueOf(to));
            }
            if (category != null) {
                sql.append(" AND category = ?");
                params.add(category);
            }
            if (type != null) {
                sql.append(" AND type = ?");
                params.add(type.name());
            }

            List<Transaction> transactions = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        transactions.add(mapResultSetToTransaction(rs));
                    }
                }
            }
            return transactions;
        });
    }

    @Override
    public Optional<Transaction> findByUserIdAndTransactionId(Long transactionId, Long userId) {
        return executeInTransaction(getConnection(), conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(FIND_TRANSACTION_BY_USER_ID_AND_TRANSACTION_ID_SQL)) {
                stmt.setLong(1, transactionId);
                stmt.setLong(2, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapResultSetToTransaction(rs));
                    }
                }
            }
            return Optional.empty();
        });
    }

    private void setTransactionParameters(PreparedStatement stmt, Transaction transaction) throws SQLException {
        stmt.setLong(1, transaction.getUserId());
        stmt.setBigDecimal(2, transaction.getAmount());
        stmt.setString(3, transaction.getCategory());
        stmt.setDate(4, Date.valueOf(transaction.getDate()));
        stmt.setString(5, transaction.getDescription());
        stmt.setString(6, transaction.getType().name());
    }

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

    private <T> T executeInTransaction(Connection conn, TransactionalOperation<T> operation) {
        try {
            conn.setAutoCommit(false);
            T result = operation.execute(conn);
            conn.commit();
            return result;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                logError("Rollback failed", rollbackEx);
            }
            logError("Transaction failed", e);
            return null;
        }
    }

    private boolean executeUpdate(String sql, PreparedStatementSetter setter) {
        return Boolean.TRUE.equals(executeInTransaction(getConnection(), conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setter.setValues(stmt);
                return stmt.executeUpdate() > 0;
            }
        }));
    }

    private Connection getConnection() {
        try {
            return DataSourceManager.getConnection();
        } catch (SQLException e) {
            logError("Failed to obtain database connection", e);
            throw new DatabaseException("Error obtaining database connection", e);
        }
    }

    @FunctionalInterface
    private interface TransactionalOperation<T> {
        T execute(Connection conn) throws SQLException;
    }

    @FunctionalInterface
    private interface PreparedStatementSetter {
        void setValues(PreparedStatement stmt) throws SQLException;
    }

    private void logError(String message, Exception e) {
        log.log(Level.SEVERE, message + ": " + e.getMessage(), e);
        throw new DatabaseException(message, e);
    }
}