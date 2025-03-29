package com.demo.finance.domain.dto;

import com.demo.finance.domain.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * The {@code UserDto} class represents a data transfer object (DTO) for user-related information.
 * It encapsulates details such as user ID, name, email, password, blocked status, role, and version.
 * This class is used to transfer user data between layers of the application, such as between the API
 * layer and the persistence layer.
 */
@Data
@Schema(description = "Data transfer object for user information")
public class UserDto {

    @Schema(description = "Unique identifier of the user", example = "1")
    private Long userId;

    @Schema(description = "Name of the user", example = "John Doe")
    private String name;

    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Password of the user", example = "P@ssw0rd")
    private String password;

    @Schema(description = "Indicates if the user is blocked", example = "false")
    private boolean blocked;

    @Schema(description = "Role of the user", example = "USER")
    private Role role;

    @Schema(description = "Version for optimistic locking", example = "1")
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