package com.demo.finance.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoalDto {

    private Long goalId;
    private Long userId;
    private String goalName;
    private BigDecimal targetAmount;
    private BigDecimal savedAmount;
    private Integer duration;
    private LocalDate startTime;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GoalDto goalDto = (GoalDto) o;
        return Objects.equals(goalId, goalDto.goalId) && Objects.equals(userId, goalDto.userId)
                && Objects.equals(goalName, goalDto.goalName) && Objects.equals(targetAmount, goalDto.targetAmount)
                && Objects.equals(savedAmount, goalDto.savedAmount) && Objects.equals(duration, goalDto.duration)
                && Objects.equals(startTime, goalDto.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(goalId, userId, goalName, targetAmount, savedAmount, duration, startTime);
    }

    @Override
    public String toString() {
        return "GoalDto{" +
                "goalId=" + goalId +
                ", userId=" + userId +
                ", goalName='" + goalName + '\'' +
                ", targetAmount=" + targetAmount +
                ", savedAmount=" + savedAmount +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}