package com.fitness.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Setter;
import java.util.ArrayList;

//주간 루틴 정보를 나타내는 클래스
@Entity
@Setter
public class WeeklyRoutinePlan{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long weeklyRoutineID; //고유 번호
    
    private long weeklyPlanID; //고유 번호
    private long userID; //회원 고유 번호
    @OneToMany(mappedBy = "weeklyRoutinePlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyRoutine> weeklyRoutines = new ArrayList<>(); //주간 루틴 목록

    public void addDailyRoutine(DailyRoutine dailyRoutine) {
        weeklyRoutines.add(dailyRoutine);
        dailyRoutine.setWeeklyRoutinePlan(this);
    }
    //생성자
    protected WeeklyRoutinePlan(){}
    public WeeklyRoutinePlan(long weeklyPlanID, long userID, List<DailyRoutine> weeklyRoutines) {
        this.weeklyPlanID = weeklyPlanID;
        this.userID = userID;
        this.weeklyRoutines = weeklyRoutines;
    }
    public List<DailyRoutine> getWeeklyRoutines() {
        return this.weeklyRoutines;
    }
    public String getRoutineDescription() {
        StringBuilder description = new StringBuilder();
        for (DailyRoutine dailyRoutine : this.weeklyRoutines) {
            description.append(dailyRoutine.getDayName()).append(": ");
            if(dailyRoutine.getRestDay()) {
                description.append("휴식일\n");
                continue;
            } else{
            for (RoutineItem item : dailyRoutine.getDailyExercises()) {
                description.append(item.getExercise().getName())
                            .append(" (")
                            .append(item.getSets()).append("세트, ")
                            .append(item.getReps()).append("회)\n");
            }
            description.append("\n");
            }
        }
        return description.toString();
    }
    public long getWeeklyPlanID() {
        return this.weeklyPlanID;
    }
    public long getUserID() {
        return this.userID;
    }
    public long getWeeklyRoutineID() {
        return this.weeklyRoutineID;
    }
    @Column(columnDefinition = "TEXT")
    private String routineDescription; //루틴 설명 (일별 운동 목록을 문자열로 저장)
    public void updateRoutineDescription() {
        this.routineDescription = this.getRoutineDescription();
        System.out.println("Updated routine description: " + this.routineDescription); //디버그용 출력
    }
}