import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

// 로그인 안 하면 /login 으로 보내는 컴포넌트
export default function PrivateRoute({ children }) {
  const { user, loading } = useAuth()

  if (loading) {
    return <div className="spinner" />
  }

  return user ? children : <Navigate to="/login" replace />
}
