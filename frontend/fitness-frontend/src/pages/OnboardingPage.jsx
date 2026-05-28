import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import axiosInstance from '../api/axiosInstance'

// 전체 스텝: 1.기본정보 → 2.운동목표 → 3.주간목표 → 4.인바디유무 → 5.인바디입력 or BMI안내
const TOTAL_STEPS = 5

export default function OnboardingPage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [step, setStep] = useState(1)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const [form, setForm] = useState({
    height: '',
    weight: '',
    age: '',
    goal: '',           // diet | muscle | fitness
    weeklyGoal: '',     // 2 | 3 | 5
    hasInbody: null,    // true | false
    bodyFat: '',
    muscleMass: '',
  })

  const update = (field, value) => setForm(prev => ({ ...prev, [field]: value }))

  const handleNext = () => {
    setError('')
    // 스텝 4에서 인바디 없으면 5를 건너뛰고 제출
    if (step === 4 && form.hasInbody === false) {
      handleSubmit()
      return
    }
    setStep(s => s + 1)
  }

  const handleSubmit = async () => {
    setLoading(true)
    setError('')
    try {
      const payload = {
    height: parseFloat(form.height),
    weight: parseFloat(form.weight),
    weeklyGoalCount: parseInt(form.weeklyGoal),  // weeklyGoal → weeklyGoalCount
    goal: form.goal,
    bodyFatRate: form.hasInbody ? parseFloat(form.bodyFat) : null,  // bodyFat → bodyFatRate
    muscleMass: form.hasInbody ? parseFloat(form.muscleMass) : null,
    }
      await axiosInstance.post('/body-info', payload)
      navigate('/home')
    } catch (err) {
      setError(err.response?.data?.message || '정보 저장에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  const progress = (step / TOTAL_STEPS) * 100

  return (
    <div style={{
      minHeight: '100vh',
      background: '#F8FAFC',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '20px',
    }}>
      <div style={{ width: '100%', maxWidth: '460px' }}>
        {/* 프로그레스 바 */}
        <div style={{ marginBottom: '24px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
            <span style={{ fontSize: '13px', color: '#64748B', fontWeight: 600 }}>
              {step} / {TOTAL_STEPS}단계
            </span>
            <span style={{ fontSize: '13px', color: '#4F7EF7', fontWeight: 600 }}>
              {Math.round(progress)}%
            </span>
          </div>
          <div style={{ height: '6px', background: '#E2E8F0', borderRadius: '99px', overflow: 'hidden' }}>
            <div style={{
              height: '100%',
              width: `${progress}%`,
              background: '#4F7EF7',
              borderRadius: '99px',
              transition: 'width 0.4s ease',
            }} />
          </div>
        </div>

        <div className="card">
          {step === 1 && <StepBasicInfo form={form} update={update} />}
          {step === 2 && <StepGoal form={form} update={update} />}
          {step === 3 && <StepWeeklyGoal form={form} update={update} />}
          {step === 4 && <StepInbodyCheck form={form} update={update} />}
          {step === 5 && form.hasInbody && <StepInbodyInput form={form} update={update} />}

          {error && <p className="error-msg" style={{ marginTop: '12px' }}>{error}</p>}

          <div style={{ display: 'flex', gap: '12px', marginTop: '24px' }}>
            {step > 1 && (
              <button className="btn-ghost" onClick={() => setStep(s => s - 1)}>
                ← 이전
              </button>
            )}
            <button
              className="btn-primary"
              onClick={step === 5 || (step === 4 && form.hasInbody === false) ? handleSubmit : handleNext}
              disabled={loading || !isStepValid(step, form)}
            >
              {loading ? '저장 중...' : (step === TOTAL_STEPS || (step === 4 && form.hasInbody === false)) ? '시작하기 🎉' : '다음 →'}
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

function isStepValid(step, form) {
  if (step === 1) return form.height && form.weight && form.age
  if (step === 2) return form.goal
  if (step === 3) return form.weeklyGoal
  if (step === 4) return form.hasInbody !== null
  if (step === 5) return form.bodyFat && form.muscleMass
  return true
}

// ─── 스텝 1: 기본 신체 정보 ───
function StepBasicInfo({ form, update }) {
  return (
    <div>
      <h2 style={{ fontWeight: 700, fontSize: '20px', marginBottom: '6px' }}>기본 정보를 알려주세요</h2>
      <p style={{ color: '#64748B', fontSize: '14px', marginBottom: '24px' }}>
        운동 루틴 추천을 위해 필요해요.
      </p>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        <div>
          <label className="label">나이</label>
          <input className="input" type="number" placeholder="예: 22" value={form.age}
            onChange={e => update('age', e.target.value)} min="10" max="100" />
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
          <div>
            <label className="label">키 (cm)</label>
            <input className="input" type="number" placeholder="예: 170" value={form.height}
              onChange={e => update('height', e.target.value)} min="100" max="250" />
          </div>
          <div>
            <label className="label">체중 (kg)</label>
            <input className="input" type="number" placeholder="예: 65" value={form.weight}
              onChange={e => update('weight', e.target.value)} min="20" max="300" />
          </div>
        </div>
        {form.height && form.weight && (
          <div style={{
            background: '#EEF2FF',
            borderRadius: '10px',
            padding: '12px 16px',
            fontSize: '14px',
            color: '#4F7EF7',
            fontWeight: 600,
          }}>
            BMI: {(form.weight / ((form.height / 100) ** 2)).toFixed(1)}
            <span style={{ fontWeight: 400, color: '#6366F1', marginLeft: '8px' }}>
              {getBmiLabel(form.weight / ((form.height / 100) ** 2))}
            </span>
          </div>
        )}
      </div>
    </div>
  )
}

function getBmiLabel(bmi) {
  if (bmi < 18.5) return '저체중'
  if (bmi < 23) return '정상'
  if (bmi < 25) return '과체중'
  return '비만'
}

// ─── 스텝 2: 운동 목표 ───
function StepGoal({ form, update }) {
const goals = [
  { value: 'DIET', icon: '🔥', label: '체중 감량/다이어트', desc: '유산소 위주 루틴 추천' },
  { value: 'MUSCLE', icon: '💪', label: '근력 향상', desc: '근력 운동 위주 루틴 추천' },
  { value: 'MAINTAIN', icon: '🏃', label: '기초체력 향상', desc: '전신 균형 루틴 추천' },
]
  return (
    <div>
      <h2 style={{ fontWeight: 700, fontSize: '20px', marginBottom: '6px' }}>운동 목표가 무엇인가요?</h2>
      <p style={{ color: '#64748B', fontSize: '14px', marginBottom: '24px' }}>
        목표에 맞는 운동 루틴을 추천해드려요.
      </p>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        {goals.map(g => (
          <button
            key={g.value}
            className={`option-btn ${form.goal === g.value ? 'selected' : ''}`}
            onClick={() => update('goal', g.value)}
            type="button"
          >
            <span className="option-icon">{g.icon}</span>
            <div>
              <div style={{ fontWeight: 600, fontSize: '15px' }}>{g.label}</div>
              <div style={{ fontSize: '12px', color: '#94A3B8', marginTop: '2px' }}>{g.desc}</div>
            </div>
          </button>
        ))}
      </div>
    </div>
  )
}

// ─── 스텝 3: 주간 목표 횟수 ───
function StepWeeklyGoal({ form, update }) {
  const options = [
    { value: '2', label: '주 2회', desc: '가볍게 시작하기' },
    { value: '3', label: '주 3회', desc: '꾸준한 루틴 만들기' },
    { value: '5', label: '주 5회', desc: '적극적으로 운동하기' },
  ]
  return (
    <div>
      <h2 style={{ fontWeight: 700, fontSize: '20px', marginBottom: '6px' }}>일주일에 몇 번 운동할까요?</h2>
      <p style={{ color: '#64748B', fontSize: '14px', marginBottom: '24px' }}>
        목표 횟수를 기준으로 달성률을 계산해드려요.
      </p>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        {options.map(o => (
          <button
            key={o.value}
            className={`option-btn ${form.weeklyGoal === o.value ? 'selected' : ''}`}
            onClick={() => update('weeklyGoal', o.value)}
            type="button"
          >
            <span className="option-icon">📅</span>
            <div>
              <div style={{ fontWeight: 600, fontSize: '15px' }}>{o.label}</div>
              <div style={{ fontSize: '12px', color: '#94A3B8', marginTop: '2px' }}>{o.desc}</div>
            </div>
          </button>
        ))}
      </div>
    </div>
  )
}

// ─── 스텝 4: 인바디 유무 확인 ───
function StepInbodyCheck({ form, update }) {
  return (
    <div>
      <h2 style={{ fontWeight: 700, fontSize: '20px', marginBottom: '6px' }}>혹시 인바디 정보가 있으신가요?</h2>
      <p style={{ color: '#64748B', fontSize: '14px', marginBottom: '24px' }}>
        인바디 정보가 있으면 더 개인화된 루틴을 제공해드려요.<br />
        없어도 괜찮아요! BMI 기반으로 추천해드립니다.
      </p>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        <button
          className={`option-btn ${form.hasInbody === true ? 'selected' : ''}`}
          onClick={() => update('hasInbody', true)}
          type="button"
        >
          <span className="option-icon">📋</span>
          <div>
            <div style={{ fontWeight: 600 }}>있어요 — 인바디 정보 직접 입력할게요</div>
            <div style={{ fontSize: '12px', color: '#94A3B8', marginTop: '2px' }}>더 정확한 맞춤 루틴 추천</div>
          </div>
        </button>
        <button
          className={`option-btn ${form.hasInbody === false ? 'selected' : ''}`}
          onClick={() => update('hasInbody', false)}
          type="button"
        >
          <span className="option-icon">🏃</span>
          <div>
            <div style={{ fontWeight: 600 }}>없어요 — BMI 기반으로 추천받을게요</div>
            <div style={{ fontSize: '12px', color: '#94A3B8', marginTop: '2px' }}>초보자용 전신 균형 루틴 추천</div>
          </div>
        </button>
      </div>
    </div>
  )
}

// ─── 스텝 5: 인바디 수치 입력 ───
function StepInbodyInput({ form, update }) {
  return (
    <div>
      <h2 style={{ fontWeight: 700, fontSize: '20px', marginBottom: '6px' }}>인바디 정보를 입력해주세요</h2>
      <p style={{ color: '#64748B', fontSize: '14px', marginBottom: '24px' }}>
        인바디 결과지에서 확인할 수 있어요.
      </p>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        <div>
          <label className="label">체지방률 (%)</label>
          <input className="input" type="number" placeholder="예: 22.5" value={form.bodyFat}
            onChange={e => update('bodyFat', e.target.value)} min="1" max="70" step="0.1" />
        </div>
        <div>
          <label className="label">골격근량 (kg)</label>
          <input className="input" type="number" placeholder="예: 28.3" value={form.muscleMass}
            onChange={e => update('muscleMass', e.target.value)} min="1" max="100" step="0.1" />
        </div>
      </div>
    </div>
  )
}
