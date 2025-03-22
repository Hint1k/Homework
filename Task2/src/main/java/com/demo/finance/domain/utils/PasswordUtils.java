package com.demo.finance.domain.utils;

/**
 * Utility interface for handling password-related operations securely.
 * It provides methods for hashing passwords and verifying raw passwords against hashed ones.
 */
public interface PasswordUtils {

    /**
     * Hashes a given password using a secure algorithm.
     *
     * @param password the raw password to be hashed
     * @return the hashed representation of the password
     */
    String hashPassword(String password);

    /**
     * Checks whether the provided raw password matches the stored hashed password.
     *
     * @param rawPassword         the raw password entered by the user
     * @param storedHashedPassword the stored hashed password (with salt)
     * @return {@code true} if the raw password matches the stored hashed password, otherwise {@code false}
     */
    boolean checkPassword(String rawPassword, String storedHashedPassword);
}