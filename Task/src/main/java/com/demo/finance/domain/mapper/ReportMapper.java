package com.demo.finance.domain.mapper;

import com.demo.finance.domain.dto.ReportDto;
import com.demo.finance.domain.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * The {@code ReportMapper} interface defines methods for mapping between {@link Report} entities
 * and {@link ReportDto} data transfer objects using MapStruct. It provides bidirectional conversion
 * capabilities to facilitate the transformation of report-related data between the application's
 * persistence layer and its API layer.
 */
@Mapper(componentModel = "spring")
public interface ReportMapper {

    /**
     * Converts a {@link Report} entity into a {@link ReportDto} data transfer object.
     *
     * @param report the {@link Report} entity to map
     * @return the corresponding {@link ReportDto} object
     */
    ReportDto toDto(Report report);

    /**
     * Converts a {@link ReportDto} data transfer object into a {@link Report} entity.
     *
     * @param reportDto the {@link ReportDto} object to map
     * @return the corresponding {@link Report} entity
     */
    Report toEntity(ReportDto reportDto);
}