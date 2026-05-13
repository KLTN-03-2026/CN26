import api from './api'

export default {
  getActiveExams() {
    return api.get('/exams/active')
  },
  
  getMyExams() {
    return api.get('/exams/my')
  },
  
  getExamById(id) {
    return api.get(`/exams/${id}`)
  },
  
  getExamQuestions(id) {
    return api.get(`/exams/${id}/questions`)
  },
  
  createExam(data) {
    return api.post('/exams', data)
  },
  
  updateExam(id, data) {
    return api.put(`/exams/${id}`, data)
  },
  
  deleteExam(id) {
    return api.delete(`/exams/${id}`)
  },
  
  toggleExamStatus(id) {
    return api.post(`/exams/${id}/toggle-status`)
  }
}
