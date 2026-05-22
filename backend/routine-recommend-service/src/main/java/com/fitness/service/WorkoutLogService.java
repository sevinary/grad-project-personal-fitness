package com.fitness.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fitness.dto.DashboardSummaryResponse;
import com.fitness.model.WorkoutLog;
import com.fitness.model.WorkoutLog.DashboardResponse;
import com.fitness.repository.WorkoutLogRepository;
import com.fitness.model.Exercise;
import com.fitness.repository.ExerciseRepository;

import jakarta.transaction.Transactional;
import lombok.NonNull;

@Service
public class WorkoutLogService {
    private final WorkoutLogRepository logRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutLogService(WorkoutLogRepository logRepository, ExerciseRepository exerciseRepository){
        this.logRepository = logRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Transactional
    public void deleteLog(@NonNull Long logID){
        logRepository.deleteById(logID);
    }
    public List<WorkoutLog> getLogsByDateRange(long userID,LocalDate start,LocalDate end){
        return logRepository.findByUserIDAndWorkoutDateBetween(userID,start,end);
    }
    @Transactional
    public DashboardSummaryResponse getDashboardData(long userID, LocalDate start, LocalDate end) {
        List<WorkoutLog> logs = logRepository.findByUserIDAndWorkoutDateBetween(userID, start, end);
        List<WorkoutLog.WorkoutLogResponse> logResponses = logs.stream()
                .map(WorkoutLog.WorkoutLogResponse::new)
                .collect(Collectors.toList());

        int totalWorkoutDays = (int) logs.stream().map(WorkoutLog::getWorkoutDate).distinct().count();
        double ana = 0, aero = 0, stre = 0;

        for(WorkoutLog log : logs){
            if(log.getExerciseType() == null) {
                Exercise exercise = exerciseRepository.findById(log.getExerciseID())
                        .orElseThrow(() -> new IllegalArgumentException("운동이 존재하지 않습니다."));
                log.setExerciseType(exercise.getExerciseType());
            }

            double load = calculateLoad(log);
            if (log.getExerciseType() == null) continue;
            switch(log.getExerciseType()){
                case ANAEROBIC -> ana += load;
                case AEROBIC -> aero += load;
                case STRETCHING -> stre += load;
            }
        }
        DashboardSummaryResponse.Summary summary = new DashboardSummaryResponse.Summary(totalWorkoutDays, ana, aero, stre);
        return new DashboardSummaryResponse(logResponses, summary);
    }
    @Transactional
    public WorkoutLog saveLog(WorkoutLog.WorkoutLogRequest request){
        if (request.workoutDate().isAfter(LocalDate.now())){
            throw new IllegalArgumentException("미래 날짜의 운동 로그는 등록할 수 없습니다.");
        }
        Exercise exercise = exerciseRepository.findById(request.exerciseID())
                .orElseThrow(() -> new IllegalArgumentException("운동이 존재하지 않습니다."));
        Optional<WorkoutLog> existingLog = logRepository.findByUserIDAndExerciseIDAndWorkoutDate(request.userID(), request.exerciseID(),request.workoutDate());
        return existingLog.map(log -> {
            log.setExerciseType(exercise.getExerciseType());
            log.setSets(request.sets());
            log.setReps(request.reps());
            log.setExerciseWeight(request.exerciseWeight());
            return logRepository.save(log);
        }).orElseGet(() -> {
            WorkoutLog newLog = request.toEntity();
            newLog.setExerciseType(exercise.getExerciseType());
            return logRepository.save(newLog);
        });
    }
    public double calculateLoad(WorkoutLog log){
        switch (log.getExerciseType()){
            case ANAEROBIC:
                return log.getSets() * log.getReps() * log.getExerciseWeight();
            case AEROBIC:
                return log.getSets() * log.getExerciseWeight();
            case STRETCHING:
                return log.getSets() * log.getExerciseWeight() * 0.5;
            default:
                return 0;
        }
    }
}