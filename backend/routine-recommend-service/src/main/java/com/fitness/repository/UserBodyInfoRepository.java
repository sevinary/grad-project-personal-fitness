package com.fitness.repository;

import com.fitness.model.UserBodyInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBodyInfoRepository extends JpaRepository<UserBodyInfo, Long> {
    Optional<UserBodyInfo> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
