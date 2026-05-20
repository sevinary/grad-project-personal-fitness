package com.fitness.repository;

import com.fitness.model.routines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutineRepository extends JpaRepository<routines, Long> {
    // Routine 엔티티에 대한 CRUD 작업을 수행할 수 있는 메서드들이 자동으로 제공됩니다.
}