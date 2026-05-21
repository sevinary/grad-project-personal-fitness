package com.fitness.service;

import com.fitness.model.*;
import com.fitness.model.Enums.*;
import com.fitness.repository.ExerciseRepository;

import java.util.*;

import org.springframework.stereotype.Service;

//추천 루틴 생성 알고리즘
@Service
public class RoutineRecommender {
    private final ExerciseRepository exerciseRepository; //운동 데이터베이스에 접근하기 위한 레포지토리
    public RoutineRecommender(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }
    //Exercise와 가중치 싸을 묶어주는 가벼운 내부클래스, 내림차순 자동 정렬 기능 포함
    static class ExerciseScore implements Comparable<ExerciseScore> {
        Exercise exercise;
        int score;
        //생성자
        public ExerciseScore(Exercise exercise, int score) {
            this.exercise = exercise;
            this.score = score;
        }
        //비교연산자
        @Override
        public int compareTo(ExerciseScore o) {
            return Integer.compare(o.score, this.score); //내림차순 정렬
        }
    }
    public WeeklyRoutinePlan recommendRoutine(BodyInfo bodyInfo, List<Exercise> exerciseDatabase) {
        //추천 루틴 생성 로직
        //0. 리스트 생성
        List<DailyRoutine> weeklyDays = new ArrayList<>();
        List<Exercise> anaerobicExercises = new ArrayList<>();
        List<Exercise> aerobicExercises = new ArrayList<>();
        List<Exercise> stretchingExercises = new ArrayList<>();
        for (Exercise exercise : exerciseDatabase) {
            if (bodyInfo.getBmi() > 25.0 && exercise.getDifficultyLevel() >= 3) {
                continue; //BMI가 25 이상인 경우 고난이도 운동 제외
            }
            if (exercise.getExerciseType() == ExerciseType.STRETCHING) {
                stretchingExercises.add(exercise); //스트레칭 운동은 별도 리스트에 추가
            } else if (exercise.getExerciseType() == ExerciseType.ANAEROBIC) {
                anaerobicExercises.add(exercise); //무산소 운동은 별도 리스트에 추가
            } else if (exercise.getExerciseType() == ExerciseType.AEROBIC) {
                aerobicExercises.add(exercise); //유산소 운동은 별도 리스트에 추가
            }
        }
        //주간 운동 일수에 따라 주간 목표 근육 그룹 결정
        MuscleGroup[] weeklytargets = determineWeeklySchedule(bodyInfo.getWeeklyWorkoutDays()); //주간 목표 근육 그룹 결정
        String[] dayNames = {"월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"};
        //주간 목표 근육 그룹에 따라 운동을 일별로 배분하는 로직
        for(int i=0;i<7;i++){
            //각 요일에 해당하는 일별 루틴 생성
            String currentDay = dayNames[i];
            MuscleGroup targetOfToday = weeklytargets[i];

            if (targetOfToday == MuscleGroup.NONE) {
                weeklyDays.add(new DailyRoutine(currentDay,true,new ArrayList<>())); //휴식일인 경우 빈 루틴 추가
                continue;
            }
            //각 운동 유형별로 점수 계산 후 정렬
            List<ExerciseScore> scoredStretch = calculateScore(bodyInfo, stretchingExercises,targetOfToday);
            List<ExerciseScore> scoredAnaerobic = calculateScore(bodyInfo, anaerobicExercises,targetOfToday);
            List<ExerciseScore> scoredAerobic = calculateScore(bodyInfo, aerobicExercises,targetOfToday);
            List<RoutineItem> dailyRoutine = new ArrayList<>();
            //스트레칭 운동 추가
            if (!scoredStretch.isEmpty()) {
                dailyRoutine.add(new RoutineItem(scoredStretch.get(0).exercise, 1, 10)); //스트레칭 운동 추가
            }
            boolean isInjuryDay = (bodyInfo.getAvoidMuscleGroup() != MuscleGroup.NONE) && (targetOfToday == bodyInfo.getAvoidMuscleGroup());
            if (isInjuryDay) {
                int addedStretchingCount = 1;
                for(int j = 1; j < scoredStretch.size() && addedStretchingCount < 3; j++){
                    dailyRoutine.add(new RoutineItem(scoredStretch.get(j).exercise, 2, 15)); //피하고 싶은 근육 그룹과 일치하는 스트레칭 운동 추가
                    addedStretchingCount++;
                }
            } else{
                buildMainRoutine(bodyInfo,scoredAerobic, scoredAnaerobic, dailyRoutine); //유산소 및 무산소 운동 추가
            }
            weeklyDays.add(new DailyRoutine(currentDay, false, dailyRoutine)); //일별 루틴 추가
        }
    return new WeeklyRoutinePlan(0, bodyInfo.getUserID(), weeklyDays); //추천된 주간 루틴 반환
    }
    public MuscleGroup[] determineWeeklySchedule(DayOfWeek[] weeklyWorkoutDays) {
        //주간 운동 일수에 따라 주간 목표 근육 그룹 결정 로직 구현
        MuscleGroup[] weeklytargets = new MuscleGroup[7];
        for(int i=0;i<7;i++){
            weeklytargets[i] = MuscleGroup.NONE; //초기값 설정
        }
        if(weeklyWorkoutDays == null || weeklyWorkoutDays.length == 0){
            //운동 요일 정보가 없는 경우 기본적으로 월, 수, 금 운동으로 설정
            return weeklytargets;
        }
        int totalDays = weeklyWorkoutDays.length;
        int dayIndex = 0;
        MuscleGroup[] splitMuscleGroups;
        if(totalDays == 1){
            splitMuscleGroups = new MuscleGroup[]{MuscleGroup.LEGS};
        } else if(totalDays == 2){
            splitMuscleGroups = new MuscleGroup[]{MuscleGroup.LEGS, MuscleGroup.BACK};
        } else if(totalDays == 3){
            splitMuscleGroups = new MuscleGroup[]{MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.LEGS};
        } else if(totalDays == 4){
            splitMuscleGroups = new MuscleGroup[]{MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.LEGS, MuscleGroup.SHOULDERS};
        } else {
            splitMuscleGroups = new MuscleGroup[]{MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.LEGS, MuscleGroup.SHOULDERS, MuscleGroup.ARMS};
        }
        for (DayOfWeek day : weeklyWorkoutDays) {
            int index = day.ordinal(); //요일을 인덱스로 변환
            if(index >= 0 && index < 7){
                weeklytargets[index] = splitMuscleGroups[dayIndex % splitMuscleGroups.length]; //운동 요일에 따라 목표 근육 그룹 할당
                dayIndex++;
            }
        }
        return weeklytargets;
    }
    public int calculateScore(BodyInfo bodyInfo, Exercise exercise, MuscleGroup targetMuscleGroup) {
        //운동과 회원 정보를 기반으로 점수 계산 로직 구현 (목표 근육 그룹 고려)
        int score = 0;
        int level = calculateDifficulty(bodyInfo);
        int difficultyDifference = Math.abs(exercise.getDifficultyLevel() - level);
        if(exercise.getTargetMuscleGroup() == targetMuscleGroup){
            score += 100; //운동의 목표 근육 그룹이 오늘의 목표 근육 그룹과 일치하는 경우 가중치 부여
        }
        if(difficultyDifference == 0){
            score += 40;
        } else if(difficultyDifference == 1){
            score += 20;
        }
        return score;
    }
    public int calculateDifficulty(BodyInfo bodyInfo) { 
        int preferredLevel = bodyInfo.getPreferredLevel();
        if(bodyInfo.getSkeletalMuscleMass() < 30.0){
            preferredLevel -= 1; //골격근량이 낮은 경우 난이도 1단계 낮춤
        } else if(bodyInfo.getBodyFat() > 25.0){
            preferredLevel += 1; //체지방률이 높은 경우 난이도 1단계 높임
        }
        return Math.max(1, Math.min(preferredLevel, 5)); //난이도는 1에서 5 사이로 제한
    }
    public List<ExerciseScore> calculateScore(BodyInfo bodyInfo, List<Exercise> exercises, MuscleGroup targetMuscleGroup) {
        List<ExerciseScore> scoredExercises = new ArrayList<>();
        for(Exercise exercise : exercises){
            int score = calculateScore(bodyInfo, exercise, targetMuscleGroup);
            scoredExercises.add(new ExerciseScore(exercise, score));
        }
        Collections.shuffle(scoredExercises);//점수가 동일한 운동이 있을 수 있으므로 순서를 랜덤하게 섞음
        Collections.sort(scoredExercises); //점수 기준으로 내림차순 정렬
        return scoredExercises;
    }
    //회원의 체형과 목표에 따른 운동 난이도 계산 로직 구현
    public void buildMainRoutine(BodyInfo bodyInfo, List<ExerciseScore> scoredAerobic, List<ExerciseScore> scoredAnaerobic, List<RoutineItem> dailyRoutine) {
        Goal userGoal = bodyInfo.getGoal();
        int defaultSets;
        int defaultReps;
        if(userGoal == Goal.MUSCLE_GAIN){
            int count = 0;
            defaultSets = (bodyInfo.getGender() == Gender.MALE) ? 4 : 3; //남성은 4세트, 여성은 3세트 권장
            defaultReps = (bodyInfo.getGender() == Gender.MALE) ? 10 : 12; //남성은 10회, 여성은 12회 권장
            for(ExerciseScore anaerobic : scoredAnaerobic){
                if(count >= 2) break;
                dailyRoutine.add(new RoutineItem(anaerobic.exercise, defaultSets, defaultReps));
                count++;
            }
        } else if(userGoal == Goal.WEIGHT_LOSS){
            defaultSets = 1;
            defaultReps = (bodyInfo.getGender() == Gender.MALE) ? 20:25;
            if(!scoredAerobic.isEmpty()){
                dailyRoutine.add(new RoutineItem(scoredAerobic.get(0).exercise, defaultSets, 30)); //체중 감량 목표인 경우 유산소 운동을 우선적으로 추가
            }
            defaultSets = 3;
            defaultReps = 15;
            if(!scoredAnaerobic.isEmpty()){
                for(ExerciseScore lowAnaerobic : scoredAnaerobic){
                    if(lowAnaerobic.exercise.getDifficultyLevel() <= 3){ //체중 감량 목표인 경우 난이도가 3 이하인 무산소 운동만 추가
                        dailyRoutine.add(new RoutineItem(lowAnaerobic.exercise, defaultSets, defaultReps)); //체중 감량 목표인 경우 무산소 운동도 추가
                    }
                }
            }
        } else if(userGoal == Goal.MAINTENANCE){
            int count = 0;
            defaultSets = 1;
            defaultReps = (bodyInfo.getGender() == Gender.MALE) ? 15:20;
            for(ExerciseScore aerobic : scoredAerobic){
                if(count >= 1) break;
                dailyRoutine.add(new RoutineItem(aerobic.exercise, defaultSets, defaultReps)); //유지 목표인 경우 유산소 운동을 추가
                count
                ++;
            }
            defaultSets = 3;
            defaultReps = 10;
            for(ExerciseScore anaerobic : scoredAnaerobic){
                if(count >= 2) break;
                dailyRoutine.add(new RoutineItem(anaerobic.exercise, defaultSets, defaultReps)); //유지 목표인 경우 무산소 운동도 추가
                count++;
            }
        }
    }
    public WeeklyRoutinePlan generateRoutine(BodyInfo bodyInfo) {
        List<Exercise> exerciseDatabase = exerciseRepository.findAll(); //운동 데이터베이스에서 모든 운동 정보 가져오기
        WeeklyRoutinePlan plan = recommendRoutine(bodyInfo, exerciseDatabase);
        plan.setRecommendedWeight(bodyInfo.getRecommendedWeight());
        return plan; //추천 루틴 생성 메서드 호출
    }
}