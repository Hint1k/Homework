package com.demo.finance.out.repository;

import com.demo.finance.domain.model.User;

import java.util.List;

public interface UserRepository {

    void save(User user);

    boolean update(User user);

    boolean delete(Long userId);

    List<User> findAll(int offset, int size);

    int getTotalUserCount();

    User findById(Long userId);

    User findByEmail(String email);
}