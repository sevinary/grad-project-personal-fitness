import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import Navbar from '../components/Navbar'
import axiosInstance from '../api/axiosInstance'

export default function HomePage() {
  const { user } = useAuth()
  const [weeklyData, setWeeklyData] = useState(null)
  const [routine, setRoutine] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [weeklyRes, routineRes] = await Promise.all([
          axiosInstance.get('/dashboard/weekly'),
          axiosInstance.get('/routines/recommend'),
        ])
        setWeeklyData(weeklyRes.data)
        setRoutine(routineRes.data)
      } catch (err) {
        console.error(err)
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [])

  const today = new Date().toLocaleDateString('ko-KR', { month: 'long', day: 'numeric', weekday: 'short' })
  const days = ['월', '화', '수', '목', '금', '토', '일']
  const todayIdx = (new Date().getDay() + 6) % 7

  return (
    <div style={{ minHeight: '100vh', background: '#F8FAFC' }}>
      <Navbar />
      <div className="page-container-wide" style={{ paddingTop: '32px' }}>
        {/* 인사 헤더 */}
        <div style={{ marginBottom: '28px' }}>
          <p style={{ fontSize: '14px', color: '#64748B', marginBottom: '4px' }}>{today}</p>
          <h1 style={{ fontSize: '26px', fontWeight: 800, color: '#1E293B' }}>
            안녕하세요, {user?.username || '회원'}님! 👋
          </h1>
          <p style={{ color: '#64748B', marginTop: '4px', fontSize: '15px' }}>오늘도 운동해볼까요?</p>
        </div>

        {loading ? (
          <div className="spinner" />
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
            {/* 주간 현황 */}
            <div className="card">
              <h2 style={{ fontWeight: 700, fontSize: '16px', marginBottom: '16px', color: '#1E293B' }}>
                📅 이번 주 운동 현황
              </h2>
              <div style={{ display: 'flex', gap: '8px', marginBottom: '16px' }}>
                {days.map((d, i) => {
                  const completed = weeklyData?.days?.[i] ?? false
                  const isToday = i === todayIdx
                  return (
                    <div key={d} style={{ flex: 1, textAlign: 'center' }}>
                      <div style={{
                        fontSize: '11px',
                        color: isToday ? '#4F7EF7' : '#94A3B8',
                        fontWeight: isToday ? 700 : 400,
                        marginBottom: '6px',
                      }}>{d}</div>
                      <div style={{
                        width: '32px',
                        height: '32px',
                        borderRadius: '8px',
                        margin: '0 auto',
                        background: completed ? '#10B981' : (isToday ? '#EEF2FF' : '#F1F5F9'),
                        border: isToday ? '2px solid #4F7EF7' : 'none',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontSize: '14px',
                      }}>
                        {completed ? '✓' : ''}
                      </div>
                    </div>
                  )
                })}
              </div>
              <div style={{ background: '#F8FAFC', borderRadius: '10px', padding: '12px', display: 'flex', justifyContent: 'space-between' }}>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '22px', fontWeight: 800, color: '#4F7EF7' }}>
                    {weeklyData?.completedCount ?? 0}
                  </div>
                  <div style={{ fontSize: '12px', color: '#64748B' }}>완료한 운동</div>
                </div>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '22px', fontWeight: 800, color: '#1E293B' }}>
                    {weeklyData?.weeklyGoal ?? 3}
                  </div>
                  <div style={{ fontSize: '12px', color: '#64748B' }}>목표 횟수</div>
                </div>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '22px', fontWeight: 800, color: '#10B981' }}>
                    {weeklyData?.achievementRate ?? 0}%
                  </div>
                  <div style={{ fontSize: '12px', color: '#64748B' }}>달성률</div>
                </div>
              </div>
            </div>

            {/* 오늘의 루틴 요약 */}
            <div className="card">
              <h2 style={{ fontWeight: 700, fontSize: '16px', marginBottom: '4px', color: '#1E293B' }}>
                💪 오늘의 추천 루틴
              </h2>
              <p style={{ fontSize: '13px', color: '#64748B', marginBottom: '16px' }}>
                {routine?.routineName ?? '맞춤 루틴'}
              </p>
              {routine?.exercises?.slice(0, 3).map((ex, i) => (
                <div key={i} style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  padding: '10px 0',
                  borderBottom: i < 2 ? '1px solid #F1F5F9' : 'none',
                  fontSize: '14px',
                }}>
                  <span style={{ fontWeight: 500 }}>{ex.name}</span>
                  <span style={{ color: '#64748B' }}>{ex.sets}세트 × {ex.reps}회</span>
                </div>
              ))}
              {(routine?.exercises?.length ?? 0) > 3 && (
                <p style={{ fontSize: '13px', color: '#94A3B8', marginTop: '8px' }}>
                  외 {routine.exercises.length - 3}개 운동
                </p>
              )}
              <Link to="/routine" style={{
                display: 'block',
                textAlign: 'center',
                marginTop: '16px',
                background: '#4F7EF7',
                color: 'white',
                textDecoration: 'none',
                padding: '11px',
                borderRadius: '10px',
                fontWeight: 600,
                fontSize: '14px',
              }}>
                운동 시작하기 →
              </Link>
            </div>

            {/* 바로가기 버튼들 */}
            <Link to="/routine" style={{ textDecoration: 'none' }}>
              <div className="card" style={{
                display: 'flex',
                alignItems: 'center',
                gap: '16px',
                cursor: 'pointer',
                transition: 'box-shadow 0.2s',
              }}>
                <div style={{ fontSize: '32px' }}>🏋️</div>
                <div>
                  <div style={{ fontWeight: 700, fontSize: '15px', color: '#1E293B' }}>운동 루틴 보기</div>
                  <div style={{ fontSize: '13px', color: '#64748B', marginTop: '2px' }}>오늘의 운동 목록 확인 및 기록</div>
                </div>
              </div>
            </Link>

            <Link to="/dashboard" style={{ textDecoration: 'none' }}>
              <div className="card" style={{
                display: 'flex',
                alignItems: 'center',
                gap: '16px',
                cursor: 'pointer',
              }}>
                <div style={{ fontSize: '32px' }}>📊</div>
                <div>
                  <div style={{ fontWeight: 700, fontSize: '15px', color: '#1E293B' }}>대시보드</div>
                  <div style={{ fontSize: '13px', color: '#64748B', marginTop: '2px' }}>주간·월간 운동 달성 현황</div>
                </div>
              </div>
            </Link>
          </div>
        )}
      </div>
    </div>
  )
}
