package com.demo.finance.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * The {@code GoalDto} class represents a data transfer object (DTO) for goal-related information.
 * It encapsulates details such as goal ID, user ID, goal name, target amount, saved amount, duration,
 * and start time. This class is used to transfer goal data between layers of the application, such as
 * between the API layer and the persistence layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalDto {

    @Schema(description = "Unique identifier of the goal", example = "1")
    private Long goalId;

    @Schema(description = "ID of the user who created the goal", example = "2")
    private Long userId;

    @Schema(description = "Name of the goal", example = "New Car Fund")
    private String goalName;

    @Schema(description = "Target amount to save", example = "10000.00")
    private BigDecimal targetAmount;

    @Schema(description = "Currently saved amount", example = "2500.00")
    private BigDecimal savedAmount;

    @Schema(description = "Duration in months", example = "3")
    private Integer duration;

    @Schema(description = "Start date of the goal (YYYY-MM-DD)", example = "2025-04-10")
    private LocalDate startTime;
}