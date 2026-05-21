package com.fitness.model;

import java.time.LocalDate;

import com.fitness.model.Enums.ExerciseType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

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

    @Enumerated(EnumType.STRING)
    private ExerciseType exerciseType;

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
    public void setLogID(long logID){this.logID = logID;}
    public long getUserID(){return this.userID;}
    public void setUserID(long userID){this.userID = userID;}
    public long getExerciseID(){return this.exerciseID;}
    public void setExerciseID(long exerciseID){this.exerciseID = exerciseID;}
    public LocalDate getWorkoutDate(){return this.workoutDate;}
    public void setWorkoutDate(LocalDate workouDate){this.workoutDate = workouDate;}
    public int getSets(){return this.sets;}
    public void setSets(int sets){this.sets = sets;}
    public int getReps(){return this.reps;}
    public void setReps(int reps){this.reps = reps;}
    public double getExerciseWeight(){return this.exerciseWeight;}
    public void setExerciseWeight(double exerciseWeight){this.exerciseWeight = exerciseWeight;}
    public ExerciseType getExerciseType(){return this.exerciseType;}
    public void setExerciseType(ExerciseType exerciseType){this.exerciseType = exerciseType;}

    public record WorkoutLogRequest(long userID, long exerciseID, LocalDate workoutDate, @Min(1) int sets, @Min(1) int reps, @Min(0) double exerciseWeight){
        public WorkoutLog toEntity(){
            return new WorkoutLog(this.userID,this.exerciseID,this.workoutDate,this.sets,this.reps,this.exerciseWeight);
        }
    }
    public record WorkoutLogResponse(long logID, long userID, long exerciseID, ExerciseType exerciseType, LocalDate workoutDate, int sets, int reps, double exerciseWeight){
        public WorkoutLogResponse(WorkoutLog log){
            this(log.getLogID(),log.getUserID(),log.getExerciseID(),log.getExerciseType(),log.getWorkoutDate(),log.getSets(),log.getReps(),log.getExerciseWeight());
        }
    }
    public record DashboardResponse(int totalWorkoutDays, double anaerobicLoad, double aerobicLoad, double stretchingLoad){}
}