import api from './api'

export default {
  startExam(examId) {
    return api.post(`/results/start/${examId}`)
  },
  
  submitExam(data) {
    return api.post('/results/submit', data)
  },
  
  getMyResults() {
    return api.get('/results/my')
  },
  
  getResultById(id) {
    return api.get(`/results/${id}`)
  },
  
  getResultDetail(id) {
    return api.get(`/results/${id}`)
  },
  
  getResultAnswers(id) {
    return api.get(`/results/${id}/answers`)
  },
  
  getExamResults(examId) {
    return api.get(`/results/exam/${examId}`)
  },
  
  getExamStatistics(examId) {
    return api.get(`/results/exam/${examId}/statistics`)
  }
}
