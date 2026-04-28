import { defineStore } from 'pinia'
import authService from '../services/authService'

export const useAuthStore = defineStore('auth', {
  state: () => {
    const token = localStorage.getItem('token')
    let isAuthenticated = false
    
    // Check if token is expired
    if (token) {
      try {
        // Parse JWT payload (base64 decode middle part)
        const payload = JSON.parse(atob(token.split('.')[1]))
        const exp = payload.exp
        
        // Check if token is expired (exp is in seconds, Date.now() is in milliseconds)
        if (exp && exp * 1000 > Date.now()) {
          isAuthenticated = true
        } else {
          // Token expired, clear it
          localStorage.removeItem('token')
          localStorage.removeItem('user')
        }
      } catch (e) {
        // Invalid token format, clear it
        localStorage.removeItem('token')
        localStorage.removeItem('user')
      }
    }
    
    return {
      user: isAuthenticated ? JSON.parse(localStorage.getItem('user')) : null,
      token: isAuthenticated ? token : null,
      isAuthenticated
    }
  },
  
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
