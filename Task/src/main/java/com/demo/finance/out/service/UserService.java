package com.demo.finance.out.service;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.User;

/**
 * The {@code UserService} interface defines the contract for managing user accounts.
 * It provides methods for updating and deleting a user's account information.
 */
public interface UserService {

    User getUserByEmail(String email);

    boolean updateOwnAccount(UserDto userDto);

    boolean deleteOwnAccount(Long userId);
}