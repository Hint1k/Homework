package com.demo.finance.out.service;

import com.demo.finance.domain.utils.PasswordUtils;
import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.UserRepository;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordUtils passwordUtils;

    public UserServiceImpl(UserRepository userRepository, PasswordUtils passwordUtils) {
        this.userRepository = userRepository;
        this.passwordUtils = passwordUtils;
    }

    @Override
    public boolean updateOwnAccount(Long userId, String name, String email, String password, Role role) {
        String hashedPassword = passwordUtils.hashPassword(password);
        User updatedUser = new User(userId, name, email, hashedPassword, false, role);
        return userRepository.update(updatedUser);
    }

    @Override
    public boolean deleteOwnAccount(Long userId) {
        return userRepository.delete(userId);
    }
}