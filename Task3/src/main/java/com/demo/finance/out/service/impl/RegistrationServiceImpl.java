package com.demo.finance.out.service.impl;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.ValidatedUser;
import com.demo.finance.domain.utils.impl.PasswordUtilsImpl;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.RegistrationService;
import com.demo.finance.domain.mapper.UserMapper;

public class RegistrationServiceImpl implements RegistrationService {
    private final UserRepository userRepository;
    private final PasswordUtilsImpl passwordUtils;

    public RegistrationServiceImpl(UserRepository userRepository, PasswordUtilsImpl passwordUtils) {
        this.userRepository = userRepository;
        this.passwordUtils = passwordUtils;
    }

    @Override
    public boolean registerUser(ValidatedUser validatedUser) {
        if (validatedUser.userDto() == null) {
            return false;
        }

        User user = UserMapper.INSTANCE.toEntity(validatedUser.userDto());
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return false;
        }
        user.setPassword(passwordUtils.hashPassword(validatedUser.password()));
        user.setBlocked(false);
        user.setRole(new Role("user"));
        user.setVersion(1L);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean authenticate(ValidatedUser validatedUser) {
        String email = validatedUser.userDto().getEmail();
        String password = validatedUser.password();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }
        return passwordUtils.checkPassword(password, user.getPassword());
    }
}