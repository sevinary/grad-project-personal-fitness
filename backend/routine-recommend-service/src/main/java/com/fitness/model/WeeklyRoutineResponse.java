package com.fitness.model;

import lombok.Getter;

@Getter
public class WeeklyRoutineResponse {
    private Long id;
    private String userID;
    private String routineDescription;

    public WeeklyRoutineResponse(WeeklyRoutinePlan plan) {
        this.id = plan.getWeeklyRoutineID();
        this.userID = String.valueOf(plan.getUserID());
        this.routineDescription = plan.getRoutineDescription();
    }
}