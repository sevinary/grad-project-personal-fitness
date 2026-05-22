package com.fitness.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitness.model.WorkoutLog;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long>{
    List<WorkoutLog> findByUserIDAndWorkoutDateBetween(long userID, LocalDate start, LocalDate end);
    Optional<WorkoutLog>findByUserIDAndExerciseIDAndWorkoutDate(long userID, long exerciseID,LocalDate workoutDate);
}
