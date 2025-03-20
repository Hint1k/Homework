package com.demo.finance.domain.mapper;

import com.demo.finance.domain.dto.GoalDto;
import com.demo.finance.domain.model.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GoalMapper {

    GoalMapper INSTANCE = Mappers.getMapper(GoalMapper.class);

    @Mapping(target = "goalId", source = "goalId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "goalName", source = "goalName")
    @Mapping(target = "targetAmount", source = "targetAmount")
    @Mapping(target = "savedAmount", source = "savedAmount")
    @Mapping(target = "duration", source = "duration")
    @Mapping(target = "startTime", source = "startTime")
    GoalDto toDto(Goal goal);

    @Mapping(target = "goalId", source = "goalId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "goalName", source = "goalName")
    @Mapping(target = "targetAmount", source = "targetAmount")
    @Mapping(target = "savedAmount", source = "savedAmount")
    @Mapping(target = "duration", source = "duration")
    @Mapping(target = "startTime", source = "startTime")
    Goal toEntity(GoalDto goalDto);
}