package com.fitness.repository;

import com.fitness.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    // 이제부터 이 인터페이스를 통해 exercise 테이블의 모든 데이터를 다룰 수 있습니다.
}