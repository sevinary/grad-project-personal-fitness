package com.fitness.service;

import com.fitness.dto.BodyInfoRequest;
import com.fitness.model.BodyInfo;
import com.fitness.model.Enums;
import com.fitness.model.UserBodyInfo;
import com.fitness.repository.UserBodyInfoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BodyInfoApiServiceTest {
    @Mock
    private UserBodyInfoRepository repository;
    @Mock
    private RoutineRecommender recommender;
    @Mock
    private RoutineSave routineSave;
    @InjectMocks
    private BodyInfoApiService service;

    @Test
    void save_passesMeasurementsAndComputedDefaultsToRecommender() {
        BodyInfoRequest request = request();
        request.setBodyFatRate(24.3f);
        request.setMuscleMass(27.1f);
        BodyInfo bodyInfo = saveAndCapture(request);

        assertThat(bodyInfo.getUserID()).isEqualTo(42L);
        assertThat(bodyInfo.getHeight()).isEqualTo(170.0);
        assertThat(bodyInfo.getWeight()).isEqualTo(65.0);
        assertThat(bodyInfo.getBodyFat()).isEqualTo((double) request.getBodyFatRate());
        assertThat(bodyInfo.getSkeletalMuscleMass()).isEqualTo((double) request.getMuscleMass());
        assertThat(bodyInfo.getBmi()).isCloseTo(22.49, org.assertj.core.data.Offset.offset(0.01));
        assertThat(bodyInfo.getRecommendedWeight()).isEqualTo(26.0);
        assertThat(bodyInfo.getPreferredLevel()).isEqualTo(2);
        assertThat(bodyInfo.getGender()).isNull();
        assertThat(bodyInfo.getAvoidMuscleGroup()).isEqualTo(Enums.MuscleGroup.NONE);
    }

    @Test
    void save_withoutInbody_retainsUnknownMeasurementsAndDefaultDifficulty() {
        BodyInfo bodyInfo = saveAndCapture(request());
        assertThat(bodyInfo.getBodyFat()).isEqualTo(-1.0);
        assertThat(bodyInfo.getSkeletalMuscleMass()).isEqualTo(-1.0);
        assertThat(new RoutineRecommender(null).calculateDifficulty(bodyInfo)).isEqualTo(2);
    }

    @Test
    void save_withOnlyBodyFat_stillUsesKnownMeasurementForDifficulty() {
        BodyInfoRequest request = request();
        request.setBodyFatRate(28.0f);
        BodyInfo bodyInfo = saveAndCapture(request);
        assertThat(new RoutineRecommender(null).calculateDifficulty(bodyInfo)).isEqualTo(3);
    }

    private BodyInfo saveAndCapture(BodyInfoRequest request) {
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        service.save(42L, request);
        ArgumentCaptor<BodyInfo> captor = ArgumentCaptor.forClass(BodyInfo.class);
        verify(recommender).generateRoutine(captor.capture());
        return captor.getValue();
    }

    private BodyInfoRequest request() {
        BodyInfoRequest request = new BodyInfoRequest();
        request.setHeight(170.0f);
        request.setWeight(65.0f);
        request.setGoal(UserBodyInfo.WorkoutGoal.MUSCLE);
        request.setWeeklyGoalCount(3);
        return request;
    }
}
