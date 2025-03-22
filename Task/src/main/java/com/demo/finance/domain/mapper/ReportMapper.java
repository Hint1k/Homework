package com.demo.finance.domain.mapper;

import com.demo.finance.domain.dto.ReportDto;
import com.demo.finance.domain.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReportMapper {

    ReportMapper INSTANCE = Mappers.getMapper(ReportMapper.class);

    @Mapping(target = "reportId", source = "reportId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "totalIncome", source = "totalIncome")
    @Mapping(target = "totalExpense", source = "totalExpense")
    @Mapping(target = "balance", source = "balance")
    ReportDto toDto(Report report);

    @Mapping(target = "reportId", source = "reportId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "totalIncome", source = "totalIncome")
    @Mapping(target = "totalExpense", source = "totalExpense")
    @Mapping(target = "balance", source = "balance")
    Report toEntity(ReportDto reportDto);
}