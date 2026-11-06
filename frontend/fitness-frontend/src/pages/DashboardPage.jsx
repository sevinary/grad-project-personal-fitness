import { useState, useEffect } from 'react'
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Tooltip } from 'chart.js'
import { Bar } from 'react-chartjs-2'
import Navbar from '../components/Navbar'
import { useAuth } from '../context/AuthContext'
import {
  DAY_LABELS, TYPE_LABELS, fetchPlan, fetchLogs, weeklyStats, monthlyStats, todayIndex, toDateStr, apiErrorMessage,
} from '../api/fitory'

ChartJS.register(CategoryScale, LinearScale, BarElement, Tooltip)

export default function DashboardPage() {
  const { user } = useAuth()
  const [tab, setTab] = useState('weekly')
  const [weekly, setWeekly] = useState(null)
  const [monthly, setMonthly] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!user?.userId) return
    const load = async () => {
      try {
        const [plan, wLogs, mLogs] = await Promise.all([
          fetchPlan(user.userId).catch(() => null),
          fetchLogs(user.userId, 'weekly'),
          fetchLogs(user.userId, 'monthly'),
        ])
        const planned = plan?.plannedDays ?? 3
        setWeekly(weeklyStats(wLogs, planned))
        setMonthly(monthlyStats(mLogs, planned))
      } catch (err) {
        setError(apiErrorMessage(err, '운동 기록을 불러오지 못했어요.'))
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [user?.userId])

  return (
    <div className="page">
      <Navbar />
      <div className="page-body">
        <p className="page-eyebrow">운동 기록</p>
        <h1 className="page-title">대시보드</h1>
        <p className="page-sub">꾸준히 쌓인 기록을 한눈에 확인해 보세요.</p>

        <div className="tab-group" style={{ margin: '24px 0 20px' }}>
          <button className={`tab ${tab === 'weekly' ? 'active' : ''}`} onClick={() => setTab('weekly')}>주간</button>
          <button className={`tab ${tab === 'monthly' ? 'active' : ''}`} onClick={() => setTab('monthly')}>월간</button>
        </div>

        {loading ? (
          <div className="spinner" />
        ) : error ? (
          <p className="error-msg">{error}</p>
        ) : tab === 'weekly' ? (
          <WeeklyView data={weekly} />
        ) : (
          <MonthlyView data={monthly} />
        )}
      </div>
    </div>
  )
}

function StatCard({ label, value, unit, highlight }) {
  return (
    <div className="card" style={{ padding: '20px' }}>
      <div className="stat-label" style={{ marginTop: 0, marginBottom: '8px' }}>{label}</div>
      <div className="stat-value" style={{ color: highlight ? 'var(--primary)' : 'var(--text)' }}>
        {value}<span style={{ fontSize: '15px', fontWeight: 600, marginLeft: '2px' }}>{unit}</span>
      </div>
    </div>
  )
}

