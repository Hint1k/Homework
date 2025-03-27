package com.demo.finance.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * The {@code GoalDto} class represents a data transfer object (DTO) for goal-related information.
 * It encapsulates details such as goal ID, user ID, goal name, target amount, saved amount, duration,
 * and start time. This class is used to transfer goal data between layers of the application, such as
 * between the API layer and the persistence layer.
 */
@Data
public class GoalDto {

    private Long goalId;
    private Long userId;
    private String goalName;
    private BigDecimal targetAmount;
    private BigDecimal savedAmount;
    private Integer duration;
    private LocalDate startTime;
}