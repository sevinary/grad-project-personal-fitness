package com.fitness.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitness.model.WorkoutLog;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long>{
    
}
