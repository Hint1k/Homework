package com.demo.finance.domain.mapper;

import com.demo.finance.domain.dto.BudgetDto;
import com.demo.finance.domain.model.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BudgetMapper {

    BudgetMapper INSTANCE = Mappers.getMapper(BudgetMapper.class);

    @Mapping(target = "budgetId", source = "budgetId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "monthlyLimit", source = "monthlyLimit")
    @Mapping(target = "currentExpenses", source = "currentExpenses")
    BudgetDto toDto(Budget budget);

    @Mapping(target = "budgetId", source = "budgetId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "monthlyLimit", source = "monthlyLimit")
    @Mapping(target = "currentExpenses", source = "currentExpenses")
    Budget toEntity(BudgetDto budgetDto);
}