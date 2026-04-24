import { defineStore } from 'pinia'
import authService from '../services/authService'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
    isAuthenticated: !!localStorage.getItem('token')
  }),
  
  getters: {
    isStudent: (state) => state.user?.role === 'student',
    isTeacher: (state) => state.user?.role === 'teacher',
    isAdmin: (state) => state.user?.role === 'admin'
  },
  
  actions: {
    async register(data) {
      const response = await authService.register(data)
      // Không gọi setAuth() vì tài khoản cần xác thực email trước
      return response
    },
    
    async login(data) {
      const response = await authService.login(data)
      if (response.success) {
        this.setAuth(response.data)
      }
      return response
    },
    
    setAuth(data) {
      this.user = {
        id: data.userId,
        email: data.email,
        fullName: data.fullName,
        role: data.role
      }
      this.token = data.token
      this.isAuthenticated = true
      
      localStorage.setItem('user', JSON.stringify(this.user))
      localStorage.setItem('token', data.token)
    },
    
    logout() {
      this.user = null
      this.token = null
      this.isAuthenticated = false
      
      localStorage.removeItem('user')
      localStorage.removeItem('token')
    }
  }
})
