package com.demo.finance.domain.mapper;

import com.demo.finance.domain.dto.GoalDto;
import com.demo.finance.domain.model.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * The {@code GoalMapper} interface defines methods for mapping between {@link Goal} entities
 * and {@link GoalDto} data transfer objects using MapStruct. It provides bidirectional conversion
 * capabilities to facilitate the transformation of goal-related data between the application's
 * persistence layer and its API layer.
 */
@Mapper
public interface GoalMapper {

    /**
     * The singleton instance of the {@code GoalMapper}, initialized by MapStruct.
     */
    GoalMapper INSTANCE = Mappers.getMapper(GoalMapper.class);

    /**
     * Converts a {@link Goal} entity into a {@link GoalDto} data transfer object.
     *
     * @param goal the {@link Goal} entity to map
     * @return the corresponding {@link GoalDto} object
     */
    @Mapping(target = "goalId", source = "goalId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "goalName", source = "goalName")
    @Mapping(target = "targetAmount", source = "targetAmount")
    @Mapping(target = "savedAmount", source = "savedAmount")
    @Mapping(target = "duration", source = "duration")
    @Mapping(target = "startTime", source = "startTime")
    GoalDto toDto(Goal goal);

    /**
     * Converts a {@link GoalDto} data transfer object into a {@link Goal} entity.
     *
     * @param goalDto the {@link GoalDto} object to map
     * @return the corresponding {@link Goal} entity
     */
    @Mapping(target = "goalId", source = "goalId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "goalName", source = "goalName")
    @Mapping(target = "targetAmount", source = "targetAmount")
    @Mapping(target = "savedAmount", source = "savedAmount")
    @Mapping(target = "duration", source = "duration")
    @Mapping(target = "startTime", source = "startTime")
    Goal toEntity(GoalDto goalDto);
}