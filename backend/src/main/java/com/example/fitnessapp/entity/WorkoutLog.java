package com.example.fitnessapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "workout_log")
@Getter
@Setter
public class WorkoutLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workoutlogID")
    private Long workoutLogId;

    @ManyToOne
    @JoinColumn(name = "userID", referencedColumnName = "userID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "exerciseID", referencedColumnName = "exerciseID")
    private Exercise exercise;

    @Column(name = "date")
    private LocalDate date;

    private Boolean completed;
}
