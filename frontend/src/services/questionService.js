import api from './api'

export default {
  getMyQuestions(params) {
    return api.get('/questions/my', { params })
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
  
  generateAIQuestions(data) {
    return api.post('/questions/generate-ai', data)
  },
  
  parseWordFile(file) {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/questions/parse-word', formData, {
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
  },
  
  saveAIQuestions(questions) {
    return api.post('/questions/save-ai-questions', questions)
  },
  
  saveWordQuestions(questions) {
    return api.post('/questions/save-word-questions', questions)
  }
}
