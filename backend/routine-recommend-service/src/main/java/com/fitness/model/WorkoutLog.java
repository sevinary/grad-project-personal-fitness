package com.fitness.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;

import lombok.NonNull;

@Entity
public class WorkoutLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long logID;

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
    public long getLogID(){return this.logID;}
    public long getUserID(){return this.userID;}
    public long getExerciseID(){return this.exerciseID;}
    public LocalDate getWorkoutDate(){return this.workoutDate;}
    public int getSets(){return this.sets;}
    public int getReps(){return this.reps;}
    public double getExerciseWeight(){return this.exerciseWeight;}
    public record WorkoutLogRequest(long userID, long exerciseID, @NonNull LocalDate workoutDate, @Min(1) int sets, @Min(1) int reps, @Min(0) double exerciseWeight){}
    public record WorkoutLogResponse(long logID, long userID, long exerciseID, LocalDate workoutDate, int sets, int reps, double exerciseWeight){
        public WorkoutLogResponse(WorkoutLog log){
            this(log.getLogID(),log.getUserID(),log.getExerciseID(),log.getWorkoutDate(),log.getSets(),log.getReps(),log.getExerciseWeight());
        }
    }
    public record WeeklyDashboardResponse(int totalWorkoutDays,int totalSets, double totalVolume){}
}