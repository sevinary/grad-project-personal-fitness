package com.fitness.repository;

import com.fitness.model.BodyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BodyInfoRepository extends JpaRepository<BodyInfo, Long> {
    // 유저ID로 회원 정보를 찾기 위해 메서드를 하나 추가해둡니다.
    BodyInfo findByUserID(Long userID);
}