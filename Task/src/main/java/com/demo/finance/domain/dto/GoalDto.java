package com.demo.finance.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * The {@code GoalDto} class represents a data transfer object (DTO) for goal-related information.
 * It encapsulates details such as goal ID, user ID, goal name, target amount, saved amount, duration,
 * and start time. This class is used to transfer goal data between layers of the application, such as
 * between the API layer and the persistence layer.
 */
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

    /**
     * Compares this {@code GoalDto} object to another object for equality. Two {@code GoalDto} objects
     * are considered equal if their goal ID, user ID, goal name, target amount, saved amount, duration,
     * and start time are the same.
     *
     * @param o the object to compare to
     * @return {@code true} if this object is equal to the provided object, otherwise {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GoalDto goalDto = (GoalDto) o;
        return Objects.equals(goalId, goalDto.goalId) && Objects.equals(userId, goalDto.userId)
                && Objects.equals(goalName, goalDto.goalName) && Objects.equals(targetAmount, goalDto.targetAmount)
                && Objects.equals(savedAmount, goalDto.savedAmount) && Objects.equals(duration, goalDto.duration)
                && Objects.equals(startTime, goalDto.startTime);
    }

    /**
     * Generates a hash code for this {@code GoalDto} object. The hash code is based on the goal ID,
     * user ID, goal name, target amount, saved amount, duration, and start time.
     *
     * @return a hash code for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(goalId, userId, goalName, targetAmount, savedAmount, duration, startTime);
    }

    /**
     * Returns a string representation of this {@code GoalDto} object. The string includes all fields
     * of the goal, such as goal ID, user ID, goal name, target amount, saved amount, duration, and start time.
     *
     * @return a string representation of this object
     */
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