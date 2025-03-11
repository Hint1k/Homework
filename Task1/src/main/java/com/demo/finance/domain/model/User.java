package com.demo.finance.domain.model;

import java.util.Objects;

/**
 * Represents a user of the finance tracker system, with personal details, authentication information,
 * account status, and role within the system.
 */
public class User {

    private final Long userId;
    private String name;
    private String email;
    private String password;
    private boolean blocked;
    private Role role;

    /**
     * Constructs a new User with the specified details.
     *
     * @param userId The unique ID of the user.
     * @param name The name of the user.
     * @param email The email address of the user.
     * @param password The user's password.
     * @param blocked Indicates whether the user is blocked.
     * @param role The role assigned to the user (e.g., admin, regular user).
     */
    public User(Long userId, String name, String email, String password, boolean blocked, Role role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.blocked = blocked;
        this.role = role;
    }

    /**
     * Gets the unique ID of the user.
     *
     * @return The user ID.
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Gets the name of the user.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for the user.
     *
     * @param name The new name of the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the email address of the user.
     *
     * @return The user's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets a new email address for the user.
     *
     * @param email The new email address of the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the password of the user.
     *
     * @return The user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets a new password for the user.
     *
     * @param password The new password for the user.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the blocked status of the user.
     *
     * @return {@code true} if the user is blocked, otherwise {@code false}.
     */
    public boolean isBlocked() {
        return blocked;
    }

    /**
     * Sets the blocked status of the user.
     *
     * @param blocked {@code true} to block the user, otherwise {@code false}.
     */
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    /**
     * Sets the role for the user.
     *
     * @param role The new role to assign to the user.
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Gets the role assigned to the user.
     *
     * @return The user's role.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Checks if the user has the admin role.
     *
     * @return {@code true} if the user's role is "admin", otherwise {@code false}.
     */
    public boolean isAdmin() {
        return role.getName().equalsIgnoreCase("admin");
    }

    /**
     * Compares this user to another object for equality. Two users are considered equal if
     * their user ID, name, email, password, blocked status, and role are the same.
     *
     * @param o The object to compare to.
     * @return {@code true} if this user is equal to the provided object, otherwise {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return blocked == user.blocked && Objects.equals(userId, user.userId) && Objects.equals(name, user.name)
                && Objects.equals(email, user.email) && Objects.equals(password, user.password)
                && Objects.equals(role, user.role);
    }

    /**
     * Generates a hash code for this user. The hash code is based on the user ID, name, email,
     * password, blocked status, and role.
     *
     * @return A hash code for this user.
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, name, email, password, blocked, role);
    }

    /**
     * Returns a string representation of the user, including the user ID, name, email, password,
     * blocked status, and role.
     *
     * @return A string representation of the user.
     */
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", blocked=" + blocked +
                ", role=" + role +
                '}';
    }
}