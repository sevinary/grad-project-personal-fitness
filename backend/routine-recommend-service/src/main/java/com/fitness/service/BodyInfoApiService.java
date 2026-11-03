package com.fitness.service;

import com.fitness.dto.BodyInfoRequest;
import com.fitness.exception.AppException;
import com.fitness.model.BodyInfo;
import com.fitness.model.Enums;
import com.fitness.model.UserBodyInfo;
import com.fitness.model.WeeklyRoutinePlan;
import com.fitness.repository.UserBodyInfoRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;
import com.fitness.model.UserBodyInfo.WorkoutGoal;

import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;

@Service
public class BodyInfoApiService {

    private final UserBodyInfoRepository userBodyInfoRepository;
    private final RoutineRecommender routineRecommender;
    private final RoutineSave routineSave;

    public BodyInfoApiService(UserBodyInfoRepository userBodyInfoRepository,
        RoutineRecommender routineRecommender, RoutineSave routineSave) {
        this.userBodyInfoRepository = userBodyInfoRepository;
        this.routineRecommender = routineRecommender;
        this.routineSave = routineSave;
    }

    @Transactional
    public BodyInfoResult save(Long userId, BodyInfoRequest request) {
        if (userBodyInfoRepository.existsByUserId(userId)) {
            throw AppException.bodyInfoExists();
        }

        UserBodyInfo info = new UserBodyInfo();
        info.setUserId(userId);
        info.setHeight(request.getHeight());
        info.setWeight(request.getWeight());
        info.setBodyFatRate(request.getBodyFatRate());
        info.setMuscleMass(request.getMuscleMass());
        info.setGoal(request.getGoal());
        info.setWeeklyGoalCount(request.getWeeklyGoalCount());
        info.setCreatedAt(LocalDateTime.now());
        info.setUpdatedAt(LocalDateTime.now());
        info.setGender(request.getGender() != null ? request.getGender() : Enums.Gender.FEMALE);
        info.setPreferredLevel(request.getPreferredLevel() != null ? request.getPreferredLevel() : 2);
        info.setAvoidMuscleGroup(request.getAvoidMuscleGroup() != null ? request.getAvoidMuscleGroup() : Enums.MuscleGroup.NONE);

        info = userBodyInfoRepository.save(info);

        BodyInfo routineInput = convertToRoutineInput(info);
        WeeklyRoutinePlan recommendedRoutine = routineRecommender.generateRoutine(routineInput);
        routineSave.saveOrUpdateRoutine(recommendedRoutine);

        return toResult(info);
    }

    private BodyInfoResult toResult(UserBodyInfo info) {
        return new BodyInfoResult(
                info.getBodyInfoId(),
                info.getUserId(),
                info.getHeight(),
                info.getWeight(),
                info.getBodyFatRate(),
                info.getMuscleMass(),
                info.getGoal().name(),
                info.getWeeklyGoalCount(),
                info.hasInbody(),
                info.getCreatedAt()
        );
    }

    @Getter
    public static class BodyInfoResult {
        private final Long bodyInfoId;
        private final Long userId;
        private final Float height;
        private final Float weight;
        private final Float bodyFatRate;
        private final Float muscleMass;
        private final String goal;
        private final Integer weeklyGoalCount;
        private final boolean hasInbody;
        private final LocalDateTime createdAt;

        public BodyInfoResult(Long bodyInfoId, Long userId, Float height, Float weight,
                              Float bodyFatRate, Float muscleMass, String goal,
                              Integer weeklyGoalCount, boolean hasInbody, LocalDateTime createdAt) {
            this.bodyInfoId = bodyInfoId;
            this.userId = userId;
            this.height = height;
            this.weight = weight;
            this.bodyFatRate = bodyFatRate;
            this.muscleMass = muscleMass;
            this.goal = goal;
            this.weeklyGoalCount = weeklyGoalCount;
            this.hasInbody = hasInbody;
            this.createdAt = createdAt;
        }
    }
    private BodyInfo convertToRoutineInput(UserBodyInfo info) {
        Enums.Goal goal = switch (info.getGoal()) {
            case DIET -> Enums.Goal.WEIGHT_LOSS;
            case MUSCLE -> Enums.Goal.MUSCLE_GAIN;
            case MAINTAIN -> Enums.Goal.MAINTENANCE;
            default -> throw new IllegalArgumentException("Invalid goal: " + info.getGoal());
        };
        Enums.DayOfWeek[] days = switch(info.getWeeklyGoalCount()){
            case 1 -> new Enums.DayOfWeek[]{Enums.DayOfWeek.WEDNESDAY};
            case 2 -> new Enums.DayOfWeek[]{Enums.DayOfWeek.MONDAY, Enums.DayOfWeek.THURSDAY};
            case 3 -> new Enums.DayOfWeek[]{Enums.DayOfWeek.MONDAY, Enums.DayOfWeek.WEDNESDAY, Enums.DayOfWeek.FRIDAY};
            case 4 -> new Enums.DayOfWeek[]{Enums.DayOfWeek.MONDAY, Enums.DayOfWeek.TUESDAY, Enums.DayOfWeek.THURSDAY, Enums.DayOfWeek.FRIDAY};
            case 5 -> new Enums.DayOfWeek[]{Enums.DayOfWeek.MONDAY, Enums.DayOfWeek.TUESDAY, Enums.DayOfWeek.WEDNESDAY, Enums.DayOfWeek.THURSDAY, Enums.DayOfWeek.FRIDAY};
            default -> throw new IllegalArgumentException("Invalid weekly goal count: " + info.getWeeklyGoalCount());
        };
        double bodyFat = info.getBodyFatRate() == null ? -1.0 : info.getBodyFatRate();
        double muscleMass = info.getMuscleMass() == null ? -1.0 : info.getMuscleMass();

        return new BodyInfo(0, info.getUserId(), info.getHeight(), info.getWeight(), info.getGender(), goal, days, info.getPreferredLevel(), bodyFat, muscleMass, info.getAvoidMuscleGroup());
    }
}
