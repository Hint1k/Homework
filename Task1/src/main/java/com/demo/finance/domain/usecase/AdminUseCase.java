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

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public boolean updateUserRole(Long userId, Role newRole) {
        return userRepository.findById(userId).map(user -> {
            user.setRole(newRole);
            return userRepository.update(user);
        }).orElse(false);
    }

    public boolean blockUser(Long userId) {
        return userRepository.findById(userId).map(user -> {
            user.setBlocked(true);
            return true;
        }).orElse(false);
    }

    public boolean deleteUser(Long userId) {
        return userRepository.delete(userId);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public boolean deleteTransaction(Long transactionId) {
        return transactionRepository.delete(transactionId);
    }
}