package com.demo.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The {@code RoleDto} class represents a data transfer object (DTO) for role-related information.
 * It encapsulates the name of the role and is used to transfer role data between layers of the application,
 * such as between the API layer and the persistence layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {

    @Schema(description = "Role of a user in the system (user or admin)", example = "user")
    private String name;
}