function WeeklyView({ data }) {
  const today = todayIndex()
  const chartData = {
    labels: DAY_LABELS,
    datasets: [{
      data: data.counts,
      backgroundColor: data.counts.map((c, i) => (i === today ? '#2E6B7C' : c > 0 ? '#7FA9B5' : '#E2E7EA')),
      borderRadius: 6,
      borderSkipped: false,
      maxBarThickness: 36,
    }],
  }
  const chartOptions = {
    responsive: true,
    aspectRatio: 2.8,
    plugins: {
      legend: { display: false },
      tooltip: { callbacks: { label: (ctx) => `완료한 운동 ${ctx.parsed.y}개` } },
    },
    scales: {
      y: { beginAtZero: true, ticks: { stepSize: 1, color: '#9AA5AA' }, grid: { color: '#EEF1F3' }, border: { display: false } },
      x: { grid: { display: false }, ticks: { color: '#5F6B70', font: { weight: 600 } }, border: { display: false } },
    },
  }
  const totalByType = Object.values(data.byType).reduce((a, b) => a + b, 0)

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      <div className="grid-3">
        <StatCard label="이번 주 운동한 날" value={data.workoutDays} unit="일" />
        <StatCard label="완료한 운동" value={data.exerciseCount} unit="개" />
        <StatCard label={`목표 달성률 (주 ${data.goal}일)`} value={data.rate} unit="%" highlight />
      </div>

      <div className="card">
        <h3 className="section-title">요일별 완료한 운동</h3>
        <Bar data={chartData} options={chartOptions} />
      </div>

      <div className="card">
        <h3 className="section-title">운동 유형 비율</h3>
        {totalByType === 0 ? (
          <p className="muted" style={{ fontSize: '14px' }}>이번 주 기록이 아직 없어요.</p>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {Object.entries(data.byType).map(([type, count]) => {
              const pct = Math.round((count / totalByType) * 100)
              return (
                <div key={type}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px', marginBottom: '6px' }}>
                    <span style={{ fontWeight: 600 }}>{TYPE_LABELS[type]}</span>
                    <span className="muted">{count}개 · {pct}%</span>
                  </div>
                  <div className="progress-track"><div className="progress-fill" style={{ width: `${pct}%` }} /></div>
                </div>
              )
            })}
          </div>
        )}
      </div>
    </div>
  )
}

function MonthlyView({ data }) {
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth()
  const firstDay = (new Date(year, month, 1).getDay() + 6) % 7
  const daysInMonth = new Date(year, month + 1, 0).getDate()
  const todayStr = toDateStr(now)

  const cells = []
  for (let i = 0; i < firstDay; i++) cells.push(null)
  for (let d = 1; d <= daysInMonth; d++) {
    const dateStr = toDateStr(new Date(year, month, d))
    cells.push({ day: d, dateStr, done: data.completedDates.has(dateStr), isToday: dateStr === todayStr })
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      <div className="grid-3">
        <StatCard label="이번 달 운동한 날" value={data.completedDays} unit="일" />
        <StatCard label="이번 달 목표" value={data.target} unit="일" />
        <StatCard label="목표 달성률" value={data.rate} unit="%" highlight />
      </div>

      <div className="card">
        <h3 className="section-title">{year}년 {month + 1}월 운동 기록</h3>
        <div style={{ maxWidth: '520px', margin: '0 auto' }}>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: '6px', marginBottom: '6px' }}>
          {DAY_LABELS.map((d) => (
            <div key={d} style={{ textAlign: 'center', fontSize: '12px', fontWeight: 600, color: 'var(--text-light)' }}>{d}</div>
          ))}
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: '6px' }}>
          {cells.map((cell, i) => (
            <div key={i} style={{
              aspectRatio: '1',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              borderRadius: '10px',
              fontSize: '13px',
              fontWeight: cell?.done || cell?.isToday ? 700 : 400,
              background: cell?.done ? 'var(--primary)' : cell ? '#F4F6F7' : 'transparent',
              color: cell?.done ? 'white' : 'var(--text)',
              border: cell?.isToday && !cell.done ? '1.5px solid var(--primary)' : 'none',
            }}>
              {cell?.day ?? ''}
            </div>
          ))}
        </div>
        </div>
        <div style={{ display: 'flex', gap: '16px', marginTop: '16px', fontSize: '12px', color: 'var(--text-secondary)' }}>
          <Legend color="var(--primary)" label="운동한 날" />
          <Legend color="#F4F6F7" label="기록 없음" border />
        </div>
      </div>
    </div>
  )
}

function Legend({ color, label, border }) {
  return (
    <span style={{ display: 'inline-flex', alignItems: 'center', gap: '6px' }}>
      <span style={{ width: '12px', height: '12px', borderRadius: '4px', background: color, border: border ? '1px solid var(--border)' : 'none' }} />
      {label}
    </span>
  )
}
