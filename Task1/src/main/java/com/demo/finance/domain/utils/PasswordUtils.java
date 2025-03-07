package com.demo.finance.domain.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

public class PasswordUtils {

    public String hashPassword(String password) {
        try {
            byte[] salt = getSalt();
            byte[] hashedPassword = pbkdf2(password.toCharArray(), salt);
            return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hashedPassword);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing the password", e);
        }
    }

    public boolean checkPassword(String rawPassword, String storedHashedPassword) {
        try {
            String[] parts = storedHashedPassword.split(":");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHash = Base64.getDecoder().decode(parts[1]);
            byte[] hashedPassword = pbkdf2(rawPassword.toCharArray(), salt);
            return MessageDigest.isEqual(storedHash, hashedPassword);
        } catch (Exception e) {
            throw new RuntimeException("Error checking the password", e);
        }
    }

    private byte[] getSalt() {
        byte[] salt = new byte[16];
        new java.security.SecureRandom().nextBytes(salt);
        return salt;
    }

    private byte[] pbkdf2(char[] password, byte[] salt) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(salt, "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(new String(password).getBytes());
            for (int i = 1; i < 10000; i++) {
                hash = mac.doFinal(hash);
            }
            return hash;
        } catch (Exception e) {
            throw new RuntimeException("Error during PBKDF2 hashing", e);
        }
    }
}