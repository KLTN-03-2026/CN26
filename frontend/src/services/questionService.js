import api from './api'

export default {
  getAllQuestions() {
    return api.get('/questions')
  },
  
  getMyQuestions() {
    return api.get('/questions/my')
  },
  
  getQuestionById(id) {
    return api.get(`/questions/${id}`)
  },
  
  getQuestionsBySubject(subject) {
    return api.get(`/questions/subject/${subject}`)
  },
  
  getQuestionsByLevel(level) {
    return api.get(`/questions/level/${level}`)
  },
  
  createQuestion(data) {
    return api.post('/questions', data)
  },
  
  updateQuestion(id, data) {
    return api.put(`/questions/${id}`, data)
  },
  
  deleteQuestion(id) {
    return api.delete(`/questions/${id}`)
  },
  
  generateQuestionsWithAI(data) {
    return api.post('/questions/generate-ai', data)
  },
  
  generateAIQuestions(data) {
    return api.post('/questions/generate-ai', data)
  },
  
  importQuestionsFromWord(file) {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/questions/import-word', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },
  
  importFromWord(file) {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/questions/import-word', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}
