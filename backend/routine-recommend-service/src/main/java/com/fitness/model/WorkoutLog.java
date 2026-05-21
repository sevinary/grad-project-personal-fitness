package com.fitness.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;

@Entity
@Getter
public class WorkoutLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long userID;
    private long exerciseID;
    private LocalDate workoutDate;
    private int sets;
    private int reps;
    private double exerciseWeight;

    protected WorkoutLog(){}

    public WorkoutLog(long userID, long exerciseID, LocalDate workoutDate, int sets, int reps, double exerciseWeight){
        this.userID = userID;
        this.exerciseID = exerciseID;
        this.workoutDate = workoutDate;
        this.sets = sets;
        this.reps = reps;
        this.exerciseWeight = exerciseWeight;
    }
    public record WorkoutLogRequest(long userID, long exerciseID, LocalDate workoutDate, int sets, int reps, double exerciseWeight){}
}