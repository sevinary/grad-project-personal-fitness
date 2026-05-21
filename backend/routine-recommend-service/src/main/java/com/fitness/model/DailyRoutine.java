package com.fitness.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

//일별 루틴 정보를 나타내는 클래스
@Entity
public class DailyRoutine{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; //고유 번호

    private String dayName; //요일 이름
    private boolean restDay; //휴식 여부 (0: 운동일, 1: 휴식일)
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<RoutineItem> dailyExercises; //운동 목록

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "weekly_routine_plan_id")
    private WeeklyRoutinePlan weeklyRoutinePlan; //주간 루틴 계획

    public void setWeeklyRoutinePlan(WeeklyRoutinePlan weeklyRoutinePlan) {
        this.weeklyRoutinePlan = weeklyRoutinePlan;
    }

    protected DailyRoutine() {} //JPA를 위한 기본 생성자
    //생성자
    public DailyRoutine(String dayName, boolean restDay, List<RoutineItem> dailyExercises) {
        this.dayName = dayName;
        this.restDay = restDay;
        this.dailyExercises = dailyExercises;
    }

    public void addRoutineItems(List<RoutineItem> routineItems) {
        this.dailyExercises = routineItems;
    }
    //getter 메서드
    public String getDayName(){return dayName;}
    public boolean getRestDay(){return restDay;}
    public List<RoutineItem> getDailyExercises(){return dailyExercises;}
    public WeeklyRoutinePlan getWeeklyRoutinePlan(){return weeklyRoutinePlan;}
}