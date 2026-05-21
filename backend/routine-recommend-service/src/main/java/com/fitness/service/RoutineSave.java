package com.fitness.service;

import com.fitness.model.WeeklyRoutinePlan;
import com.fitness.repository.WeeklyRoutinePlanRepository;

import java.util.List;
import jakarta.transaction.Transactional;

import com.fitness.model.DailyRoutine;
import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;

@Service
public class RoutineSave {
    
    private final WeeklyRoutinePlanRepository routineRepository;
    public RoutineSave(WeeklyRoutinePlanRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    @Transactional
    public WeeklyRoutinePlan saveOrUpdateRoutine(WeeklyRoutinePlan weeklyRoutinePlan) {
        List<WeeklyRoutinePlan> existingPlans = routineRepository.findByUserID(weeklyRoutinePlan.getUserID());
        
        if(!existingPlans.isEmpty()){
            routineRepository.deleteAll(existingPlans);
        }
        routineRepository.flush();
        
        weeklyRoutinePlan.updateRoutineDescription(); //루틴 설명 업데이트
        if(weeklyRoutinePlan.getWeeklyRoutines() != null) {
            for(DailyRoutine dailyRoutine : weeklyRoutinePlan.getWeeklyRoutines()) {
                dailyRoutine.setWeeklyRoutinePlan(weeklyRoutinePlan);
            }
        }
        return routineRepository.save(weeklyRoutinePlan);
    }
    public WeeklyRoutinePlan findById(@NonNull Long id) {
        return routineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("루틴을 찾을 수 없습니다. ID: " + id));
    }
}