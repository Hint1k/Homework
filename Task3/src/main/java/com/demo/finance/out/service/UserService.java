package com.demo.finance.out.service;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;

/**
 * The {@code UserService} interface defines the contract for managing user accounts.
 * It provides methods for updating and deleting a user's account information.
 */
public interface UserService {

    User getUser(Long userId);

    /**
     * Updates the account details of the user who is performing the action.
     *
     * @param userId            the ID of the user whose account is to be updated
     * @param name              the new name of the user
     * @param email             the new email address of the user
     * @param password          the new password for the user
     * @param role              the new role assigned to the user (e.g., user, admin)
     * @param isPasswordUpdated indicates whether the password has been updated
     * @return {@code true} if the account was successfully updated, {@code false} otherwise
     */
    boolean updateOwnAccount(Long userId, String name, String email, String password, Role role, Long version,
                             boolean isPasswordUpdated);

    /**
     * Deletes the account of the user who is performing the action.
     *
     * @param userId the ID of the user whose account is to be deleted
     * @return {@code true} if the account was successfully deleted, {@code false} otherwise
     */
    boolean deleteOwnAccount(Long userId);
}