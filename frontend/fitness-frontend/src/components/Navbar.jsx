import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  const handleLogout = async () => {
    await logout()
    navigate('/')
  }

  const isActive = (path) => location.pathname === path

  return (
    <nav style={{
      background: 'white',
      borderBottom: '1.5px solid #E2E8F0',
      padding: '0 24px',
      height: '60px',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      position: 'sticky',
      top: 0,
      zIndex: 100,
    }}>
      {/* 로고 */}
      <Link to="/home" style={{ textDecoration: 'none', display: 'flex', alignItems: 'center', gap: '8px' }}>
        <span style={{ fontSize: '20px' }}>💪</span>
        <span style={{ fontWeight: 700, fontSize: '17px', color: '#4F7EF7' }}>FitStart</span>
      </Link>

      {/* 메뉴 */}
      {user && (
        <div style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
          <NavLink to="/home" active={isActive('/home')}>홈</NavLink>
          <NavLink to="/routine" active={isActive('/routine')}>운동 루틴</NavLink>
          <NavLink to="/dashboard" active={isActive('/dashboard')}>대시보드</NavLink>
          <button
            onClick={handleLogout}
            style={{
              background: 'none',
              border: '1.5px solid #E2E8F0',
              borderRadius: '8px',
              padding: '6px 14px',
              fontSize: '13px',
              fontWeight: 500,
              color: '#64748B',
              cursor: 'pointer',
              marginLeft: '8px',
            }}
          >
            로그아웃
          </button>
        </div>
      )}
    </nav>
  )
}

function NavLink({ to, children, active }) {
  return (
    <Link
      to={to}
      style={{
        textDecoration: 'none',
        padding: '6px 14px',
        borderRadius: '8px',
        fontSize: '14px',
        fontWeight: active ? 600 : 400,
        color: active ? '#4F7EF7' : '#64748B',
        background: active ? '#EEF2FF' : 'transparent',
        transition: 'background 0.15s',
      }}
    >
      {children}
    </Link>
  )
}
