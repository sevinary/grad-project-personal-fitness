package com.fitness.controller;

import com.fitness.model.*;
import com.fitness.service.RoutineRecommender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/routines")
public class RoutineController {

    private final RoutineRecommender routineRecommender;

    public RoutineController(RoutineRecommender routineRecommender) {
        this.routineRecommender = routineRecommender;
    }

    @PostMapping("/recommend")
    public WeeklyRoutinePlan generateRoutine(@RequestBody BodyInfo bodyInfo) {
        return routineRecommender.generateRoutine(bodyInfo);
    }
}