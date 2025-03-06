package com.demo.finance.out.repository;

import com.demo.finance.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    void save(User user);

    List<User> findAll();

    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    boolean update(User user);

    boolean delete(String id);
}
