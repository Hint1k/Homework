package com.demo.finance.domain.mapper;

import com.demo.finance.domain.dto.BudgetDto;
import com.demo.finance.domain.model.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * The {@code BudgetMapper} interface defines methods for mapping between {@link Budget} entities
 * and {@link BudgetDto} data transfer objects using MapStruct. It provides bidirectional conversion
 * capabilities to facilitate the transformation of budget-related data between the application's
 * persistence layer and its API layer.
 */
@Mapper(componentModel = "spring")
public interface BudgetMapper {

    /**
     * Converts a {@link Budget} entity into a {@link BudgetDto} data transfer object.
     *
     * @param budget the {@link Budget} entity to map
     * @return the corresponding {@link BudgetDto} object
     */
    @Mapping(target = "budgetId", source = "budgetId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "monthlyLimit", source = "monthlyLimit")
    @Mapping(target = "currentExpenses", source = "currentExpenses")
    BudgetDto toDto(Budget budget);

    /**
     * Converts a {@link BudgetDto} data transfer object into a {@link Budget} entity.
     *
     * @param budgetDto the {@link BudgetDto} object to map
     * @return the corresponding {@link Budget} entity
     */
    @Mapping(target = "budgetId", source = "budgetId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "monthlyLimit", source = "monthlyLimit")
    @Mapping(target = "currentExpenses", source = "currentExpenses")
    Budget toEntity(BudgetDto budgetDto);
}