package com.fitness.model;

import jakarta.persistence.*;

@Entity
@Table(name = "routine_items")
public class RoutineItem{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //고유 ID

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise; //운동 정보
    private int sets; //세트 수
    private int reps; //반복 횟수

    protected RoutineItem(){}
    //생성자
    public RoutineItem(Exercise exercise, int sets, int reps) {
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
    }
    //getter 메서드
    public Exercise getExercise(){return exercise;}
    public int getSets(){return sets;}
    public int getReps(){return reps;}
}