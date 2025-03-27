package com.demo.finance.domain.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportDatesDto {
    private LocalDate fromDate;
    private LocalDate toDate;
}