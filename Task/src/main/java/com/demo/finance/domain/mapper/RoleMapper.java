package com.demo.finance.domain.mapper;

import com.demo.finance.domain.dto.RoleDto;
import com.demo.finance.domain.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    @Mapping(target = "name", source = "name")
    RoleDto toDto(Role role);

    @Mapping(target = "name", source = "name")
    Role toEntity(RoleDto roleDto);
}