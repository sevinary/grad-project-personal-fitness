import { Link } from 'react-router-dom'

export default function LandingPage() {
  return (
    <div style={{ minHeight: '100vh', background: 'white' }}>
      {/* 헤더 */}
      <header style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: '20px 40px',
        borderBottom: '1px solid #E2E8F0',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span style={{ fontSize: '22px' }}>💪</span>
          <span style={{ fontWeight: 700, fontSize: '18px', color: '#4F7EF7' }}>FitStart</span>
        </div>
        <div style={{ display: 'flex', gap: '12px' }}>
          <Link to="/login" style={{
            padding: '8px 20px',
            borderRadius: '8px',
            border: '1.5px solid #E2E8F0',
            color: '#64748B',
            textDecoration: 'none',
            fontWeight: 500,
            fontSize: '14px',
          }}>
            로그인
          </Link>
          <Link to="/signup" style={{
            padding: '8px 20px',
            borderRadius: '8px',
            background: '#4F7EF7',
            color: 'white',
            textDecoration: 'none',
            fontWeight: 600,
            fontSize: '14px',
          }}>
            무료로 시작하기
          </Link>
        </div>
      </header>

      {/* 히어로 섹션 */}
      <section style={{
        textAlign: 'center',
        padding: '80px 20px 60px',
        maxWidth: '700px',
        margin: '0 auto',
      }}>
        <div style={{
          display: 'inline-block',
          background: '#EEF2FF',
          color: '#4F7EF7',
          padding: '6px 16px',
          borderRadius: '20px',
          fontSize: '13px',
          fontWeight: 600,
          marginBottom: '24px',
        }}>
          🏃 운동 초보자를 위한 맞춤형 플랫폼
        </div>
        <h1 style={{
          fontSize: '48px',
          fontWeight: 800,
          color: '#1E293B',
          lineHeight: 1.2,
          marginBottom: '20px',
          letterSpacing: '-1px',
        }}>
          운동 습관, 지금<br />
          <span style={{ color: '#4F7EF7' }}>시작하세요</span>
        </h1>
        <p style={{
          fontSize: '18px',
          color: '#64748B',
          lineHeight: 1.7,
          marginBottom: '40px',
        }}>
          나의 신체 정보를 입력하면 딱 맞는 운동 루틴을 추천해드립니다.<br />
          운동 기록을 시각화해서 동기를 유지하세요.
        </p>
        <Link to="/signup" style={{
          display: 'inline-block',
          background: '#4F7EF7',
          color: 'white',
          padding: '16px 40px',
          borderRadius: '12px',
          textDecoration: 'none',
          fontWeight: 700,
          fontSize: '17px',
          boxShadow: '0 4px 15px rgba(79,126,247,0.3)',
        }}>
          무료로 시작하기 →
        </Link>
      </section>

      {/* 기능 카드 3개 */}
      <section style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(3, 1fr)',
        gap: '20px',
        maxWidth: '900px',
        margin: '0 auto',
        padding: '0 20px 80px',
      }}>
        {[
          { icon: '🎯', title: '맞춤형 루틴 추천', desc: '신체 정보와 목표에 맞게 자동으로 운동 루틴을 생성해드립니다.' },
          { icon: '✅', title: '간편한 운동 기록', desc: '오늘 운동한 항목을 체크만 하면 자동으로 기록됩니다.' },
          { icon: '📊', title: '대시보드 시각화', desc: '주간·월간 운동 달성률을 그래프로 한눈에 확인하세요.' },
        ].map((item) => (
          <div key={item.title} className="card" style={{ textAlign: 'center' }}>
            <div style={{ fontSize: '36px', marginBottom: '12px' }}>{item.icon}</div>
            <h3 style={{ fontWeight: 700, fontSize: '16px', marginBottom: '8px', color: '#1E293B' }}>
              {item.title}
            </h3>
            <p style={{ fontSize: '14px', color: '#64748B', lineHeight: 1.6 }}>{item.desc}</p>
          </div>
        ))}
      </section>
    </div>
  )
}
