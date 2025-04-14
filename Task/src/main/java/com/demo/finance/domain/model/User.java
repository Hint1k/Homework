package com.demo.finance.domain.model;

import com.demo.finance.domain.utils.GeneratedKey;
import com.demo.finance.domain.utils.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * Represents a user of the finance tracker system, with personal details, authentication information,
 * account status, and role within the system.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @GeneratedKey
    private Long userId;
    private String name;
    private String email;
    private String password;
    private boolean blocked;
    private Role role;
    private Long version;

    /**
     * Constructs a new {@code User} object with the specified details. This constructor is used to create
     * a user without an initially assigned user ID, typically before persisting the user to the database.
     *
     * @param name     the name of the user
     * @param email    the email address of the user
     * @param password the password of the user
     * @param blocked  the blocked status of the user (true if blocked, false otherwise)
     * @param role     the role of the user within the system
     * @param version  the version of the user record for optimistic locking
     */
    public User(String name, String email, String password, boolean blocked, Role role, Long version) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.blocked = blocked;
        this.role = role;
        this.version = version;
    }

    /**
     * Compares this user to another object for equality. Two users are considered equal if
     * their user ID, name, email, password, blocked status, role and version are the same.
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
                && Objects.equals(role, user.role) && Objects.equals(version, user.version);
    }

    /**
     * Generates a hash code for this user. The hash code is based on the user ID, name, email,
     * password, blocked status, role and version.
     *
     * @return A hash code for this user.
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, name, email, password, blocked, role, version);
    }

    /**
     * Returns a string representation of the user, including the user ID, name, email, password,
     * blocked status, role and version.
     *
     * @return A string representation of the user.
     */
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", blocked=" + blocked +
                ", role=" + role +
                ", version=" + version +
                '}';
    }
}