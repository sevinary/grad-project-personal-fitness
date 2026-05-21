package com.fitness.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
//회원의 신체 정보를 나타내는 클래스
public class BodyInfo {
    //회원 정보
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long bodyID; //고유 번호
    private long userID; //회원 고유 번호
    //필수 정보
    private double height;  //키
    private double weight; //몸무게

    @Enumerated(EnumType.STRING)
    private Enums.Gender gender; //성별

    @Enumerated(EnumType.STRING)
    private Enums.Goal goal;    //목적

    private Enums.DayOfWeek[] weeklyWorkoutDays;  //주당 운동 요일
    //선택 정보
    private int preferredLevel; //선호하는 운동 난이도 (1-5)
    private double bodyFat; //체지방률
    private double skeletalMuscleMass;  //골격근량
    @Enumerated(EnumType.STRING)
    private Enums.MuscleGroup avoidMuscleGroup;    //피하고 싶은 근육 부위
    //기타 정보
    private double bmi; //체질량 지수 (BMI)
    private double recommendedWeight;

    protected BodyInfo() {} //JPA를 위한 기본 생성자

    //필수 정보만 입력하는 생성자
    public BodyInfo(long bodyID, long userID, double height, double weight, Enums.Gender gender, Enums.Goal goal, Enums.DayOfWeek[] weeklyWorkoutDays) {
        //필수 정보 초기화
        this.bodyID = bodyID;
        this.userID = userID;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.goal = goal;
        this.weeklyWorkoutDays = weeklyWorkoutDays != null ? weeklyWorkoutDays : new Enums.DayOfWeek[]{Enums.DayOfWeek.MONDAY, Enums.DayOfWeek.WEDNESDAY, Enums.DayOfWeek.FRIDAY};

        //선택 사항 기본값 설정
        this.preferredLevel = 2;
        this.bodyFat = -1.0;
        this.skeletalMuscleMass = -1.0;
        this.avoidMuscleGroup = Enums.MuscleGroup.NONE;
        //BMI 계산
        this.bmi = (height > 0) ? weight / Math.pow(height / 100.0, 2) : -1.0;
        this.recommendedWeight = weight * 0.4;
    }
    //모든 정보를 입력하는 생성자
    public BodyInfo(long bodyID, long userID, double height, double weight, Enums.Gender gender, Enums.Goal goal, Enums.DayOfWeek[] weeklyWorkoutDays, int preferredLevel, double bodyFat, double skeletalMuscleMass, Enums.MuscleGroup avoidMuscleGroup) {
        this.bodyID = bodyID;
        this.userID = userID;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.goal = goal;
        this.weeklyWorkoutDays = (weeklyWorkoutDays == null || weeklyWorkoutDays.length == 0) ? new Enums.DayOfWeek[]{Enums.DayOfWeek.MONDAY, Enums.DayOfWeek.WEDNESDAY, Enums.DayOfWeek.FRIDAY} : weeklyWorkoutDays;

        this.preferredLevel = preferredLevel;
        this.bodyFat = (bodyFat <= 0) ? -1.0 : bodyFat;
        this.skeletalMuscleMass = (skeletalMuscleMass <= 0) ? -1.0 : skeletalMuscleMass;
        this.avoidMuscleGroup = (avoidMuscleGroup == null) ? Enums.MuscleGroup.NONE : avoidMuscleGroup;
        //BMI 계산
        this.bmi = (height > 0) ? weight / Math.pow(height / 100.0, 2) : -1.0;
        this.recommendedWeight = weight * 0.4;
    }

    //getter, setter 메서드
    public long getBodyID(){return bodyID;}
    public long getUserID(){return userID;}
    public double getHeight(){return height;}
    public void setHeight(double height){this.height = height; this.updateBmi();}
    public double getWeight(){return weight;}
    public void setWeight(double weight){this.weight = weight; this.updateBmi(); this.updateRecommendedWeight();}
    public Enums.Goal getGoal(){return goal;}
    public void setGoal(Enums.Goal goal){this.goal = goal;}
    public Enums.Gender getGender(){return gender;}
    public double getBmi(){return bmi;}
    public double getRecommendedWeight(){return recommendedWeight;}
    public int getPreferredLevel(){return preferredLevel;}
    public void setPreferredLevel(int preferredLevel){this.preferredLevel = preferredLevel;}

    public double getBodyFat(){return bodyFat;}
    public void setBodyFat(double bodyFat){this.bodyFat = bodyFat;}

    public double getSkeletalMuscleMass(){return skeletalMuscleMass;}
    public void setSkeletalMuscleMass(double skeletalMuscleMass){this.skeletalMuscleMass = skeletalMuscleMass;}
    
    public Enums.DayOfWeek[] getWeeklyWorkoutDays(){return weeklyWorkoutDays;}
    public void setWeeklyWorkoutDays(Enums.DayOfWeek[] weeklyWorkoutDays){this.weeklyWorkoutDays = weeklyWorkoutDays;}
    
    public Enums.MuscleGroup getAvoidMuscleGroup(){return avoidMuscleGroup;}
    public void setAvoidMuscleGroup(Enums.MuscleGroup avoidMuscleGroup){this.avoidMuscleGroup = avoidMuscleGroup;}
    //BMI 업데이트 메서드
    private void updateBmi() {
        this.bmi = (height > 0) ? weight / Math.pow(height / 100.0, 2) : -1.0;
    }
    private void updateRecommendedWeight(){
        this.recommendedWeight = weight * 0.4;
    }
}