import api from './api'

export default {
  register(data) {
    return api.post('/auth/register', data)
  },
  
  login(data) {
    return api.post('/auth/login', data)
  },
  
  verifyEmail(token) {
    return api.get(`/auth/verify?token=${token}`)
  },
  
  forgotPassword(email) {
    return api.post(`/auth/forgot-password?email=${email}`)
  },
  
  resetPassword(token, newPassword) {
    return api.post(`/auth/reset-password?token=${token}&newPassword=${newPassword}`)
  }
}
