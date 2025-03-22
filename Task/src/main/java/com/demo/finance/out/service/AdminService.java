package com.demo.finance.out.service;

import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.PaginatedResponse;

/**
 * The {@code AdminService} interface defines the contract for managing user-related administrative actions.
 * It provides methods for retrieving all users, updating user roles, and performing actions like blocking,
 * unblocking, and deleting users.
 */
public interface AdminService {

    User getUser(Long userId);

    boolean updateUserRole(UserDto userDto);

    boolean blockOrUnblockUser(Long userId, boolean blocked);

    /**
     * Deletes the user with the specified user ID.
     *
     * @param userId the ID of the user to be deleted
     * @return {@code true} if the user was successfully deleted, {@code false} if the user was not found
     */
    boolean deleteUser(Long userId);

    PaginatedResponse<UserDto> getPaginatedUsers(int page, int size);

    PaginatedResponse<TransactionDto> getPaginatedTransactionsForUser(Long userId, int page, int size);
}