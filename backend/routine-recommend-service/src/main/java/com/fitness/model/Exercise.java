package com.fitness.model;

import jakarta.persistence.*;

@Entity
@Table(name = "exercise")
//운동 정보를 나타내는 클래스
public class Exercise{
    @Id
    @Column(name = "exercise_id")
    private long exerciseID; //고유 번호
    private String name; //운동 이름

    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_type")
    private Enums.ExerciseType exerciseType; //운동 유형
    @Enumerated(EnumType.STRING)
    @Column(name = "target_muscle_group")
    private Enums.MuscleGroup targetMuscleGroup; //목적 근육 그룹
    @Column(name = "difficulty_level")
    private int difficultyLevel; //난이도 (1-5)

    //생성자
    protected Exercise(){} //JPA용 기본 생성자
    public Exercise(long exerciseID, String name, Enums.ExerciseType exerciseType, Enums.MuscleGroup targetMuscleGroup, int difficultyLevel) {
        this.exerciseID = exerciseID;
        this.name = name;
        this.exerciseType = exerciseType;
        this.targetMuscleGroup = targetMuscleGroup;
        this.difficultyLevel = difficultyLevel;
    }
    //getter 메서드
    public long getExerciseID(){return exerciseID;}
    public String getName(){return name;}
    public Enums.ExerciseType getExerciseType(){return exerciseType;}
    public Enums.MuscleGroup getTargetMuscleGroup(){return targetMuscleGroup;}
    public int getDifficultyLevel(){return difficultyLevel;}
}