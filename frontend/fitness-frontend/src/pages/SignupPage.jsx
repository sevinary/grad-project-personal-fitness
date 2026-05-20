import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function SignupPage() {
  const [form, setForm] = useState({ username: '', email: '', password: '', passwordConfirm: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { signup, login } = useAuth()
  const navigate = useNavigate()

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    if (form.password !== form.passwordConfirm) {
      setError('비밀번호가 일치하지 않습니다.')
      return
    }
    if (form.password.length < 6) {
      setError('비밀번호는 6자 이상이어야 합니다.')
      return
    }

    setLoading(true)
    try {
      await signup(form.email, form.password, form.username)
      // 가입 후 자동 로그인
      await login(form.email, form.password)
      navigate('/onboarding')
    } catch (err) {
      setError(err.response?.data?.message || '회원가입에 실패했습니다. 다시 시도해주세요.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: '#F8FAFC',
      padding: '20px',
    }}>
      <div style={{ width: '100%', maxWidth: '420px' }}>
        <div style={{ textAlign: 'center', marginBottom: '32px' }}>
          <Link to="/" style={{ textDecoration: 'none' }}>
            <span style={{ fontSize: '28px' }}>💪</span>
            <div style={{ fontWeight: 800, fontSize: '22px', color: '#4F7EF7', marginTop: '4px' }}>FitStart</div>
          </Link>
          <p style={{ color: '#64748B', marginTop: '8px', fontSize: '15px' }}>
            처음 방문하시나요? 계정을 만들어보세요.
          </p>
        </div>

        <div className="card">
          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div>
              <label className="label">이름 (닉네임)</label>
              <input
                className="input"
                name="username"
                type="text"
                placeholder="이름을 입력하세요"
                value={form.username}
                onChange={handleChange}
                required
              />
            </div>
            <div>
              <label className="label">이메일</label>
              <input
                className="input"
                name="email"
                type="email"
                placeholder="example@email.com"
                value={form.email}
                onChange={handleChange}
                required
              />
            </div>
            <div>
              <label className="label">비밀번호</label>
              <input
                className="input"
                name="password"
                type="password"
                placeholder="6자 이상 입력하세요"
                value={form.password}
                onChange={handleChange}
                required
              />
            </div>
            <div>
              <label className="label">비밀번호 확인</label>
              <input
                className="input"
                name="passwordConfirm"
                type="password"
                placeholder="비밀번호를 다시 입력하세요"
                value={form.passwordConfirm}
                onChange={handleChange}
                required
              />
            </div>

            {error && <p className="error-msg">{error}</p>}

            <button className="btn-primary" type="submit" disabled={loading}>
              {loading ? '가입 중...' : '회원가입'}
            </button>
          </form>

          <p style={{ textAlign: 'center', marginTop: '20px', fontSize: '14px', color: '#64748B' }}>
            이미 계정이 있으신가요?{' '}
            <Link to="/login" style={{ color: '#4F7EF7', fontWeight: 600, textDecoration: 'none' }}>
              로그인
            </Link>
          </p>
        </div>
      </div>
    </div>
  )
}
