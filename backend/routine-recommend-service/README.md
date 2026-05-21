#코드 동작 방식#

    -BodyInfo 입력 -> BodyInfo와 Exercises 테이블의 데이터를 활용하여 루틴 생성
    -> 생성한 루틴을 서버에 저장
    -저장된 루틴은 userID가 동일한 새로운 루틴이 생성될 경우 덮어씌워짐

#가중치 계산 및 루틴 생성 알고리즘 규칙#

-난이도 결정 방식:
    -기본 난이도 : 사용자 입력
    -골격근량 <30.0 -> 난이도 1단계 하향
    -체지방률 > 25.0 -> 난이도 1단계 상향
    -범위 : 1~5
-데이터 필터링:
    -기피 부위를 다루는 운동은 필터링
    -사용자의 난이도에 비해 지나치게 어렵거나 쉬운 운동도 필터링
-루틴 생성 방식:
    -필터링 된 운동을 제외하고 구성에 맞춰 가중치에 따른 난수로 생성
    -이미 동일한 userID의 루틴이 존재할 경우 기존 루틴을 삭제하고 생성
-루틴 구성:
    -1번자리에는 스트레칭 고정
    -2,3번 자리는 목적에 따라 유산소, 무산소 조합

-운동 데이터:
    -운동 ID 체계:
        -1000번대 : 무산소 운동
        -2000번대 : 유산소 운동
        -3000번대 : 스트레칭
    -현재 총 30종의 운동 종목 준비

#사용 예시#

-각종 기능
    -POST /api/routine/recommend-and-save : recommend와 save를 호출
    -POST /api/routines/recommend : BodyInfo를 기반으로 루틴을 생성
    -POST /api/routines/save : 생성된 루틴을 테이블에 저장
    -GET  /api/routines/{id} : 특정 루틴의 상세 내용을 조회
-설치 및 실행 방법:
    -Spring Boot 프로젝트 내 routine-service 모듈을 포함하여 빌드하면 'src/main/resource/data.sql'을 통해 운동 데이터가 H2 데이터세이스에 자동으로 로드됩니다. 이후 Postman을 통해 테스트할 수 있습니다.
    -h2 콘솔 접속 : http://localhost:8080/h2-console
    (테스트용이며 추후 MySQL로 변경 예정)
        -JDBC URL: 'jdbc:h2:mem:fitnessdb'
        -User: 'sa'
        -Password : (없음)

#추가 예정 기능#
    -입력값 예외 처리
    -일일 루틴 체크
    -특정 운동 고정 또는 특정 운동만 변경
    -에러 핸들링 기록 조회

#실제 API 명세 예시#

-입력용 링크
    -루틴 생성:
        -Postman 링크 : POST http://localhost:8080/api/routines/recommend-and-save
    -루틴 조회:
        -Postman 링크: GET http://localhost:8080/api/routines/recommend?userId={userID}
    -로그 생성:
        -Postman 링크: POST http://localhost:8080/api/workout-logs
    -로그 삭제:
        -Postman 링크: DELETE http://localhost:8080/api/workout-logs/101
    -주간 로즈 조회:
        GET http://localhost:8080/api/workout-logs/weekly/{userID}
    -월간 로그 조회:
        GET http://localhost:8080/api/workout-logs/monthly/{userID}

    -입력 양식 예시
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
    -로그 생성
{
    "userID": 1,
    "exerciseID": 101,
    "workoutDate": "2026-05-21",
    "sets": 3,
    "reps": 12,
    "exerciseWeight": 50.5
}
    
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