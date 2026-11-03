package com.fitness.service;

import com.fitness.model.WeeklyRoutinePlan;
import com.fitness.repository.WeeklyRoutinePlanRepository;

import java.util.List;
import jakarta.transaction.Transactional;

import com.fitness.model.DailyRoutine;
import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;

import com.fitness.exception.AppException;

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
        return routineRepository.findById(id).orElseThrow(() -> AppException.notFound("루틴을 찾을 수 없습니다. ID: " + id));
    }
    public WeeklyRoutinePlan findByUserID(long userID){
        return routineRepository.findRoutineByUserID(userID).orElseThrow(() -> AppException.notFound("해당 사용자의 루틴을 찾을 수 없습니다. userID: " + userID));
    }
}