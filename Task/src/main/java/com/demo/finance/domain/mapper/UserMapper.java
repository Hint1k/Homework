package com.demo.finance.domain.mapper;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.User;
import org.mapstruct.Mapper;

/**
 * The {@code UserMapper} interface defines methods for mapping between {@link User} entities and
 * {@link UserDto} data transfer objects using MapStruct. It provides bidirectional conversion
 * capabilities to facilitate the transformation of user-related data between the application's
 * persistence layer and its API layer.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a {@link User} entity into a {@link UserDto} data transfer object.
     *
     * @param user the {@link User} entity to map
     * @return the corresponding {@link UserDto} object
     */
    UserDto toDto(User user);

    /**
     * Converts a {@link UserDto} data transfer object into a {@link User} entity.
     *
     * @param userDto the {@link UserDto} object to map
     * @return the corresponding {@link User} entity
     */
    User toEntity(UserDto userDto);
}