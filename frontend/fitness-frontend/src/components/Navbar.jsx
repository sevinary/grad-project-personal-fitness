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
      borderBottom: '1px solid var(--border)',
      padding: '0 24px',
      height: '60px',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      position: 'sticky',
      top: 0,
      zIndex: 100,
    }}>
      <Link to="/home" style={{ textDecoration: 'none' }}>
        <Wordmark />
      </Link>

      {user && (
        <div style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
          <NavLink to="/home" active={isActive('/home')}>홈</NavLink>
          <NavLink to="/routine" active={isActive('/routine')}>오늘의 루틴</NavLink>
          <NavLink to="/dashboard" active={isActive('/dashboard')}>대시보드</NavLink>
          <button
            onClick={handleLogout}
            style={{
              background: 'none',
              border: '1px solid var(--border)',
              borderRadius: '8px',
              padding: '6px 14px',
              fontSize: '13px',
              fontWeight: 500,
              color: 'var(--text-secondary)',
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

export function Wordmark({ size = 19 }) {
  return (
    <span style={{ fontWeight: 800, fontSize: `${size}px`, letterSpacing: '1.5px', color: 'var(--text)' }}>
      <span style={{ color: 'var(--primary)' }}>FIT</span>ORY
    </span>
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
        fontWeight: active ? 700 : 500,
        color: active ? 'var(--primary)' : 'var(--text-secondary)',
        background: active ? 'var(--primary-light)' : 'transparent',
        transition: 'background 0.15s',
      }}
    >
      {children}
    </Link>
  )
}
