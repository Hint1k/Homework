package com.demo.finance.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Represents a user of the finance tracker system, with personal details, authentication information,
 * account status, and role within the system.
 */
@Setter
@Getter
@AllArgsConstructor
public class User {

    private final Long userId;
    private String name;
    private String email;
    private String password;
    private boolean blocked;
    private Role role;

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