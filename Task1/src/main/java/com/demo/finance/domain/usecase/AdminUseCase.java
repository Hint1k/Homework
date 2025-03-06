package com.demo.finance.domain.usecase;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.TransactionRepository;
import com.demo.finance.out.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class AdminUseCase {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public AdminUseCase(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public boolean updateUserRole(String userId, Role newRole) {
        return userRepository.findById(userId).map(user -> {
            user.setRole(newRole);
            return userRepository.update(user);
        }).orElse(false);
    }

    public boolean blockUser(String id) {
        return userRepository.findById(id).map(user -> {
            user.setBlocked(true);
            return true;
        }).orElse(false);
    }

    public boolean deleteUser(String id) {
        return userRepository.delete(id);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public boolean deleteTransaction(String transactionId) {
        return transactionRepository.delete(transactionId);
    }
}