package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.mapper.TransactionMapper;
import com.demo.finance.domain.mapper.UserMapper;
import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.Transaction;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.out.repository.TransactionRepository;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.AdminService;

import java.util.List;

/**
 * {@code AdminServiceImpl} is an implementation of the {@code AdminService} interface.
 * It provides the functionality to perform administrative actions such as retrieving users,
 * updating roles, blocking/unblocking, and deleting users.
 */
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Constructs an {@code AdminServiceImpl} instance with the specified {@code UserRepository}.
     *
     * @param userRepository the repository to interact with user data
     */
    public AdminServiceImpl(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public boolean updateUserRole(UserDto userDto) {
        Long userId = userDto.getUserId();
        Role newRole = userDto.getRole();
        User user = userRepository.findById(userId);
        if (user == null) {
            return false;
        }
        user.setRole(newRole);
        user.setVersion(user.getVersion() + 1);
        return userRepository.update(user);
    }

    /**
     * Blocks the user with the specified user ID.
     *
     * @param userId the ID of the user to be blocked
     * @return {@code true} if the user was successfully blocked, {@code false} if the user was not found
     */
    @Override
    public boolean blockOrUnblockUser(Long userId, boolean blocked) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return false;
        }
        user.setBlocked(blocked);
        user.setVersion(user.getVersion() + 1);
        return userRepository.update(user);
    }

    /**
     * Deletes the user with the specified user ID.
     *
     * @param userId the ID of the user to be deleted
     * @return {@code true} if the user was successfully deleted, {@code false} if the user was not found
     */
    @Override
    public boolean deleteUser(Long userId) {
        return userRepository.delete(userId);
    }

    @Override
    public PaginatedResponse<UserDto> getPaginatedUsers(int page, int size) {
        int offset = (page - 1) * size;
        List<User> users = userRepository.findAll(offset, size);
        int totalUsers = userRepository.getTotalUserCount();
        List<UserDto> dtoList = users.stream().map(user ->
                UserDto.removePassword(UserMapper.INSTANCE.toDto(user))).toList();

        return new PaginatedResponse<>(dtoList, totalUsers, (int) Math.ceil((double) totalUsers / size), page, size);
    }

    @Override
    public PaginatedResponse<TransactionDto> getPaginatedTransactionsForUser(Long userId, int page, int size) {
        int offset = (page - 1) * size;
        List<Transaction> transactions = transactionRepository.findByUserId(userId, offset, size);
        int totalTransactions = transactionRepository.getTotalTransactionCountForUser(userId);
        List<TransactionDto> dtoList = transactions.stream().map(TransactionMapper.INSTANCE::toDto).toList();
        return new PaginatedResponse<>(dtoList, totalTransactions, (int) Math.ceil((double) totalTransactions / size),
                page, size);
    }
}