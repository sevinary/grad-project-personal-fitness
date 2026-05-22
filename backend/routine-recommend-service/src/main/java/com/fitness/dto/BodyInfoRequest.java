package com.fitness.dto;

import com.fitness.model.UserBodyInfo.WorkoutGoal;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BodyInfoRequest {

    @NotNull
    private Float height;

    @NotNull
    private Float weight;

    private Float bodyFatRate;

    private Float muscleMass;

    @NotNull
    private WorkoutGoal goal;

    @NotNull
    @Min(1)
    private Integer weeklyGoalCount;
}
