package com.demo.finance.domain.model;

import java.util.Objects;

/**
 * Represents a role assigned to a user, defined by its name.
 * This class encapsulates the name of a role and provides methods for comparison and representation.
 */
public final class Role {

    private String name;

    /**
     * Constructs a new Role with the specified name.
     *
     * @param name The name of the role.
     */
    public Role(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the role.
     *
     * @return The name of the role.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for the role.
     *
     * @param name The new name for the role.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Compares this role to another object for equality. Two roles are considered equal if
     * their names are the same.
     *
     * @param o The object to compare to.
     * @return {@code true} if this role is equal to the provided object, otherwise {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name);
    }

    /**
     * Generates a hash code for this role. The hash code is based on the role name.
     *
     * @return A hash code for this role.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    /**
     * Returns a string representation of the role, which is its name.
     *
     * @return The name of the role.
     */
    @Override
    public String toString() {
        return name;
    }
}