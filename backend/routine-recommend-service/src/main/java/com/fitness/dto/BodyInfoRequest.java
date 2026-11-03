package com.fitness.dto;

import com.fitness.model.UserBodyInfo.WorkoutGoal;
import jakarta.validation.constraints.Min;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.fitness.model.Enums;

@Getter
@Setter
public class BodyInfoRequest {

    @Column (nullable = false)
    Enums.Gender gender;

    @NotNull
    private Float height;

    @NotNull
    private Float weight;

    private Float bodyFatRate;

    private Float muscleMass;

    @NotNull
    private WorkoutGoal goal;

    @Column (nullable = false)
    @Min(1)
    @Max(5)
    private Integer preferredLevel;

    @NotNull
    @Min(1)
    private Integer weeklyGoalCount;

    @Column (nullable = false)
    private Enums.MuscleGroup avoidMuscleGroup;
}
