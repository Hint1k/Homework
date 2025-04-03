package com.demo.finance.domain.mapper;

import com.demo.finance.domain.dto.RoleDto;
import com.demo.finance.domain.model.Role;
import org.mapstruct.Mapper;

/**
 * The {@code RoleMapper} interface defines methods for mapping between {@link Role} entities
 * and {@link RoleDto} data transfer objects using MapStruct. It provides bidirectional conversion
 * capabilities to facilitate the transformation of role-related data between the application's
 * persistence layer and its API layer.
 */
@Mapper(componentModel = "spring")
public interface RoleMapper {

    /**
     * Converts a {@link Role} entity into a {@link RoleDto} data transfer object.
     *
     * @param role the {@link Role} entity to map
     * @return the corresponding {@link RoleDto} object
     */
    RoleDto toDto(Role role);

    /**
     * Converts a {@link RoleDto} data transfer object into a {@link Role} entity.
     *
     * @param roleDto the {@link RoleDto} object to map
     * @return the corresponding {@link Role} entity
     */
    Role toEntity(RoleDto roleDto);
}