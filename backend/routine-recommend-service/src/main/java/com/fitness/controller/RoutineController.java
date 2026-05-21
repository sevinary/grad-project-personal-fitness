package com.fitness.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fitness.model.BodyInfo;
import com.fitness.model.WeeklyRoutinePlan;
import com.fitness.model.WorkoutLog;
import com.fitness.service.RoutineRecommender;
import com.fitness.service.RoutineSave;
import com.fitness.service.WorkoutLogService;

@RestController
@RequestMapping("/api")
public class RoutineController {

    private final RoutineRecommender routineRecommender;
    private final RoutineSave routineSave;
    private final WorkoutLogService workoutLogService;

    public RoutineController(RoutineRecommender routineRecommender, RoutineSave routineSave,WorkoutLogService workoutLogService) {
        this.routineRecommender = routineRecommender;
        this.routineSave = routineSave;
        this.workoutLogService = workoutLogService;
    }

    @GetMapping("/routines/recommend")
    public WeeklyRoutinePlan recommendRoutine(@RequestParam Long userId){
        return routineSave.findByUserID(userId);
    }
    
    @PostMapping("/routines/recommend-and-save")
    public WeeklyRoutinePlan generateRoutine(@RequestBody BodyInfo bodyInfo) {
        WeeklyRoutinePlan recommendedPlan = routineRecommender.generateRoutine(bodyInfo);
        return routineSave.saveOrUpdateRoutine(recommendedPlan);
    }

    @PostMapping("/workout-logs")
    public ResponseEntity<WorkoutLog.WorkoutLogResponse> saveWorkoutLog(@RequestBody WorkoutLog.WorkoutLogRequest request){
        WorkoutLog savedLog = workoutLogService.saveLog(request);
        return ResponseEntity.ok(new WorkoutLog.WorkoutLogResponse(savedLog));
    }
    @DeleteMapping("/workout-logs/{logID}")
    public ResponseEntity<Void> deleteLog(@PathVariable("logID") long logID){
        workoutLogService.deleteLog(logID);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/workout-logs/weekly/{userID}")
    public ResponseEntity<List<WorkoutLog.WorkoutLogResponse>> getWeeklyLogs(@PathVariable long userID){
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(7);
        List<WorkoutLog>logs = workoutLogService.getLogsByDateRange(userID,start,end);
        List<WorkoutLog.WorkoutLogResponse> response = logs.stream().map(WorkoutLog.WorkoutLogResponse::new).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/workout-logs/monthly/{userID}")
    public ResponseEntity<List<WorkoutLog.WorkoutLogResponse>> getMonthlyLogs(@PathVariable long userID){
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(30);
        List<WorkoutLog>logs = workoutLogService.getLogsByDateRange(userID,start,end);
        List<WorkoutLog.WorkoutLogResponse> response = logs.stream().map(WorkoutLog.WorkoutLogResponse::new).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}