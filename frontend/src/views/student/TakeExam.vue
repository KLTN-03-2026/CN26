<template>
  <div class="exam-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <img src="/logo/logo.png" alt="Logo" class="sidebar-logo" />
        <h2>{{ exam?.name || 'Đang tải...' }}</h2>
        <div class="timer-box">
          <div class="timer-label">Thời gian còn lại</div>
          <div class="timer-value">{{ formatTime(timeRemaining) }}</div>
        </div>
      </div>
      
      <div class="question-grid">
        <div class="grid-header">
          <span>Danh sách câu hỏi</span>
          <span class="answered-count">{{ answeredCount }}/{{ questions.length }}</span>
        </div>
        <div class="grid-container">
          <button v-for="(question, index) in questions" 
                  :key="question.id"
                  @click="goToQuestion(index)"
                  :class="['question-btn', { 
                    'answered': answers[question.id], 
                    'current': index === currentQuestionIndex 
                  }]">
            {{ index + 1 }}
          </button>
        </div>
      </div>

      <div class="sidebar-footer">
        <button class="btn-submit-exam" @click="confirmSubmit">
          Nộp bài
        </button>
      </div>
    </aside>

    <div class="main-content">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Đang tải đề thi...</p>
      </div>
      
      <div v-else-if="!exam" class="error-state">
        <p>Không tìm thấy đề thi</p>
        <button class="btn-back" @click="router.push('/student')">Quay lại</button>
      </div>
      
      <div v-else class="question-container">
        <div class="question-header">
          <h1>Câu {{ currentQuestionIndex + 1 }}/{{ questions.length }}</h1>
        </div>

        <div v-if="currentQuestion" class="question-content">
          <div class="question-text">
            {{ currentQuestion.content }}
          </div>
          
          <div class="options-list">
            <label v-for="option in ['A', 'B', 'C', 'D']" :key="option" 
                   class="option-item"
                   :class="{ 'selected': answers[currentQuestion.id] === option }">
              <input type="radio" 
                     :name="'question-' + currentQuestion.id"
                     :value="option"
                     v-model="answers[currentQuestion.id]">
              <span class="option-letter">{{ option }}</span>
              <span class="option-text">{{ currentQuestion['option' + option] }}</span>
            </label>
          </div>
        </div>

        <div class="navigation-buttons">
          <button class="btn-nav btn-prev" @click="previousQuestion" :disabled="currentQuestionIndex === 0">
            ← Câu trước
          </button>
          <button v-if="currentQuestionIndex < questions.length - 1" 
                  class="btn-nav btn-next" 
                  @click="nextQuestion">
            Câu tiếp →
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import resultService from '../../services/resultService'
import examService from '../../services/examService'

const router = useRouter()
const route = useRoute()

const exam = ref(null)
const questions = ref([])
const answers = ref({})
const currentQuestionIndex = ref(0)
const loading = ref(false)
const timeRemaining = ref(0)
const resultId = ref(null)
let timer = null

const currentQuestion = computed(() => questions.value[currentQuestionIndex.value])
const answeredCount = computed(() => Object.keys(answers.value).length)

