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
import java.util.Optional;

public class TransactionRepositoryImpl extends BaseRepository implements TransactionRepository {

    private static final String INSERT_SQL = "INSERT INTO finance.transactions (user_id, amount, category, date, "
            + "description, type) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE finance.transactions SET user_id = ?, amount = ?, category = ?, "
            + "date = ?, description = ?, type = ? WHERE transaction_id = ?";
    private static final String DELETE_SQL = "DELETE FROM finance.transactions WHERE transaction_id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM finance.transactions WHERE transaction_id = ?";
    private static final String FIND_BY_USER_ID_SQL = "SELECT * FROM finance.transactions WHERE user_id = ?";
    private static final String FIND_BY_USER_AND_TRANSACTION_SQL = "SELECT * FROM finance.transactions WHERE "
            + "transaction_id = ? AND user_id = ?";

    @Override
    public void save(Transaction transaction) {
        executeInTransaction(conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                setTransactionParameters(stmt, transaction);
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        transaction.setTransactionId(rs.getLong(1));
                    }
                }
                return null;
            }
        });
    }

    @Override
    public boolean update(Transaction transaction) {
        return executeUpdate(UPDATE_SQL, stmt -> {
            setTransactionParameters(stmt, transaction);
            stmt.setLong(7, transaction.getTransactionId());
        });
    }

    @Override
    public boolean delete(Long transactionId) {
        return executeUpdate(DELETE_SQL, stmt -> stmt.setLong(1, transactionId));
    }

    @Override
    public Transaction findById(Long transactionId) {
        return findByCriteria(FIND_BY_ID_SQL, stmt -> stmt.setLong(1, transactionId),
                this::mapResultSetToTransaction).orElse(null);
    }

    @Override
    public List<Transaction> findByUserId(Long userId) {
        return executeInTransaction(conn -> {
            List<Transaction> transactions = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(FIND_BY_USER_ID_SQL)) {
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
        return executeInTransaction(conn -> {
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
        return findByCriteria(FIND_BY_USER_AND_TRANSACTION_SQL, stmt -> {
            stmt.setLong(1, transactionId);
            stmt.setLong(2, userId);
        }, this::mapResultSetToTransaction);
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
}