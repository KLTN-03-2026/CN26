<template>
  <div class="exam-layout">
    <ExamSidebar
      :exam-name="exam?.name || 'Đang tải...'"
      :time-remaining="timeRemaining"
      :questions="questions"
      :answers="answers"
      :current-index="currentQuestionIndex"
      @go-to-question="goToQuestion"
      @submit="confirmSubmit"
    />

    <div class="exam-main-content">
      <LoadingSpinner v-if="loading" />
      
      <EmptyState 
        v-else-if="!exam"
        message="Không tìm thấy đề thi"
        actionText="Quay lại"
        @action="router.push('/student')"
      />
      
      <QuestionDisplay
        v-else-if="currentQuestion"
        :question="currentQuestion"
        :current-index="currentQuestionIndex"
        :total-questions="questions.length"
        :selected-answer="answers[currentQuestion.id]"
        @select-answer="handleSelectAnswer"
        @previous="previousQuestion"
        @next="nextQuestion"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import resultService from '../../services/resultService'
import examService from '../../services/examService'
import LoadingSpinner from '../../components/common/LoadingSpinner.vue'
import EmptyState from '../../components/common/EmptyState.vue'
import ExamSidebar from '../../components/student/ExamSidebar.vue'
import QuestionDisplay from '../../components/student/QuestionDisplay.vue'

const router = useRouter()
const route = useRoute()

const exam = ref(null)
const questions = ref([])
const answers = ref({})
const currentQuestionIndex = ref(0)
const loading = ref(false)
const timeRemaining = ref(0)
const resultId = ref(null)
const examStartTime = ref(null)
let timer = null

const STORAGE_KEY = computed(() => `exam_${route.params.id}_state`)

const currentQuestion = computed(() => questions.value[currentQuestionIndex.value])

onMounted(async () => {
  // Try to restore state from localStorage
  const savedState = localStorage.getItem(STORAGE_KEY.value)
  if (savedState) {
    try {
      const state = JSON.parse(savedState)
      answers.value = state.answers || {}
      currentQuestionIndex.value = state.currentQuestionIndex || 0
      resultId.value = state.resultId
      examStartTime.value = state.examStartTime
    } catch (e) {
      console.error('Error restoring state:', e)
    }
  }
  
  const success = await startExam()
  // Only start timer if exam started successfully AND time remaining > 0
  if (success && timeRemaining.value > 0) {
    startTimer()
  }
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const startExam = async () => {
  try {
    loading.value = true
    
    // If resultId already exists in localStorage, don't call startExam again (avoid duplicate)
    if (!resultId.value) {
      const startResponse = await resultService.startExam(route.params.id)
      if (startResponse.success) {
        resultId.value = startResponse.data.id
        if (!examStartTime.value) {
          examStartTime.value = startResponse.data.startTime
        }
      }
    }
    
    const examResponse = await examService.getExamById(route.params.id)
    if (examResponse.success) {
      exam.value = examResponse.data
    }
    
    const questionsResponse = await examService.getExamQuestions(route.params.id)
    if (questionsResponse.success) {
      questions.value = questionsResponse.data
    }
    
    // Calculate time remaining based on start time
    if (exam.value && exam.value.duration && examStartTime.value) {
      const startTime = new Date(examStartTime.value)
      const now = new Date()
      const elapsedSeconds = Math.floor((now - startTime) / 1000)
      const totalSeconds = exam.value.duration * 60
      timeRemaining.value = Math.max(0, totalSeconds - elapsedSeconds)
    }
    
    // Save state to localStorage
    saveState()
    return true // Return success
  } catch (error) {
    console.error('Error starting exam:', error)
    const errorMsg = error.error || error.message || 'Không thể bắt đầu bài thi'
    alert(errorMsg)
    router.push('/student')
    return false // Return failure
  } finally {
    loading.value = false
  }
}

const startTimer = () => {
  timer = setInterval(() => {
    if (timeRemaining.value > 0) {
      timeRemaining.value--
    } else {
      // Clear timer first to prevent infinite loop
      if (timer) clearInterval(timer)
      submitExam()
    }
  }, 1000)
}

const handleSelectAnswer = (questionId, answer) => {
  answers.value[questionId] = answer
  saveState()
}

const saveState = () => {
  const state = {
    answers: answers.value,
    currentQuestionIndex: currentQuestionIndex.value,
    resultId: resultId.value,
    examStartTime: examStartTime.value
  }
  localStorage.setItem(STORAGE_KEY.value, JSON.stringify(state))
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
  const answeredCount = Object.keys(answers.value).length
  const unanswered = questions.value.length - answeredCount
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
      // Clear saved state after successful submit
      localStorage.removeItem(STORAGE_KEY.value)
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

.exam-main-content {
  margin-left: 320px;
  flex: 1;
  padding: 40px;
}

@media (max-width: 1024px) {
  .exam-main-content {
    margin-left: 280px;
    padding: 30px 20px;
  }
}
</style>
