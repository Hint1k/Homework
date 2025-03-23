package com.demo.finance.out.service.impl;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.User;
import com.demo.finance.domain.utils.PaginatedResponse;
import com.demo.finance.domain.utils.impl.PasswordUtilsImpl;
import com.demo.finance.out.repository.UserRepository;
import com.demo.finance.out.service.UserService;
import com.demo.finance.domain.mapper.UserMapper;

import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordUtilsImpl passwordUtils;

    public UserServiceImpl(UserRepository userRepository, PasswordUtilsImpl passwordUtils) {
        this.userRepository = userRepository;
        this.passwordUtils = passwordUtils;
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean updateOwnAccount(UserDto userDto) {
        User user = UserMapper.INSTANCE.toEntity(userDto);
        User existingUser = userRepository.findById(user.getUserId());
        if (existingUser == null) {
            return false;
        }
        if (!userDto.getPassword().equals(existingUser.getPassword())) {
            user.setPassword(passwordUtils.hashPassword(userDto.getPassword()));
        } else {
            user.setPassword(existingUser.getPassword());
        }
        user.setRole(existingUser.getRole());
        user.setVersion(existingUser.getVersion() + 1);
        return userRepository.update(user);
    }

    @Override
    public boolean deleteOwnAccount(Long userId) {
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
}