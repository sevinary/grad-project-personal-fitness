package com.fitness.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fitness.model.BodyInfo;
import com.fitness.model.WeeklyRoutinePlan;
import com.fitness.model.WeeklyRoutineResponse;
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
    
    @PostMapping("/routines/recommend")
    public WeeklyRoutinePlan generateRoutine(@RequestBody BodyInfo bodyInfo) {
        WeeklyRoutinePlan recommendedPlan = routineRecommender.generateRoutine(bodyInfo);
        return routineSave.saveOrUpdateRoutine(recommendedPlan);
    }

    @PostMapping("/routines/save")
    public WeeklyRoutinePlan saveRoutine(@RequestBody WeeklyRoutinePlan weeklyRoutinePlan) {
        System.out.println("Received WeeklyRoutinePlan for saving: " + weeklyRoutinePlan);
        if(weeklyRoutinePlan == null) {
            throw new IllegalArgumentException("저장할 루틴 정보가 없습니다.");
        }
        return routineSave.saveOrUpdateRoutine(weeklyRoutinePlan);
    }

    @PostMapping("/routines/recommend-and-save")
    public WeeklyRoutinePlan recommendAndSaveRoutine(@RequestBody BodyInfo bodyInfo) {
        WeeklyRoutinePlan recommendedPlan = generateRoutine(bodyInfo);
        if(recommendedPlan == null) {
            throw new IllegalStateException("루틴 추천에 실패했습니다.");
        }
        return saveRoutine(recommendedPlan);
    }

    @GetMapping("/routines/{id}")
    public WeeklyRoutineResponse getRoutineDetail(@PathVariable @NonNull Long id) {
        WeeklyRoutinePlan plan = routineSave.findById(id);
        return new WeeklyRoutineResponse(plan);
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
}