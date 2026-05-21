package com.fitness.service;

import com.fitness.dto.BodyInfoRequest;
import com.fitness.exception.AppException;
import com.fitness.model.UserBodyInfo;
import com.fitness.repository.UserBodyInfoRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BodyInfoApiService {

    private final UserBodyInfoRepository userBodyInfoRepository;

    public BodyInfoApiService(UserBodyInfoRepository userBodyInfoRepository) {
        this.userBodyInfoRepository = userBodyInfoRepository;
    }

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
}
