package com.demo.finance.domain.mapper;

import com.demo.finance.domain.dto.UserDto;
import com.demo.finance.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "blocked", source = "blocked")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "version", source = "version")
    UserDto toDto(User user);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "blocked", source = "blocked")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "version", source = "version")
    User toEntity(UserDto userDto);
}