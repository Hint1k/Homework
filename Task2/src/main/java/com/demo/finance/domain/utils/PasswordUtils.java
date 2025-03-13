package com.demo.finance.domain.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * The {@code PasswordUtils} class provides methods for securely hashing and checking passwords.
 * It uses the PBKDF2 (Password-Based Key Derivation Function 2) algorithm for hashing passwords with a salt.
 */
public class PasswordUtils {

    /**
     * Hashes the provided password with a generated salt using the PBKDF2 algorithm.
     *
     * @param password The raw password to be hashed.
     * @return A string containing the salt and the hashed password, separated by a colon.
     */
    public String hashPassword(String password) {
        try {
            byte[] salt = getSalt();
            byte[] hashedPassword = pbkdf2(password.toCharArray(), salt);
            return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hashedPassword);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing the password", e);
        }
    }

    /**
     * Checks whether the provided raw password matches the stored hashed password.
     *
     * @param rawPassword The raw password entered by the user.
     * @param storedHashedPassword The stored hashed password (with salt).
     * @return {@code true} if the raw password matches the stored hashed password, otherwise {@code false}.
     */
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

    /**
     * Generates a random salt for hashing.
     *
     * @return A byte array containing the generated salt.
     */
    private byte[] getSalt() {
        byte[] salt = new byte[16];
        new java.security.SecureRandom().nextBytes(salt);
        return salt;
    }

    /**
     * Hashes the provided password using the PBKDF2 algorithm with the given salt.
     *
     * @param password The password to be hashed.
     * @param salt The salt to be used in the hashing process.
     * @return A byte array containing the hashed password.
     */
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