import { createContext, useContext, useState, useEffect } from 'react'
import axiosInstance from '../api/axiosInstance'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    // 새로고침해도 로그인 유지
    const savedUser = localStorage.getItem('user')
    const token = localStorage.getItem('token')
    if (savedUser && token) {
      setUser(JSON.parse(savedUser))
    }
    setLoading(false)
  }, [])

  const login = async (username, password) => {
  const response = await axiosInstance.post('/auth/login', { username, password })
  const { accessToken, userId, username: userName } = response.data.data
  localStorage.setItem('token', accessToken)
  localStorage.setItem('user', JSON.stringify({ userId, username: userName }))
  setUser({ userId, username: userName })
  return response.data.data
}

  const signup = async (email, password, username) => {
    const response = await axiosInstance.post('/auth/signup', { email, password, username })
    return response.data
  }

  const logout = async () => {
    try {
      await axiosInstance.post('/auth/logout')
    } catch (e) {
      // 로그아웃 실패해도 로컬은 지우기
    }
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, login, signup, logout, loading }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
