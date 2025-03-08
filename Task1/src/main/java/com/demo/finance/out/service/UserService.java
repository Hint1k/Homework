package com.demo.finance.out.service;

import com.demo.finance.domain.model.Role;

public interface UserService {

    boolean updateOwnAccount(Long userId, String name, String email, String password, Role role);

    boolean deleteOwnAccount(Long userId);
}