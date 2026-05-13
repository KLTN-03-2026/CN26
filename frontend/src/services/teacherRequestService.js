import api from './api'

export default {
  createRequest(formData) {
    return api.post('/teacher-requests', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },
  
  getMyRequests() {
    return api.get('/teacher-requests/my')
  },
  
  getAllRequests() {
    return api.get('/teacher-requests')
  },
  
  getPendingRequests() {
    return api.get('/teacher-requests/pending')
  },
  
  reviewRequest(id, data) {
    return api.post(`/teacher-requests/${id}/review`, data)
  },
  
  deleteRequest(id) {
    return api.delete(`/teacher-requests/${id}`)
  },
  
  countPendingRequests() {
    return api.get('/teacher-requests/count/pending')
  }
}
