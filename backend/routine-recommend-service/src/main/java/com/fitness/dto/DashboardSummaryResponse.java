package com.fitness.dto;

import com.fitness.model.WorkoutLog;
import java.util.List;

public record DashboardSummaryResponse(List<WorkoutLog.WorkoutLogResponse>logs, Summary summary){
    public record Summary(int totalWorkoutDays, double anaerobicLoad, double aerobicLoad, double stretchingLoad){}
}