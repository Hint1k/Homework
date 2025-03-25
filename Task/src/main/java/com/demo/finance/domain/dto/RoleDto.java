package com.demo.finance.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * The {@code RoleDto} class represents a data transfer object (DTO) for role-related information.
 * It encapsulates the name of the role and is used to transfer role data between layers of the application,
 * such as between the API layer and the persistence layer.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {

    private String name;

    /**
     * Compares this {@code RoleDto} object to another object for equality. Two {@code RoleDto} objects
     * are considered equal if their role names are the same.
     *
     * @param o the object to compare to
     * @return {@code true} if this object is equal to the provided object, otherwise {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RoleDto roleDto = (RoleDto) o;
        return Objects.equals(name, roleDto.name);
    }

    /**
     * Generates a hash code for this {@code RoleDto} object. The hash code is based on the role name.
     *
     * @return a hash code for this object
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    /**
     * Returns a string representation of this {@code RoleDto} object. The string includes the role name.
     *
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        return "RoleDto{" +
                "name='" + name + '\'' +
                '}';
    }
}