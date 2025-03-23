package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.AdminService;

public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    @Override
    public boolean deleteUser(Long userId) {
        return userRepository.delete(userId);
    }
}