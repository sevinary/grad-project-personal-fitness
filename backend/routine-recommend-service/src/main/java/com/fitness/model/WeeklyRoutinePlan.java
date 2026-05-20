package com.fitness.model;

import java.util.List;

//주간 루틴 정보를 나타내는 클래스
public class WeeklyRoutinePlan{
    private long weeklyPlanID; //고유 번호
    private long userID; //회원 고유 번호
    private List<DailyRoutine> weeklyRoutines; //주간 루틴 목록

    //생성자
    public WeeklyRoutinePlan(long weeklyPlanID, long userID, List<DailyRoutine> weeklyRoutines) {
        this.weeklyPlanID = weeklyPlanID;
        this.userID = userID;
        this.weeklyRoutines = weeklyRoutines;
    }
    //getter 메서드
    public long getWeeklyPlanID(){return weeklyPlanID;}
    public long getUserID(){return userID;}
    public List<DailyRoutine> getWeeklyRoutines(){return weeklyRoutines;}
}