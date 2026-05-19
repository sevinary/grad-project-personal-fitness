package com.example.fitnessapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "routines")
@Getter
@Setter
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routineID")
    private Long routineId;

    private String goal;

    @ManyToOne
    @JoinColumn(name = "exerciseID", referencedColumnName = "exerciseID")
    private Exercise exercise;

    private Integer sets;

    private Integer reps;
}
