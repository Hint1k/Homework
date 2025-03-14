package com.demo.finance.out.repository.impl;

import com.demo.finance.app.config.DataSourceManager;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.utils.Type;
import com.demo.finance.exception.DatabaseException;
import com.demo.finance.out.repository.TransactionRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
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

    @Override
    public void save(Transaction transaction) {
        String sql = "INSERT INTO finance.transactions (user_id, amount, category, date, description, type) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, transaction.getUserId());
                stmt.setBigDecimal(2, transaction.getAmount());
                stmt.setString(3, transaction.getCategory());
                stmt.setDate(4, Date.valueOf(transaction.getDate()));
                stmt.setString(5, transaction.getDescription());
                stmt.setString(6, transaction.getType().name());

                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transaction.setTransactionId(generatedKeys.getLong(1));
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                log.log(Level.SEVERE, "Error saving transaction", e);
                throw new DatabaseException("Error saving transaction", e);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error saving transaction", e);
            throw new DatabaseException("Error handling transaction", e);
        }
    }

    @Override
    public boolean update(Transaction transaction) {
        String sql = "UPDATE finance.transactions "
                + "SET user_id = ?, amount = ?, category = ?, date = ?, description = ?, type = ? "
                + "WHERE transaction_id = ?";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, transaction.getUserId());
                stmt.setBigDecimal(2, transaction.getAmount());
                stmt.setString(3, transaction.getCategory());
                stmt.setDate(4, Date.valueOf(transaction.getDate()));
                stmt.setString(5, transaction.getDescription());
                stmt.setString(6, transaction.getType().name());
                stmt.setLong(7, transaction.getTransactionId());

                boolean result = stmt.executeUpdate() > 0;
                conn.commit();
                return result;
            } catch (SQLException e) {
                conn.rollback();
                log.log(Level.SEVERE, "Error updating transaction", e);
                throw new DatabaseException("Error updating transaction", e);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error updating transaction", e);
            throw new DatabaseException("Error handling transaction", e);
        }
    }

    @Override
    public boolean delete(Long transactionId) {
        String sql = "DELETE FROM finance.transactions WHERE transaction_id = ?";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, transactionId);

                boolean result = stmt.executeUpdate() > 0;
                conn.commit();
                return result;
            } catch (SQLException e) {
                conn.rollback();
                log.log(Level.SEVERE, "Error deleting transaction", e);
                throw new DatabaseException("Error deleting transaction", e);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error deleting transaction", e);
            throw new DatabaseException("Error handling transaction", e);
        }
    }

    @Override
    public Transaction findByTransactionId(Long transactionId) {
        String sql = "SELECT * FROM finance.transactions WHERE transaction_id = ?";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, transactionId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        conn.commit();
                        return mapResultSetToTransaction(rs);
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                log.log(Level.SEVERE, "Error finding transaction by id", e);
                throw new DatabaseException("Error finding transaction", e);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error finding transaction by id", e);
            throw new DatabaseException("Error handling transaction", e);
        }
        return null;
    }

    @Override
    public List<Transaction> findByUserId(Long userId) {
        String sql = "SELECT * FROM finance.transactions WHERE user_id = ?";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        transactions.add(mapResultSetToTransaction(rs));
                    }
                    conn.commit();
                }
            } catch (SQLException e) {
                conn.rollback();
                log.log(Level.SEVERE, "Error finding transaction by user id", e);
                throw new DatabaseException("Error finding transactions by user ID", e);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error finding transaction by user id", e);
            throw new DatabaseException("Error handling transaction", e);
        }
        return transactions;
    }

    @Override
    public List<Transaction> findFiltered(Long userId, LocalDate from, LocalDate to, String category, Type type) {
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
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        transactions.add(mapResultSetToTransaction(rs));
                    }
                    conn.commit();
                }
            } catch (SQLException e) {
                conn.rollback();
                log.log(Level.SEVERE, "Error filtering transactions", e);
                throw new DatabaseException("Error filtering transactions", e);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error filtering transactions", e);
            throw new DatabaseException("Error handling transaction", e);
        }
        return transactions;
    }

    @Override
    public Optional<Transaction> findByUserIdAndTransactionId(Long transactionId, Long userId) {
        String sql = "SELECT * FROM finance.transactions WHERE transaction_id = ? AND user_id = ?";
        try (Connection conn = DataSourceManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, transactionId);
                stmt.setLong(2, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        conn.commit();
                        return Optional.of(mapResultSetToTransaction(rs));
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                log.log(Level.SEVERE, "Error finding transaction by id and user id", e);
                throw new DatabaseException("Error finding transaction by user ID and transaction ID", e);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Error finding transaction by id and user id", e);
            throw new DatabaseException("Error handling transaction", e);
        }
        return Optional.empty();
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