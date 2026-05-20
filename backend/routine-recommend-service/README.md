#가중치 계산 및 루틴 생성 알고리즘 규칙#

-난이도 결정 방식:
    -기본 난이도 : 사용자 입력
    -골격근량 <30.0 -> 난이도 1단계 하향
    -체지방률 > 25.0 -> 난이도 1단계 상향
    -범위 : 1~5

-데이터 필터링:
    -기피 부위를 다루는 운동은 필터링

-루틴 생성 방식:
    -필터링 된 운동을 제외하고 구성에 맞춰 가중치에 따른 난수로 생성

-루틴 구성:
    -1번자리에는 스트레칭 고정
    -2,3번 자리는 목적에 따라 유산소, 무산소 조합

-운동 데이터:
    -운동 ID 체계:
        -1000번대 : 무산소 운동
        -2000번대 : 유산소 운동
        -3000번대 : 스트레칭
    -현재 총 30종의 운동 종목 준비

-실제 API 명세 예시:
    -BodyInfo:
        -1.필수 정보만:
{
  "bodyID": 1,
  "userID": 1,
  "height": 178.0,
  "weight": 75.0,
  "gender": "MALE",
  "goal":"MUSCLE_GAIN",
  "weeklyWorkoutDays": ["MONDAY", "WEDNESDAY", "FRIDAY"]
}
        -2.선택 정보 포함:
{
  "bodyID": 1,
  "userID": 1,
  "height": 178.0,
  "weight": 80.0,
  "gender": "MALE",
  "goal": "MUSCLE_GAIN",
  "weeklyWorkoutDays": ["MONDAY", "WEDNESDAY", "FRIDAY"],
  "preferredLevel": 3,
  "bodyFat": 27.5,
  "skeletalMuscleMass": 29.0,
  "avoidMuscleGroup": "SHOULDERS"
}

-설치 및 실행 방법:
    Spring Boot 프로젝트 내 routine-service 모듈을 포함하여 빌드하면 자동으로 data.sql이 H2 데이터세이스에 로드됩니다. 이후 Postman을 통해 테스트 가능
    -Postman 링크 : http://localhost:8080/api/routines/recommend

-mvn spring-boot:run 시 클래스 못 찾는 경우
    -mvn clean install 실행
    -java -jar target/routine-recommend-service-1.0-SNAPSHOT.jar 실행

