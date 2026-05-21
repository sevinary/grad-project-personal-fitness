package com.fitness.repository;

import com.fitness.model.WeeklyRoutinePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WeeklyRoutinePlanRepository extends JpaRepository<WeeklyRoutinePlan, Long> {
    // WeeklyRoutinePlan 엔티티에 대한 CRUD 작업을 수행할 수 있는 메서드들이 자동으로 제공됩니다.
    void deleteByUserID(long userID);
    List<WeeklyRoutinePlan> findByUserID(long userID);
}