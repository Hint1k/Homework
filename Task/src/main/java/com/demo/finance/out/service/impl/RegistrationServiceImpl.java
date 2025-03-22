package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
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
    public boolean registerUser(UserDto userDto) {
        User user = UserMapper.INSTANCE.toEntity(userDto);
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return false;
        }
        user.setPassword(passwordUtils.hashPassword(userDto.getPassword()));
        user.setBlocked(false);
        user.setRole(new Role("user"));
        user.setVersion(1L);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean authenticate(UserDto userDto) {
        String email = userDto.getEmail();
        String password = userDto.getPassword();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }
        return passwordUtils.checkPassword(password, user.getPassword());
    }
}