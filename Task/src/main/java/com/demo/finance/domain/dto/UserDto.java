package com.demo.finance.domain.dto;

import com.demo.finance.domain.model.Role;

import com.demo.finance.starter.audit.AuditableUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The {@code UserDto} class represents a data transfer object (DTO) for user-related information.
 * It encapsulates details such as user ID, name, email, password, blocked status, role, and version.
 * This class is used to transfer user data between layers of the application, such as between the API
 * layer and the persistence layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements AuditableUser {

    @Schema(description = "Unique identifier of the user", example = "2")
    private Long userId;

    @Schema(description = "User's full name", example = "John Doe")
    private String name;

    @Schema(description = "User's email address", example = "JohnDoe@demo.com")
    private String email;

    @Schema(description = "User's password", example = "12345")
    private String password;

    @Schema(description = "Indicates if the user account is blocked", example = "false")
    private boolean blocked;

    @Schema(description = "User's role in the system (user or admin)", example = "user")
    private Role role;

    @Schema(description = "Optimistic locking version number", example = "1")
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