package com.demo.finance.out.service;

import com.demo.finance.domain.model.Role;
import com.demo.finance.domain.model.User;

import java.util.List;

public interface AdminService {

    List<User> getAllUsers();

    boolean updateUserRole(Long userId, Role newRole);

    boolean blockUser(Long userId);

    boolean unBlockUser(Long userId);

    boolean deleteUser(Long userId);
}