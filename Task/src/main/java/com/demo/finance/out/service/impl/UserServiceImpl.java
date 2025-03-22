package com.demo.finance.out.service.impl;

import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.ValidatedUser;
import com.demo.finance.domain.utils.impl.PasswordUtilsImpl;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.UserService;
import com.demo.finance.domain.mapper.UserMapper;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordUtilsImpl passwordUtils;

    public UserServiceImpl(UserRepository userRepository, PasswordUtilsImpl passwordUtils) {
        this.userRepository = userRepository;
        this.passwordUtils = passwordUtils;
    }

    @Override
    public boolean updateOwnAccount(ValidatedUser validatedUser) {
        User user = UserMapper.INSTANCE.toEntity(validatedUser.userDto());
        User existingUser = userRepository.findById(user.getUserId());
        if (existingUser == null) {
            return false;
        }
        if (validatedUser.password() != null && !validatedUser.password().equals(existingUser.getPassword())) {
            user.setPassword(passwordUtils.hashPassword(validatedUser.password()));
        } else {
            user.setPassword(existingUser.getPassword());
        }
        return userRepository.update(user);
    }

    @Override
    public boolean deleteOwnAccount(Long userId) {
        return userRepository.delete(userId);
    }
}