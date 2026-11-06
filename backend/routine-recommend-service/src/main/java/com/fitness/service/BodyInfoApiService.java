package com.fitness.service;

import com.fitness.dto.BodyInfoRequest;
import com.fitness.exception.AppException;
import com.fitness.model.BodyInfo;
import com.fitness.model.Enums.DayOfWeek;
import com.fitness.model.Enums.Goal;
import com.fitness.model.UserBodyInfo;
import com.fitness.repository.UserBodyInfoRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

        info = userBodyInfoRepository.save(info);
        BodyInfo routineBodyInfo = toRoutineBodyInfo(info);
        routineSave.saveOrUpdateRoutine(routineRecommender.generateRoutine(routineBodyInfo));
        return toResult(info);
    }

    private BodyInfo toRoutineBodyInfo(UserBodyInfo info) {
        Goal goal = switch (info.getGoal()) {
            case DIET -> Goal.WEIGHT_LOSS;
            case MUSCLE -> Goal.MUSCLE_GAIN;
            case MAINTAIN -> Goal.MAINTENANCE;
        };
        BodyInfo bodyInfo = new BodyInfo(0, info.getUserId(), info.getHeight(), info.getWeight(),
                null, goal, defaultWorkoutDays(info.getWeeklyGoalCount()));
        // Missing InBody measurements retain BodyInfo's -1 (unknown) defaults.
        if (info.getBodyFatRate() != null) {
            bodyInfo.setBodyFat(info.getBodyFatRate());
        }
        if (info.getMuscleMass() != null) {
            bodyInfo.setSkeletalMuscleMass(info.getMuscleMass());
        }
        return bodyInfo;
    }

    private DayOfWeek[] defaultWorkoutDays(int weeklyGoalCount) {
        return switch (weeklyGoalCount) {
            case 1 -> new DayOfWeek[]{DayOfWeek.WEDNESDAY};
            case 2 -> new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.THURSDAY};
            case 3 -> new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY};
            case 4 -> new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY};
            case 5 -> new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY};
            case 6 -> new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY};
            case 7 -> DayOfWeek.values();
            default -> throw AppException.badRequest("주당 운동 횟수는 1~7회여야 합니다.");
        };
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
}
