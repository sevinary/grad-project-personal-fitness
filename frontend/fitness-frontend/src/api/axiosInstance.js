import axios from 'axios'

const axiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 요청 인터셉터: 모든 요청에 JWT 토큰 자동으로 붙이기
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 응답 인터셉터: 401(인증 만료) 시 로그인 페이지로 이동
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default axiosInstance