-postman 예시 결과 예시
    -1번
{
    "weeklyPlanID": 0,
    "userID": 1,
    "weeklyRoutines": [
        {
            "dayName": "월요일",
            "restDay": false,
            "dailyExercises": [
                {
                    "exercise": {
                        "exerciseID": 3003,
                        "name": "가슴 스트레칭",
                        "exerciseType": "STRETCHING",
                        "targetMuscleGroup": "CHEST",
                        "difficultyLevel": 1
                    },
                    "sets": 1,
                    "reps": 10
                },
                {
                    "exercise": {
                        "exerciseID": 1006,
                        "name": "인클라인 벤치프레스",
                        "exerciseType": "ANAEROBIC",
                        "targetMuscleGroup": "CHEST",
                        "difficultyLevel": 4
                    },
                    "sets": 4,
                    "reps": 10
                },
                {
                    "exercise": {
                        "exerciseID": 1001,
                        "name": "벤치프레스",
                        "exerciseType": "ANAEROBIC",
                        "targetMuscleGroup": "CHEST",
                        "difficultyLevel": 3
                    },
                    "sets": 4,
                    "reps": 10
                }
            ]
        },
        {
            "dayName": "화요일",
            "restDay": true,
            "dailyExercises": []
        },
        {
            "dayName": "수요일",
            "restDay": false,
            "dailyExercises": [
                {
                    "exercise": {
                        "exerciseID": 3004,
                        "name": "등 스트레칭",
                        "exerciseType": "STRETCHING",
                        "targetMuscleGroup": "BACK",
                        "difficultyLevel": 1
                    },
                    "sets": 1,
                    "reps": 10
                },
                {
                    "exercise": {
                        "exerciseID": 1008,
                        "name": "렛풀다운",
                        "exerciseType": "ANAEROBIC",
                        "targetMuscleGroup": "BACK",
                        "difficultyLevel": 3
                    },
                    "sets": 4,
                    "reps": 10
                },
                {
                    "exercise": {
                        "exerciseID": 1003,
                        "name": "데드리프트",
                        "exerciseType": "ANAEROBIC",
                        "targetMuscleGroup": "BACK",
                        "difficultyLevel": 4
                    },
                    "sets": 4,
                    "reps": 10
                }
            ]
        },
        {
            "dayName": "목요일",
            "restDay": true,
            "dailyExercises": []
        },
        {
            "dayName": "금요일",
            "restDay": false,
            "dailyExercises": [
                {
                    "exercise": {
                        "exerciseID": 3002,
                        "name": "하체 스트레칭",
                        "exerciseType": "STRETCHING",
                        "targetMuscleGroup": "LEGS",
                        "difficultyLevel": 1
                    },
                    "sets": 1,
                    "reps": 10
                },
                {
                    "exercise": {
                        "exerciseID": 1007,
                        "name": "레그 프레스",
                        "exerciseType": "ANAEROBIC",
                        "targetMuscleGroup": "LEGS",
                        "difficultyLevel": 3
                    },
                    "sets": 4,
                    "reps": 10
                },
                {
                    "exercise": {
                        "exerciseID": 1002,
                        "name": "스쿼트",
                        "exerciseType": "ANAEROBIC",
                        "targetMuscleGroup": "LEGS",
                        "difficultyLevel": 4
                    },
                    "sets": 4,
                    "reps": 10
                }
            ]
        },
        {
            "dayName": "토요일",
            "restDay": true,
            "dailyExercises": []
        },
        {
            "dayName": "일요일",
            "restDay": true,
            "dailyExercises": []
        }
    ]
}
    -2번:
{
    "weeklyPlanID": 0,
    "userID": 1,
    "weeklyRoutines": [
        {
            "dayName": "월요일",
            "restDay": false,
            "dailyExercises": [
                {
                    "exercise": {
                        "exerciseID": 3003,
                        "name": "가슴 스트레칭",
                        "exerciseType": "STRETCHING",
                        "targetMuscleGroup": "CHEST",
                        "difficultyLevel": 1
                    },
                    "sets": 1,
                    "reps": 10
                },
                {
                    "exercise": {
                        "exerciseID": 1005,
                        "name": "덤벨 컬",
                        "exerciseType": "ANAEROBIC",
                        "targetMuscleGroup": "ARMS",
                        "difficultyLevel": 2
                    },
                    "sets": 4,
                    "reps": 10
                },
                {
                    "exercise": {
                        "exerciseID": 1009,
                        "name": "사이드 레터럴 레이즈",
                        "exerciseType": "ANAEROBIC",
                        "targetMuscleGroup": "SHOULDERS",
                        "difficultyLevel": 2
                    },
                    "sets": 4,
                    "reps": 10
                }
            ]
        },
        {
            "dayName": "화요일",
            "restDay": true,
            "dailyExercises": []
        },
        {
            "dayName": "수요일",
            "restDay": false,
            "dailyExercises": [
                {
                    "exercise": {
                        "exerciseID": 3007,
                        "name": "요가 코브라 자세",
                        "exerciseType": "STRETCHING",
                        "targetMuscleGroup": "BACK",
                        "difficultyLevel": 2
                    },
                    "sets": 1,
                    "reps": 10
                },
                {
                    "exercise": {
                        "exerciseID": 1010,
                        "name": "트라이셉스 익스텐션",
                        "exerciseType": "ANAEROBIC",
                        "targetMuscleGroup": "ARMS",
                        "difficultyLevel": 2
                    },
                    "sets": 4,
                    "reps": 10
                },
                {
                    "exercise": {
                        "exerciseID": 1005,
                        "name": "덤벨 컬",
                        "exerciseType": "ANAEROBIC",
                        "targetMuscleGroup": "ARMS",
                        "difficultyLevel": 2
                    },
                    "sets": 4,
                    "reps": 10
                }
            ]
        },
        {
            "dayName": "목요일",
            "restDay": true,
            "dailyExercises": []
        },
        {
            "dayName": "금요일",
            "restDay": false,
            "dailyExercises": [
                {
                    "exercise": {
                        "exerciseID": 3008,
                        "name": "런지 스트레칭",
                        "exerciseType": "STRETCHING",
                        "targetMuscleGroup": "LEGS",
                        "difficultyLevel": 2
                    },
                    "sets": 1,
                    "reps": 10
                },
                {
                    "exercise": {
                        "exerciseID": 1009,
                        "name": "사이드 레터럴 레이즈",
                        "exerciseType": "ANAEROBIC",
                        "targetMuscleGroup": "SHOULDERS",
                        "difficultyLevel": 2
                    },
                    "sets": 4,
                    "reps": 10
                },
                {
                    "exercise": {
                        "exerciseID": 1005,
                        "name": "덤벨 컬",
                        "exerciseType": "ANAEROBIC",
                        "targetMuscleGroup": "ARMS",
                        "difficultyLevel": 2
                    },
                    "sets": 4,
                    "reps": 10
                }
            ]
        },
        {
            "dayName": "토요일",
            "restDay": true,
            "dailyExercises": []
        },
        {
            "dayName": "일요일",
            "restDay": true,
            "dailyExercises": []
        }
    ]
}