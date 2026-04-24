import api from './api'

export default {
  getProfile() {
    return api.get('/users/profile')
  },
  
  updateProfile(data) {
    return api.put('/users/profile', data)
  },
  
  changePassword(data) {
    return api.post('/users/change-password', data)
  },
  
  getAllUsers() {
    return api.get('/users')
  },
  
  getUserById(id) {
    return api.get(`/users/${id}`)
  },
  
  lockUser(id) {
    return api.post(`/users/${id}/lock`)
  },
  
  unlockUser(id) {
    return api.post(`/users/${id}/unlock`)
  },
  
  deleteUser(id) {
    return api.delete(`/users/${id}`)
  }
}