onMounted(async () => {
  await startExam()
  startTimer()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const startExam = async () => {
  try {
    loading.value = true
    
    // Start exam and get result ID
    const startResponse = await resultService.startExam(route.params.id)
    if (startResponse.success) {
      resultId.value = startResponse.data.id
    }
    
    // Get exam details
    const examResponse = await examService.getExamById(route.params.id)
    if (examResponse.success) {
      exam.value = examResponse.data
    }
    
    // Get exam questions
    const questionsResponse = await examService.getExamQuestions(route.params.id)
    if (questionsResponse.success) {
      questions.value = questionsResponse.data
    }
    
    // Set timer
    if (exam.value && exam.value.duration) {
      timeRemaining.value = exam.value.duration * 60
    }
  } catch (error) {
    console.error('Error starting exam:', error)
    const errorMsg = error.response?.data?.message || error.message || 'Không thể bắt đầu bài thi'
    alert(errorMsg)
    router.push('/student')
  } finally {
    loading.value = false
  }
}

const startTimer = () => {
  timer = setInterval(() => {
    if (timeRemaining.value > 0) {
      timeRemaining.value--
    } else {
      submitExam()
    }
  }, 1000)
}

const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const nextQuestion = () => {
  if (currentQuestionIndex.value < questions.value.length - 1) {
    currentQuestionIndex.value++
  }
}

const previousQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

const goToQuestion = (index) => {
  currentQuestionIndex.value = index
}

const confirmSubmit = () => {
  const unanswered = questions.value.length - answeredCount.value
  if (unanswered > 0) {
    if (!confirm(`Bạn còn ${unanswered} câu chưa trả lời. Bạn có chắc muốn nộp bài?`)) {
      return
    }
  }
  submitExam()
}

const submitExam = async () => {
  if (timer) clearInterval(timer)
  
  try {
    const answersList = questions.value.map(q => ({
      questionId: q.id,
      selectedAnswer: answers.value[q.id] || null
    }))

    const response = await resultService.submitExam({
      resultId: resultId.value,
      answers: answersList
    })
    
    if (response.success) {
      alert('Nộp bài thành công!')
      router.push(`/student/result/${resultId.value}`)
    }
  } catch (error) {
    console.error('Error submitting exam:', error)
    alert('Có lỗi khi nộp bài')
  }
}
</script>

<style scoped>
.exam-layout {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.sidebar {
  width: 320px;
  background: linear-gradient(180deg, #1e40af 0%, #1e3a8a 100%);
  color: white;
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  overflow-y: auto;
}

.sidebar-header {
  padding: 30px 20px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  text-align: center;
}

.sidebar-logo {
  height: 80px;
  width: auto;
  object-fit: contain;
  margin-bottom: 15px;
  background: white;
  padding: 12px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.sidebar-header h2 {
  font-size: 18px;
  margin: 0 0 20px 0;
  font-weight: 700;
  line-height: 1.4;
}

.timer-box {
  background: rgba(255,255,255,0.15);
  padding: 15px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.timer-label {
  font-size: 12px;
  opacity: 0.9;
  margin-bottom: 5px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.timer-value {
  font-size: 32px;
  font-weight: 800;
  color: #fbbf24;
  font-family: 'Courier New', monospace;
}

.question-grid {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.grid-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  font-size: 14px;
  font-weight: 600;
}

.answered-count {
  background: rgba(255,255,255,0.2);
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
}

.grid-container {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
}

.question-btn {
  aspect-ratio: 1;
  border: 2px solid rgba(255,255,255,0.3);
  background: rgba(255,255,255,0.1);
  color: white;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.2s;
}

.question-btn:hover {
  background: rgba(255,255,255,0.2);
  transform: translateY(-2px);
}

.question-btn.answered {
  background: #10b981;
  border-color: #10b981;
}

.question-btn.current {
  border-color: #fbbf24;
  border-width: 3px;
  background: rgba(251,191,36,0.2);
}

.sidebar-footer {
  padding: 20px;
  border-top: 1px solid rgba(255,255,255,0.1);
}

.btn-submit-exam {
  width: 100%;
  padding: 14px;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 700;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-submit-exam:hover {
  background: #dc2626;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(239,68,68,0.4);
}

.main-content {
  margin-left: 320px;
  flex: 1;
  padding: 40px;
}

.loading-state, .error-state {
  text-align: center;
  padding: 60px 20px;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #e5e7eb;
  border-top-color: #1e40af;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-state p {
  font-size: 18px;
  color: #6b7280;
  margin-bottom: 20px;
}

.btn-back {
  padding: 12px 24px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}

.question-container {
  max-width: 900px;
  margin: 0 auto;
}

.question-header {
  background: white;
  padding: 25px 30px;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-bottom: 25px;
}

.question-header h1 {
  font-size: 24px;
  color: #1f2937;
  margin: 0;
  font-weight: 800;
}

.question-content {
  background: white;
  padding: 35px;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-bottom: 25px;
}

.question-text {
  font-size: 20px;
  color: #1f2937;
  line-height: 1.7;
  margin-bottom: 30px;
  font-weight: 500;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 18px 20px;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  background: #f9fafb;
}

.option-item:hover {
  border-color: #1e40af;
  background: #eff6ff;
  transform: translateX(4px);
}

.option-item.selected {
  border-color: #1e40af;
  background: #dbeafe;
  box-shadow: 0 0 0 3px rgba(30,64,175,0.1);
}

.option-item input[type="radio"] {
  margin-right: 15px;
  width: 20px;
  height: 20px;
  cursor: pointer;
}

.option-letter {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: #1e40af;
  color: white;
  border-radius: 8px;
  font-weight: 700;
  font-size: 16px;
  margin-right: 15px;
  flex-shrink: 0;
}

.option-item.selected .option-letter {
  background: #1e3a8a;
}

.option-text {
  font-size: 16px;
  color: #374151;
  line-height: 1.6;
  flex: 1;
}

.navigation-buttons {
  display: flex;
  justify-content: space-between;
  gap: 15px;
}

.btn-nav {
  padding: 14px 28px;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-prev {
  background: #f3f4f6;
  color: #374151;
}

.btn-prev:hover:not(:disabled) {
  background: #e5e7eb;
  transform: translateX(-2px);
}

.btn-prev:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-next {
  background: #1e40af;
  color: white;
  margin-left: auto;
}

.btn-next:hover {
  background: #1e3a8a;
  transform: translateX(2px);
  box-shadow: 0 4px 12px rgba(30,64,175,0.3);
}

@media (max-width: 1024px) {
  .sidebar {
    width: 280px;
  }
  .main-content {
    margin-left: 280px;
    padding: 30px 20px;
  }
  .grid-container {
    grid-template-columns: repeat(4, 1fr);
  }
}
</style>
