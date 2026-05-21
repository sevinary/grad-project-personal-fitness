package com.fitness.service;

import com.fitness.model.WorkoutLog;
import com.fitness.model.WorkoutLog.WorkoutLogRequest;
import com.fitness.repository.WorkoutLogRepository;

import org.springframework.stereotype.Service;

@Service
public class WorkoutLogService {
    private final WorkoutLogRepository logRepository;

    public WorkoutLogService(WorkoutLogRepository logRepository){
        this.logRepository = logRepository;
    }

    public WorkoutLog saveLog(WorkoutLogRequest request){
        WorkoutLog log = new WorkoutLog(request.userID(), request.exerciseID(),request.workoutDate(),request.sets(),request.reps(),request.exerciseWeight());
        return logRepository.save(log);
    }
}
