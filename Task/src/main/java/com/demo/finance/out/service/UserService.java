package com.demo.finance.out.service;

import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.ValidatedUser;

/**
 * The {@code UserService} interface defines the contract for managing user accounts.
 * It provides methods for updating and deleting a user's account information.
 */
public interface UserService {

     boolean updateOwnAccount(ValidatedUser validatedUser);

    boolean deleteOwnAccount(Long userId);
}