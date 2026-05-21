package com.fitness.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_body_info")
@Getter
@Setter
@NoArgsConstructor
public class UserBodyInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bodyInfoId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Float height;

    @Column(nullable = false)
    private Float weight;

    private Float bodyFatRate;

    private Float muscleMass;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkoutGoal goal;

    @Column(nullable = false)
    private Integer weeklyGoalCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public enum WorkoutGoal {
        DIET, MUSCLE, MAINTAIN
    }

    public boolean hasInbody() {
        return bodyFatRate != null && muscleMass != null;
    }
}
