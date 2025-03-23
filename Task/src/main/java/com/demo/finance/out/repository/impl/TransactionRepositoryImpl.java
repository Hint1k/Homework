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

    @Override
    public Long save(Transaction transaction) {
        return insertRecord(INSERT_SQL, stmt -> setTransactionParameters(stmt, transaction));
    }

    @Override
    public boolean update(Transaction transaction) {
        return updateRecord(UPDATE_SQL, stmt -> {
            setTransactionParameters(stmt, transaction);
            stmt.setLong(7, transaction.getTransactionId());
        });
    }

    @Override
    public boolean delete(Long transactionId) {
        return updateRecord(DELETE_SQL, stmt -> stmt.setLong(1, transactionId));
    }

    @Override
    public Transaction findById(Long transactionId) {
        return findRecordByCriteria(FIND_BY_ID_SQL, stmt ->
                        stmt.setLong(1, transactionId),
                this::mapResultSetToTransaction).orElse(null);
    }

    @Override
    public List<Transaction> findByUserId(Long userId) {
        return findAllRecordsByCriteria(FIND_BY_USER_ID_SQL, List.of(userId), this::mapResultSetToTransaction);
    }

    @Override
    public List<Transaction> findByUserId(Long userId, int offset, int size) {
        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(size);
        params.add(offset);
        return findAllRecordsByCriteria(FIND_BY_USER_ID_SQL_PAGINATED, params, this::mapResultSetToTransaction);
    }

    @Override
    public Transaction findByUserIdAndTransactionId(Long userId, Long transactionId) {
        return findRecordByCriteria(FIND_BY_USER_AND_TRANSACTION_SQL, stmt -> {
            stmt.setLong(1, transactionId);
            stmt.setLong(2, userId);
        }, this::mapResultSetToTransaction).orElse(null);
    }

    @Override
    public int getTotalTransactionCountForUser(Long userId) {
        return queryDatabase(COUNT_SQL, stmt -> stmt.setLong(1, userId), rs -> {
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        });
    }

    @Override
    public List<Transaction> findFiltered(Long userId, LocalDate from, LocalDate to, String category, Type type) {
        String sql = buildFilteredQuery(from, to, category, type);
        List<Object> params = getFilterParameters(userId, from, to, category, type);
        return findAllRecordsByCriteria(sql, params, this::mapResultSetToTransaction);
    }


    private String buildFilteredQuery(LocalDate from, LocalDate to, String category, Type type) {
        StringBuilder sql = new StringBuilder(FIND_BY_USER_ID_SQL);

        if (from != null) sql.append(" AND date >= ?");
        if (to != null) sql.append(" AND date <= ?");
        if (category != null) sql.append(" AND category = ?");
        if (type != null) sql.append(" AND type = ?");

        return sql.toString();
    }

    private List<Object> getFilterParameters(Long userId, LocalDate from, LocalDate to, String category, Type type) {
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (from != null) params.add(Date.valueOf(from));
        if (to != null) params.add(Date.valueOf(to));
        if (category != null) params.add(category);
        if (type != null) params.add(type.name());

        return params;
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