package com.demo.finance.domain.dto;

import com.demo.finance.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * The {@code UserDto} class represents a data transfer object (DTO) for user-related information.
 * It encapsulates details such as user ID, name, email, password, blocked status, role, and version.
 * This class is used to transfer user data between layers of the application, such as between the API
 * layer and the persistence layer.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long userId;
    private String name;
    private String email;
    private String password;
    private boolean blocked;
    private Role role;
    private Long version;

    /**
     * Removes the password from the provided {@code UserDto} object by setting it to {@code null}.
     *
     * @param userDto the {@code UserDto} object whose password is to be removed
     * @return the modified {@code UserDto} object with the password set to {@code null}
     */
    public static UserDto removePassword(UserDto userDto) {
        userDto.setPassword(null);
        return userDto;
    }

    /**
     * Compares this {@code UserDto} object to another object for equality. Two {@code UserDto} objects
     * are considered equal if their user ID, name, email, password, blocked status, role, and version
     * are the same.
     *
     * @param o the object to compare to
     * @return {@code true} if this object is equal to the provided object, otherwise {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return blocked == userDto.blocked && Objects.equals(userId, userDto.userId)
                && Objects.equals(name, userDto.name) && Objects.equals(email, userDto.email)
                && Objects.equals(password, userDto.password) && Objects.equals(role, userDto.role)
                && Objects.equals(version, userDto.version);
    }

    /**
     * Generates a hash code for this {@code UserDto} object. The hash code is based on the user ID,
     * name, email, password, blocked status, role, and version.
     *
     * @return a hash code for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, name, email, password, blocked, role, version);
    }

    /**
     * Returns a string representation of this {@code UserDto} object. The password field is excluded
     * from the string representation for security reasons.
     *
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        return "UserDto{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", blocked=" + blocked +
                ", role=" + role +
                ", version=" + version +
                '}';
    }
}