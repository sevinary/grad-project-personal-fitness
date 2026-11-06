import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import Navbar from '../components/Navbar'
import { useAuth } from '../context/AuthContext'
import {
  DAY_LABELS, fetchPlan, fetchLogs, saveLog, deleteLog, toDateStr, todayIndex, apiErrorMessage,
} from '../api/fitory'

export default function RoutinePage() {
  const { user } = useAuth()
  const [plan, setPlan] = useState(null)
  const [selected, setSelected] = useState(todayIndex())
  const [doneLogs, setDoneLogs] = useState({}) // { exerciseId: logId } — 오늘 완료한 운동
  const [saving, setSaving] = useState({})
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [notFound, setNotFound] = useState(false)

  const today = todayIndex()
  const todayStr = toDateStr(new Date())

  useEffect(() => {
    if (!user?.userId) return
    const load = async () => {
      try {
        const [p, logs] = await Promise.all([
          fetchPlan(user.userId),
          fetchLogs(user.userId, 'weekly').catch(() => []),
        ])
        setPlan(p)
        const done = {}
        logs.filter((l) => l.date === todayStr).forEach((l) => { done[l.exerciseId] = l.logId })
        setDoneLogs(done)
      } catch (err) {
        if (err.response?.status === 404) setNotFound(true)
        else setError(apiErrorMessage(err, '루틴을 불러오지 못했어요. 잠시 후 다시 시도해 주세요.'))
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [user?.userId, todayStr])

  const handleCheck = async (exercise) => {
    const id = exercise.exerciseId
    setSaving((s) => ({ ...s, [id]: true }))
    try {
      if (doneLogs[id]) {
        await deleteLog(doneLogs[id])
        setDoneLogs((d) => { const next = { ...d }; delete next[id]; return next })
      } else {
        const logId = await saveLog(user.userId, exercise, todayStr)
        setDoneLogs((d) => ({ ...d, [id]: logId }))
      }
    } catch (err) {
      setError(apiErrorMessage(err, '기록 저장에 실패했어요. 다시 시도해 주세요.'))
    } finally {
      setSaving((s) => ({ ...s, [id]: false }))
    }
  }

  const day = plan?.days[selected]
  const isToday = selected === today
  const todayExercises = plan?.days[today]?.exercises ?? []
  const completed = todayExercises.filter((e) => doneLogs[e.exerciseId]).length
  const progress = todayExercises.length ? Math.round((completed / todayExercises.length) * 100) : 0
  const dateLabel = new Date().toLocaleDateString('ko-KR', { month: 'long', day: 'numeric', weekday: 'long' })

  return (
    <div className="page">
      <Navbar />
      <div className="page-body" style={{ maxWidth: '680px' }}>
        <p className="page-eyebrow">{dateLabel}</p>
        <h1 className="page-title">오늘의 루틴</h1>
        <p className="page-sub">나에게 맞춰 만들어진 이번 주 운동 계획이에요.</p>

        {loading ? (
          <div className="spinner" />
        ) : notFound ? (
          <div className="card empty-state" style={{ marginTop: '28px' }}>
            <h3>아직 만들어진 루틴이 없어요</h3>
            <p>신체 정보와 운동 목표를 입력하면 맞춤 루틴을 만들어 드려요.</p>
            <Link to="/onboarding"><button className="btn-primary" style={{ maxWidth: '240px' }}>정보 입력하러 가기</button></Link>
          </div>
        ) : (
          <>
            {error && <p className="error-msg" style={{ marginTop: '16px' }}>{error}</p>}

            {/* 요일 선택 */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: '8px', margin: '28px 0 20px' }}>
              {plan.days.map((d, i) => {
                const active = i === selected
                return (
                  <button
                    key={d.dayName}
                    onClick={() => setSelected(i)}
                    style={{
                      padding: '10px 0',
                      borderRadius: '12px',
                      cursor: 'pointer',
                      border: active ? '1.5px solid var(--primary)' : '1px solid var(--border)',
                      background: active ? 'var(--primary)' : 'white',
                      color: active ? 'white' : d.restDay ? 'var(--text-light)' : 'var(--text)',
                    }}
                  >
                    <div style={{ fontSize: '13px', fontWeight: 700 }}>
                      {DAY_LABELS[i]}{i === today && <span style={{ fontWeight: 500 }}> · 오늘</span>}
                    </div>
                    <div style={{ fontSize: '11px', marginTop: '3px', opacity: active ? 0.9 : 1 }}>
                      {d.restDay ? '휴식' : d.focus}
                    </div>
                  </button>
                )
              })}
            </div>

            {/* 오늘 진행률 */}
            {isToday && !day.restDay && (
              <div className="card" style={{ marginBottom: '16px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: '12px' }}>
                  <span style={{ fontWeight: 700, fontSize: '15px' }}>오늘의 진행률</span>
                  <span style={{ fontWeight: 800, fontSize: '20px', color: 'var(--primary)' }}>
                    {completed} / {todayExercises.length}
                  </span>
                </div>
                <div className="progress-track"><div className="progress-fill" style={{ width: `${progress}%` }} /></div>
                {progress === 100 && (
                  <p style={{ marginTop: '12px', fontSize: '14px', fontWeight: 600, color: 'var(--primary)' }}>
                    오늘 운동을 모두 마쳤어요. 수고했어요!
                  </p>
                )}
              </div>
            )}

            {/* 운동 목록 */}
            {day.restDay ? (
              <div className="card empty-state">
                <h3>{DAY_LABELS[selected]}요일은 휴식일이에요</h3>
                <p style={{ marginBottom: 0 }}>가벼운 스트레칭으로 몸을 풀어 주세요.</p>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                {day.exercises.map((ex, idx) => {
                  const done = isToday && !!doneLogs[ex.exerciseId]
                  return (
                    <div
                      key={`${ex.exerciseId}-${idx}`}
                      className="card"
                      style={{
                        display: 'flex', alignItems: 'center', gap: '16px', padding: '18px 20px',
                        borderColor: done ? 'var(--primary)' : 'var(--border)',
                        background: done ? 'var(--primary-light)' : 'white',
                        opacity: saving[ex.exerciseId] ? 0.6 : 1,
                      }}
                    >
                      {isToday && (
                        <button
                          onClick={() => handleCheck(ex)}
                          disabled={saving[ex.exerciseId]}
                          aria-label={done ? '완료 취소' : '완료로 표시'}
                          style={{
                            width: '28px', height: '28px', borderRadius: '8px', flexShrink: 0, cursor: 'pointer',
                            border: done ? 'none' : '1.5px solid #C5CED2',
                            background: done ? 'var(--primary)' : 'white',
                            color: 'white', fontSize: '15px', fontWeight: 700,
                          }}
                        >
                          {done ? '✓' : ''}
                        </button>
                      )}
                      <div style={{ flex: 1, minWidth: 0 }}>
                        <div style={{
                          fontWeight: 700, fontSize: '15px',
                          color: done ? 'var(--primary)' : 'var(--text)',
                          textDecoration: done ? 'line-through' : 'none',
                        }}>
                          {ex.name}
                        </div>
                        <div style={{ display: 'flex', gap: '6px', marginTop: '6px' }}>
                          <span className="chip chip-primary">{ex.typeLabel}</span>
                          {ex.muscle && ex.muscle !== 'NONE' && <span className="chip">{ex.muscleLabel}</span>}
                          <span className="chip">난이도 {ex.level}</span>
                        </div>
                      </div>
                      <div style={{ textAlign: 'right', flexShrink: 0 }}>
                        <div style={{ fontWeight: 800, fontSize: '17px' }}>{ex.sets}세트</div>
                        <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>{ex.reps}회씩</div>
                      </div>
                    </div>
                  )
                })}
                {!isToday && (
                  <p className="muted" style={{ fontSize: '13px', textAlign: 'center', marginTop: '6px' }}>
                    완료 체크는 오늘 루틴에서만 할 수 있어요.
                  </p>
                )}
              </div>
            )}
          </>
        )}
      </div>
    </div>
  )
}
