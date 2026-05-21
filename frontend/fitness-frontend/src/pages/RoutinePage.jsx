import { useState, useEffect } from 'react'
import Navbar from '../components/Navbar'
import axiosInstance from '../api/axiosInstance'

export default function RoutinePage() {
  const [routine, setRoutine] = useState(null)
  const [workoutLogs, setWorkoutLogs] = useState({}) // { exerciseId: logId }
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState({})

  useEffect(() => {
    const fetchRoutine = async () => {
      try {
        const [routineRes, logsRes] = await Promise.all([
          axiosInstance.get('/routines/recommend'),
          axiosInstance.get('/workout-logs'),
        ])
        setRoutine(routineRes.data)

        // 오늘 날짜의 기록만 체크 상태로 표시
        const today = new Date().toISOString().split('T')[0]
        const todayLogs = {}
        logsRes.data?.forEach(log => {
          if (log.date === today && log.completed) {
            todayLogs[log.exerciseId] = log.logId
          }
        })
        setWorkoutLogs(todayLogs)
      } catch (err) {
        console.error(err)
      } finally {
        setLoading(false)
      }
    }
    fetchRoutine()
  }, [])

  const handleCheck = async (exerciseId) => {
    const isCompleted = !!workoutLogs[exerciseId]
    setSaving(prev => ({ ...prev, [exerciseId]: true }))
    try {
      if (isCompleted) {
        // 체크 해제 → 기록 삭제
        await axiosInstance.delete(`/workout-logs/${workoutLogs[exerciseId]}`)
        setWorkoutLogs(prev => {
          const next = { ...prev }
          delete next[exerciseId]
          return next
        })
      } else {
        // 체크 → 기록 저장
        const res = await axiosInstance.post('/workout-logs', {
          exerciseId,
          date: new Date().toISOString().split('T')[0],
          completed: true,
        })
        setWorkoutLogs(prev => ({ ...prev, [exerciseId]: res.data.logId }))
      }
    } catch (err) {
      alert('저장에 실패했습니다. 다시 시도해주세요.')
    } finally {
      setSaving(prev => ({ ...prev, [exerciseId]: false }))
    }
  }

  const completedCount = Object.keys(workoutLogs).length
  const totalCount = routine?.exercises?.length ?? 0
  const progressPct = totalCount > 0 ? Math.round((completedCount / totalCount) * 100) : 0

  return (
    <div style={{ minHeight: '100vh', background: '#F8FAFC' }}>
      <Navbar />
      <div style={{ maxWidth: '640px', margin: '0 auto', padding: '32px 16px' }}>
        <h1 style={{ fontWeight: 800, fontSize: '24px', marginBottom: '4px' }}>오늘의 루틴</h1>
        <p style={{ color: '#64748B', fontSize: '14px', marginBottom: '24px' }}>
          {routine?.routineName ?? '맞춤 운동 루틴'}
        </p>

        {loading ? (
          <div className="spinner" />
        ) : (
          <>
            {/* 진행률 카드 */}
            <div className="card" style={{ marginBottom: '20px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                <span style={{ fontWeight: 600, fontSize: '15px' }}>오늘의 진행률</span>
                <span style={{ fontWeight: 700, fontSize: '20px', color: progressPct === 100 ? '#10B981' : '#4F7EF7' }}>
                  {completedCount}/{totalCount}
                </span>
              </div>
              <div style={{ height: '8px', background: '#E2E8F0', borderRadius: '99px', overflow: 'hidden' }}>
                <div style={{
                  height: '100%',
                  width: `${progressPct}%`,
                  background: progressPct === 100 ? '#10B981' : '#4F7EF7',
                  borderRadius: '99px',
                  transition: 'width 0.4s ease',
                }} />
              </div>
              {progressPct === 100 && (
                <p style={{ color: '#10B981', fontWeight: 600, fontSize: '14px', marginTop: '10px', textAlign: 'center' }}>
                  🎉 오늘 운동 완료! 대단해요!
                </p>
              )}
            </div>

            {/* 운동 목록 */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              {routine?.exercises?.map((ex) => {
                const isCompleted = !!workoutLogs[ex.exerciseId]
                const isSaving = saving[ex.exerciseId]
                return (
                  <div
                    key={ex.exerciseId}
                    className="card"
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '16px',
                      opacity: isSaving ? 0.7 : 1,
                      transition: 'all 0.2s',
                      border: isCompleted ? '1.5px solid #10B981' : '1.5px solid transparent',
                      background: isCompleted ? '#F0FDF4' : 'white',
                    }}
                  >
                    {/* 체크박스 */}
                    <button
                      onClick={() => handleCheck(ex.exerciseId)}
                      disabled={isSaving}
                      style={{
                        width: '28px',
                        height: '28px',
                        borderRadius: '8px',
                        border: isCompleted ? 'none' : '2px solid #CBD5E1',
                        background: isCompleted ? '#10B981' : 'white',
                        cursor: 'pointer',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        flexShrink: 0,
                        fontSize: '14px',
                        color: 'white',
                        fontWeight: 700,
                        transition: 'all 0.2s',
                      }}
                    >
                      {isCompleted ? '✓' : ''}
                    </button>

                    {/* 운동 정보 */}
                    <div style={{ flex: 1 }}>
                      <div style={{
                        fontWeight: 600,
                        fontSize: '15px',
                        color: isCompleted ? '#059669' : '#1E293B',
                        textDecoration: isCompleted ? 'line-through' : 'none',
                      }}>
                        {ex.name}
                      </div>
                      <div style={{ fontSize: '13px', color: '#94A3B8', marginTop: '2px' }}>
                        {ex.category}
                      </div>
                    </div>

                    {/* 세트/반복 */}
                    <div style={{ textAlign: 'right' }}>
                      <div style={{ fontWeight: 700, fontSize: '16px', color: '#4F7EF7' }}>
                        {ex.sets}세트
                      </div>
                      <div style={{ fontSize: '13px', color: '#94A3B8' }}>× {ex.reps}회</div>
                    </div>
                  </div>
                )
              })}
            </div>
          </>
        )}
      </div>
    </div>
  )
}
