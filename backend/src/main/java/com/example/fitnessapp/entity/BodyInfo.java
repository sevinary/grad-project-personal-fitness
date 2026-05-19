package com.example.fitnessapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "body_info")
@Getter
@Setter
public class BodyInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bodyID")
    private Long bodyId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userID")
    private User user;

    private Double height;

    private Double weight;

    @Column(name = "body_fat")
    private Double bodyFat;

    private String goal;

    @Column(name = "weekly_goal")
    private String weeklyGoal;
}
