package com.fitness.model;

import java.util.List;
import jakarta.persistence.*;

@Entity
//루틴 정보를 나타내는 클래스
public class Routine{
    //루틴 정보
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long routineID; //고유 번호

    @Enumerated(EnumType.STRING)
    private Enums.Goal goal; //루틴 목적

    @OneToMany(cascade = CascadeType.ALL)
    private List<RoutineItem> exercises; //운동 목록

    protected Routine() {} //JPA를 위한 기본 생성자
    //생성자
    public Routine(Enums.Goal goal, List<RoutineItem> exercises) {
        this.goal = goal;
        this.exercises = exercises;
    }
    public Routine(long routineID, Enums.Goal goal, List<RoutineItem> exercises) {
        this.routineID = routineID;
        this.goal = goal;
        this.exercises = exercises;
    }
    //getter 메서드
    public long getRoutineID(){return routineID;}
    public Enums.Goal getGoal(){return goal;}
    public List<RoutineItem> getExercises(){return exercises;}

    public void setRoutineID(long routineID) {
        this.routineID = routineID;
    }

    public void setGoal(Enums.Goal goal) {
        this.goal = goal;
    }
    public void setExercises(List<RoutineItem> exercises) {
        this.exercises = exercises;
    }
}