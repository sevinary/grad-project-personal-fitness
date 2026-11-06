import axiosInstance from './axiosInstance'

// 백엔드 응답(주간 루틴, 운동 기록)을 화면에서 쓰기 쉬운 형태로 바꿔 주는 모듈

export const DAY_LABELS = ['월', '화', '수', '목', '금', '토', '일']
const DAY_NAMES = ['월요일', '화요일', '수요일', '목요일', '금요일', '토요일', '일요일']

const TYPE_LABEL = { ANAEROBIC: '근력', AEROBIC: '유산소', STRETCHING: '스트레칭' }
const MUSCLE_LABEL = { CHEST: '가슴', BACK: '등', LEGS: '하체', SHOULDERS: '어깨', ARMS: '팔', NONE: '전신' }

const pad = (n) => String(n).padStart(2, '0')

// toISOString()은 UTC 기준이라 한국 시간 새벽에 날짜가 하루 밀릴 수 있어서 로컬 날짜로 만든다
export const toDateStr = (d) => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`

// 월요일 = 0 ... 일요일 = 6
export const dayIndexOf = (d) => (d.getDay() + 6) % 7
export const todayIndex = () => dayIndexOf(new Date())

export function startOfWeek(date = new Date()) {
  const d = new Date(date.getFullYear(), date.getMonth(), date.getDate())
  d.setDate(d.getDate() - dayIndexOf(d))
  return d
}

// ===== 루틴 =====

function normalizeExercise(item) {
  const ex = item.exercise ?? {}
  return {
    exerciseId: ex.exerciseID,
    name: ex.name,
    type: ex.exerciseType,
    typeLabel: TYPE_LABEL[ex.exerciseType] ?? '',
    muscle: ex.targetMuscleGroup,
    muscleLabel: MUSCLE_LABEL[ex.targetMuscleGroup] ?? '',
    level: ex.difficultyLevel,
    sets: item.sets,
    reps: item.reps,
  }
}

function dayFocus(exercises) {
  const main = exercises.find((e) => e.type !== 'STRETCHING') ?? exercises[0]
  if (!main) return ''
  if (main.type === 'AEROBIC') return '유산소'
  return main.muscleLabel || main.typeLabel
}

export function normalizePlan(plan) {
  const days = DAY_NAMES.map((dayName) => ({ dayName, restDay: true, exercises: [], focus: '' }))
  ;(plan?.weeklyRoutines ?? []).forEach((d) => {
    const idx = DAY_NAMES.indexOf(d.dayName)
    if (idx < 0) return
    const exercises = (d.dailyExercises ?? []).map(normalizeExercise)
    days[idx] = { dayName: d.dayName, restDay: d.restDay || exercises.length === 0, exercises, focus: dayFocus(exercises) }
  })
  return {
    days,
    plannedDays: days.filter((d) => !d.restDay).length,
  }
}

export async function fetchPlan(userId) {
  const { data } = await axiosInstance.get('/routines/recommend', { params: { userId } })
  return normalizePlan(data)
}

// ===== 운동 기록 =====

export async function fetchLogs(userId, period = 'weekly') {
  const { data } = await axiosInstance.get(`/workout-logs/${period}/${userId}`)
  return (data?.logs ?? []).map((l) => ({
    logId: l.logID,
    exerciseId: l.exerciseID,
    type: l.exerciseType,
    date: l.workoutDate,
    sets: l.sets,
    reps: l.reps,
  }))
}

export async function saveLog(userId, exercise, date = toDateStr(new Date())) {
  const { data } = await axiosInstance.post('/workout-logs', {
    userID: userId,
    exerciseID: exercise.exerciseId,
    workoutDate: date,
    sets: exercise.sets,
    reps: exercise.reps,
    exerciseWeight: 0,
  })
  return data.logID
}

export async function deleteLog(logId) {
  await axiosInstance.delete(`/workout-logs/${logId}`)
}

// ===== 대시보드 계산 =====

export function weeklyStats(logs, plannedDays) {
  const monday = toDateStr(startOfWeek())
  const thisWeek = logs.filter((l) => l.date >= monday)
  const counts = [0, 0, 0, 0, 0, 0, 0]
  thisWeek.forEach((l) => {
    const [y, m, d] = l.date.split('-').map(Number)
    counts[dayIndexOf(new Date(y, m - 1, d))] += 1
  })
  const workoutDays = counts.filter((c) => c > 0).length
  const byType = { ANAEROBIC: 0, AEROBIC: 0, STRETCHING: 0 }
  thisWeek.forEach((l) => { if (byType[l.type] !== undefined) byType[l.type] += 1 })
  const goal = plannedDays || 3
  return {
    counts,
    workoutDays,
    exerciseCount: thisWeek.length,
    goal,
    rate: Math.min(100, Math.round((workoutDays / goal) * 100)),
    byType,
  }
}

export function monthlyStats(logs, plannedDays) {
  const now = new Date()
  const prefix = `${now.getFullYear()}-${pad(now.getMonth() + 1)}`
  const dates = new Set(logs.filter((l) => l.date.startsWith(prefix)).map((l) => l.date))
  const daysInMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0).getDate()
  const target = Math.max(1, Math.round(((plannedDays || 3) * daysInMonth) / 7))
  return {
    completedDates: dates,
    completedDays: dates.size,
    target,
    rate: Math.min(100, Math.round((dates.size / target) * 100)),
  }
}

export const TYPE_LABELS = TYPE_LABEL

export function apiErrorMessage(err, fallback) {
  const data = err?.response?.data
  return data?.error?.message || data?.message || fallback
}
