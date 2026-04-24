import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor - Add token to headers
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor - Handle errors
api.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    // Don't auto-redirect on 401 if it's a login attempt with inactive account
    const errorMessage = error.response?.data?.error || error.response?.data?.message || ''
    const isInactiveAccount = errorMessage.includes('chưa được kích hoạt') || errorMessage.includes('inactive')
    const isLoginPage = window.location.pathname === '/login'
    
    // Only redirect if not on login page and not inactive account error
    if (error.response?.status === 401 && !isInactiveAccount && !isLoginPage) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error.response?.data || error.message)
  }
)

export default api
