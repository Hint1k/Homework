package com.demo.finance.domain.utils;

public interface PasswordUtils {

    String hashPassword(String password);

    boolean checkPassword(String rawPassword, String storedHashedPassword);
}