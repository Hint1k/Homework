package com.demo.finance.in.controller;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.usecase.AdminUseCase;

import java.util.List;
import java.util.Optional;

public class AdminController {
    private final AdminUseCase adminUseCase;

    public AdminController(AdminUseCase adminUseCase) {
        this.adminUseCase = adminUseCase;
    }

    public List<User> getAllUsers() {
        return adminUseCase.getAllUsers();
    }

    public Optional<User> getUserById(String id) {
        return adminUseCase.getUserById(id);
    }

    public boolean updateUserRole(String userId, Role newRole) {
        return adminUseCase.updateUserRole(userId, newRole);
    }

    public boolean blockUser(String id) {
        return adminUseCase.blockUser(id);
    }

    public boolean deleteUser(String id) {
        return adminUseCase.deleteUser(id);
    }

    public List<Transaction> getAllTransactions() {
        return adminUseCase.getAllTransactions();
    }

    public boolean deleteTransaction(String transactionId) {
        return adminUseCase.deleteTransaction(transactionId);
    }
}