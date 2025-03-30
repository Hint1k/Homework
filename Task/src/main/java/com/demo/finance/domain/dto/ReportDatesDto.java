package com.demo.finance.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * The {@code ReportDatesDto} class represents a data transfer object (DTO) used to encapsulate
 * date range information for generating reports. It contains two fields: {@code fromDate} and
 * {@code toDate}, which define the start and end dates of the report period.
 * <p>
 * This class is typically used in scenarios where a user specifies a date range for filtering
 * or analyzing financial data within a system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDatesDto {
    private LocalDate fromDate;
    private LocalDate toDate;
}