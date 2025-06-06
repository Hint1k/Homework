package com.demo.finance.domain.mapper;

import com.demo.finance.domain.dto.GoalDto;
import com.demo.finance.domain.model.Goal;
import org.mapstruct.Mapper;

/**
 * The {@code GoalMapper} interface defines methods for mapping between {@link Goal} entities
 * and {@link GoalDto} data transfer objects using MapStruct. It provides bidirectional conversion
 * capabilities to facilitate the transformation of goal-related data between the application's
 * persistence layer and its API layer.
 */
@Mapper(componentModel = "spring")
public interface GoalMapper {

    /**
     * Converts a {@link Goal} entity into a {@link GoalDto} data transfer object.
     *
     * @param goal the {@link Goal} entity to map
     * @return the corresponding {@link GoalDto} object
     */
    GoalDto toDto(Goal goal);

    /**
     * Converts a {@link GoalDto} data transfer object into a {@link Goal} entity.
     *
     * @param goalDto the {@link GoalDto} object to map
     * @return the corresponding {@link Goal} entity
     */
    Goal toEntity(GoalDto goalDto);
}