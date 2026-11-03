package com.fitness.service;

import com.fitness.dto.BodyInfoRequest;
import com.fitness.model.UserBodyInfo;
import com.fitness.model.WeeklyRoutinePlan;
import com.fitness.repository.UserBodyInfoRepository;
import com.fitness.repository.WeeklyRoutinePlanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:body-info-transaction-test;DB_CLOSE_DELAY=-1")
class BodyInfoTransactionTest {
    @Autowired
    private BodyInfoApiService service;
    @Autowired
    private UserBodyInfoRepository bodyInfoRepository;
    @Autowired
    private WeeklyRoutinePlanRepository routineRepository;
    @SpyBean
    private RoutineSave routineSave;

    @Test
    void save_rollsBackBodyInfoAndRoutineWhenRoutineSaveFails() {
        BodyInfoRequest request = new BodyInfoRequest();
        request.setHeight(170.0f);
        request.setWeight(65.0f);
        request.setGoal(UserBodyInfo.WorkoutGoal.DIET);
        request.setWeeklyGoalCount(3);
        // Throw after inserting the plan and its children, outside any test transaction.
        doAnswer(invocation -> {
            invocation.callRealMethod();
            throw new IllegalStateException("Routine save failed");
        }).when(routineSave).saveOrUpdateRoutine(any(WeeklyRoutinePlan.class));

        assertThatThrownBy(() -> service.save(42L, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Routine save failed");
        assertThat(bodyInfoRepository.existsByUserId(42L)).isFalse();
        assertThat(routineRepository.findByUserID(42L)).isEmpty();
    }
}
