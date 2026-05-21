package com.fitness.controller;

import com.fitness.model.*;
import com.fitness.service.RoutineRecommender;
import com.fitness.service.RoutineSave;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/routines")
public class RoutineController {

    private final RoutineRecommender routineRecommender;
    private final RoutineSave routineSave;

    public RoutineController(RoutineRecommender routineRecommender, RoutineSave routineSave) {
        this.routineRecommender = routineRecommender;
        this.routineSave = routineSave;
    }

    @PostMapping("/recommend")
    public WeeklyRoutinePlan generateRoutine(@RequestBody BodyInfo bodyInfo) {
        WeeklyRoutinePlan recommendedPlan = routineRecommender.generateRoutine(bodyInfo);
        return routineSave.saveOrUpdateRoutine(recommendedPlan);
    }

    @PostMapping("/save")
    public WeeklyRoutinePlan saveRoutine(@RequestBody WeeklyRoutinePlan weeklyRoutinePlan) {
        System.out.println("Received WeeklyRoutinePlan for saving: " + weeklyRoutinePlan);
        if(weeklyRoutinePlan == null) {
            throw new IllegalArgumentException("저장할 루틴 정보가 없습니다.");
        }
        return routineSave.saveOrUpdateRoutine(weeklyRoutinePlan);
    }

    @PostMapping("/recommend-and-save")
    public WeeklyRoutinePlan recommendAndSaveRoutine(@RequestBody BodyInfo bodyInfo) {
        WeeklyRoutinePlan recommendedPlan = generateRoutine(bodyInfo);
        if(recommendedPlan == null) {
            throw new IllegalStateException("루틴 추천에 실패했습니다.");
        }
        return saveRoutine(recommendedPlan);
    }

    @GetMapping("/{id}")
    public WeeklyRoutineResponse getRoutineDetail(@PathVariable Long id) {
        WeeklyRoutinePlan plan = routineSave.findById(id);
        return new WeeklyRoutineResponse(plan);
    }
}