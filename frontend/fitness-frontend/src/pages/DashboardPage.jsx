import { useState, useEffect } from 'react'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js'
import { Bar } from 'react-chartjs-2'
import Navbar from '../components/Navbar'
import axiosInstance from '../api/axiosInstance'

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)

export default function DashboardPage() {
  const [tab, setTab] = useState('weekly') // 'weekly' | 'monthly'
  const [weeklyData, setWeeklyData] = useState(null)
  const [monthlyData, setMonthlyData] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchAll = async () => {
      try {
        const [wRes, mRes] = await Promise.all([
          axiosInstance.get('/dashboard/weekly'),
          axiosInstance.get('/dashboard/monthly'),
        ])
        setWeeklyData(wRes.data)
        setMonthlyData(mRes.data)
      } catch (err) {
        console.error(err)
      } finally {
        setLoading(false)
      }
    }
    fetchAll()
  }, [])

  return (
    <div style={{ minHeight: '100vh', background: '#F8FAFC' }}>
      <Navbar />
      <div style={{ maxWidth: '800px', margin: '0 auto', padding: '32px 16px' }}>
        <h1 style={{ fontWeight: 800, fontSize: '24px', marginBottom: '4px' }}>📊 대시보드</h1>
        <p style={{ color: '#64748B', fontSize: '14px', marginBottom: '24px' }}>운동 기록과 달성 현황을 확인하세요</p>

        {/* 탭 */}
        <div style={{
          display: 'flex',
          gap: '4px',
          background: '#E2E8F0',
          borderRadius: '10px',
          padding: '4px',
          marginBottom: '24px',
          width: 'fit-content',
        }}>
          {['weekly', 'monthly'].map(t => (
            <button
              key={t}
              onClick={() => setTab(t)}
              style={{
                padding: '8px 20px',
                borderRadius: '8px',
                border: 'none',
                cursor: 'pointer',
                fontWeight: 600,
                fontSize: '14px',
                background: tab === t ? 'white' : 'transparent',
                color: tab === t ? '#4F7EF7' : '#64748B',
                boxShadow: tab === t ? '0 1px 3px rgba(0,0,0,0.1)' : 'none',
                transition: 'all 0.2s',
              }}
            >
              {t === 'weekly' ? '주간 현황' : '월간 현황'}
            </button>
          ))}
        </div>

        {loading ? (
          <div className="spinner" />
        ) : tab === 'weekly' ? (
          <WeeklyView data={weeklyData} />
        ) : (
          <MonthlyView data={monthlyData} />
        )}
      </div>
    </div>
  )
}

function WeeklyView({ data }) {
  const days = ['월', '화', '수', '목', '금', '토', '일']
  const counts = data?.dailyCounts ?? [0, 0, 0, 0, 0, 0, 0]

  const chartData = {
    labels: days,
    datasets: [{
      label: '완료한 운동 수',
      data: counts,
      backgroundColor: counts.map(c => c > 0 ? '#4F7EF7' : '#E2E8F0'),
      borderRadius: 8,
      borderSkipped: false,
    }],
  }
  const chartOptions = {
    responsive: true,
    plugins: {
      legend: { display: false },
      title: { display: false },
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: { stepSize: 1 },
        grid: { color: '#F1F5F9' },
      },
      x: { grid: { display: false } },
    },
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      {/* 요약 통계 */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '12px' }}>
        <StatCard label="이번 주 완료" value={`${data?.completedCount ?? 0}회`} color="#4F7EF7" icon="✅" />
        <StatCard label="목표 횟수" value={`${data?.weeklyGoal ?? 3}회`} color="#64748B" icon="🎯" />
        <StatCard label="달성률" value={`${data?.achievementRate ?? 0}%`}
          color={(data?.achievementRate ?? 0) >= 100 ? '#10B981' : '#F59E0B'} icon="📈" />
      </div>

      {/* 요일별 바 차트 */}
      <div className="card">
        <h3 style={{ fontWeight: 700, fontSize: '15px', marginBottom: '20px' }}>요일별 운동 현황</h3>
        <Bar data={chartData} options={chartOptions} />
      </div>

      {/* 요일 도트 */}
      <div className="card">
        <h3 style={{ fontWeight: 700, fontSize: '15px', marginBottom: '16px' }}>운동한 날</h3>
        <div style={{ display: 'flex', gap: '8px' }}>
          {days.map((d, i) => {
            const done = (data?.days?.[i]) ?? false
            return (
              <div key={d} style={{ flex: 1, textAlign: 'center' }}>
                <div style={{
                  fontSize: '12px', color: '#94A3B8', marginBottom: '6px', fontWeight: 500,
                }}>{d}</div>
                <div style={{
                  width: '36px', height: '36px', borderRadius: '50%',
                  background: done ? '#10B981' : '#F1F5F9',
                  margin: '0 auto',
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                  fontSize: '16px',
                }}>
                  {done ? '✓' : ''}
                </div>
              </div>
            )
          })}
        </div>
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
  const completedDays = new Set(data?.completedDates ?? [])

  const cells = []
  for (let i = 0; i < firstDay; i++) cells.push(null)
  for (let d = 1; d <= daysInMonth; d++) {
    const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    cells.push({ day: d, dateStr, done: completedDays.has(dateStr) })
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '12px' }}>
        <StatCard label="이번 달 완료" value={`${data?.completedDays ?? 0}일`} color="#4F7EF7" icon="📅" />
        <StatCard label="목표 일수" value={`${data?.targetDays ?? 12}일`} color="#64748B" icon="🎯" />
        <StatCard label="달성률" value={`${data?.achievementRate ?? 0}%`}
          color={(data?.achievementRate ?? 0) >= 100 ? '#10B981' : '#F59E0B'} icon="📈" />
      </div>

      <div className="card">
        <h3 style={{ fontWeight: 700, fontSize: '15px', marginBottom: '16px' }}>
          {now.getFullYear()}년 {now.getMonth() + 1}월 기록
        </h3>
        {/* 요일 헤더 */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: '4px', marginBottom: '4px' }}>
          {['월','화','수','목','금','토','일'].map(d => (
            <div key={d} style={{ textAlign: 'center', fontSize: '12px', color: '#94A3B8', fontWeight: 600, padding: '4px 0' }}>{d}</div>
          ))}
        </div>
        {/* 날짜 그리드 */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: '4px' }}>
          {cells.map((cell, i) => (
            <div key={i} style={{
              aspectRatio: '1',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              borderRadius: '8px',
              fontSize: '13px',
              fontWeight: cell?.done ? 700 : 400,
              background: cell?.done ? '#10B981' : cell ? '#F8FAFC' : 'transparent',
              color: cell?.done ? 'white' : cell ? '#1E293B' : 'transparent',
              border: cell && !cell.done ? '1px solid #F1F5F9' : 'none',
            }}>
              {cell?.day ?? ''}
            </div>
          ))}
        </div>
        <div style={{ display: 'flex', gap: '16px', marginTop: '16px', fontSize: '12px', color: '#64748B' }}>
          <span>🟢 운동 완료</span>
          <span>⬜ 미완료</span>
        </div>
      </div>
    </div>
  )
}

function StatCard({ label, value, color, icon }) {
  return (
    <div className="card" style={{ textAlign: 'center' }}>
      <div style={{ fontSize: '24px', marginBottom: '8px' }}>{icon}</div>
      <div style={{ fontSize: '24px', fontWeight: 800, color }}>{value}</div>
      <div style={{ fontSize: '12px', color: '#64748B', marginTop: '4px' }}>{label}</div>
    </div>
  )
}
