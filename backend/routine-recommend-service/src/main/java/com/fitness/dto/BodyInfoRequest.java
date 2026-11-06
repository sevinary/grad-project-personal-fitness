package com.fitness.dto;

import com.fitness.model.UserBodyInfo.WorkoutGoal;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.fitness.model.Enums;

@Getter
@Setter
public class BodyInfoRequest {

    
    private Enums.Gender gender;

    @NotNull
    private Float height;

    @NotNull
    private Float weight;

    private Float bodyFatRate;

    private Float muscleMass;

    @NotNull
    private WorkoutGoal goal;

    @Min(1)
    @Max(5)
    private Integer preferredLevel;

    @NotNull
    @Min(1)
    @Max(7)
    private Integer weeklyGoalCount;

    private Enums.MuscleGroup avoidMuscleGroup;
}
