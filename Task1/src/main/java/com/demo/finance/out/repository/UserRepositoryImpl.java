package com.demo.finance.out.repository;

import com.demo.finance.domain.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepositoryImpl implements UserRepository {

    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public void save(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public boolean update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        if (users.containsKey(id)) {
            users.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public List<User> findAll(){
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }
}