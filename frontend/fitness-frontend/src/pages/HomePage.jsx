import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import Navbar from '../components/Navbar'
import { DAY_LABELS, fetchPlan, fetchLogs, weeklyStats, todayIndex } from '../api/fitory'

export default function HomePage() {
  const { user } = useAuth()
  const [plan, setPlan] = useState(null)
  const [week, setWeek] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!user?.userId) return
    const load = async () => {
      const [p, logs] = await Promise.all([
        fetchPlan(user.userId).catch(() => null),
        fetchLogs(user.userId, 'weekly').catch(() => []),
      ])
      setPlan(p)
      setWeek(weeklyStats(logs, p?.plannedDays ?? 3))
      setLoading(false)
    }
    load()
  }, [user?.userId])

  const today = todayIndex()
  const dateLabel = new Date().toLocaleDateString('ko-KR', { month: 'long', day: 'numeric', weekday: 'long' })
  const todayPlan = plan?.days[today]

  return (
    <div className="page">
      <Navbar />
      <div className="page-body">
        <p className="page-eyebrow">{dateLabel}</p>
        <h1 className="page-title">{user?.username || '회원'}님, 오늘도 한 걸음 더</h1>
        <p className="page-sub">FITORY가 오늘의 운동을 준비해 두었어요.</p>

        {loading ? (
          <div className="spinner" />
        ) : (
          <div className="grid-2" style={{ marginTop: '28px' }}>
            {/* 오늘의 루틴 */}
            <div className="card" style={{ display: 'flex', flexDirection: 'column' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                <h2 className="section-title" style={{ marginBottom: 0 }}>오늘의 루틴</h2>
                {todayPlan && !todayPlan.restDay && <span className="chip chip-primary">{todayPlan.focus} 집중</span>}
              </div>

              {!plan ? (
                <>
                  <p className="muted" style={{ fontSize: '14px', marginBottom: '16px' }}>
                    아직 루틴이 없어요. 신체 정보를 입력하면 맞춤 루틴을 만들어 드려요.
                  </p>
                  <Link to="/onboarding" style={{ marginTop: 'auto' }}><button className="btn-primary">정보 입력하기</button></Link>
                </>
              ) : todayPlan.restDay ? (
                <p className="muted" style={{ fontSize: '14px' }}>오늘은 휴식일이에요. 가벼운 스트레칭으로 몸을 풀어 주세요.</p>
              ) : (
                <>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', marginBottom: '16px' }}>
                    {todayPlan.exercises.map((ex, i) => (
                      <div key={i} style={{
                        display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                        padding: '12px 14px', background: 'var(--bg)', borderRadius: '10px',
                      }}>
                        <span style={{ fontSize: '14px', fontWeight: 600 }}>{ex.name}</span>
                        <span className="muted" style={{ fontSize: '13px' }}>{ex.sets}세트 × {ex.reps}회</span>
                      </div>
                    ))}
                  </div>
                  <Link to="/routine" style={{ marginTop: 'auto' }}><button className="btn-primary">운동 시작하기</button></Link>
                </>
              )}
            </div>

            {/* 이번 주 현황 */}
            <div className="card">
              <h2 className="section-title">이번 주 운동 현황</h2>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: '6px', marginBottom: '20px' }}>
                {DAY_LABELS.map((d, i) => {
                  const done = week.counts[i] > 0
                  const isToday = i === today
                  const rest = plan?.days[i]?.restDay
                  return (
                    <div key={d} style={{ textAlign: 'center' }}>
                      <div style={{
                        fontSize: '12px', marginBottom: '6px',
                        color: isToday ? 'var(--primary)' : 'var(--text-light)', fontWeight: isToday ? 700 : 500,
                      }}>{d}</div>
                      <div style={{
                        height: '34px', borderRadius: '9px',
                        background: done ? 'var(--primary)' : rest ? 'transparent' : 'var(--bg)',
                        border: isToday && !done ? '1.5px solid var(--primary)' : rest && !done ? '1px dashed var(--border)' : 'none',
                        color: 'white', fontSize: '14px', fontWeight: 700,
                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                      }}>{done ? '✓' : ''}</div>
                    </div>
                  )
                })}
              </div>
              <div className="grid-3" style={{ gap: '8px' }}>
                <MiniStat label="운동한 날" value={`${week.workoutDays}일`} />
                <MiniStat label="목표" value={`주 ${week.goal}일`} />
                <MiniStat label="달성률" value={`${week.rate}%`} highlight />
              </div>
              <Link to="/dashboard" style={{ display: 'block', marginTop: '16px', fontSize: '13px', fontWeight: 600, color: 'var(--primary)', textDecoration: 'none' }}>
                대시보드에서 자세히 보기 →
              </Link>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

function MiniStat({ label, value, highlight }) {
  return (
    <div style={{ background: 'var(--bg)', borderRadius: '10px', padding: '12px 8px', textAlign: 'center' }}>
      <div style={{ fontSize: '18px', fontWeight: 800, color: highlight ? 'var(--primary)' : 'var(--text)' }}>{value}</div>
      <div style={{ fontSize: '12px', color: 'var(--text-secondary)', marginTop: '2px' }}>{label}</div>
    </div>
  )
}
