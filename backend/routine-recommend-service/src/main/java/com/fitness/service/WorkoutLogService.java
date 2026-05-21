package com.fitness.service;

import com.fitness.model.WorkoutLog;
import com.fitness.model.WorkoutLog.WorkoutLogRequest;
import com.fitness.repository.WorkoutLogRepository;

import jakarta.transaction.Transactional;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.List;

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

    @Transactional
    public void deleteLog(@NonNull Long logID){
        logRepository.deleteById(logID);
    }
    public List<WorkoutLog> getLogsByDateRange(long userID,LocalDate start,LocalDate end){
        return logRepository.findByUserIDAndWorkoutDateBetween(userID,start,end);
    }

}
