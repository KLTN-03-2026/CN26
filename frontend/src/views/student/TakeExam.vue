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
let timer = null

const currentQuestion = computed(() => questions.value[currentQuestionIndex.value])

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
    
    const startResponse = await resultService.startExam(route.params.id)
    if (startResponse.success) {
      resultId.value = startResponse.data.id
    }
    
    const examResponse = await examService.getExamById(route.params.id)
    if (examResponse.success) {
      exam.value = examResponse.data
    }
    
    const questionsResponse = await examService.getExamQuestions(route.params.id)
    if (questionsResponse.success) {
      questions.value = questionsResponse.data
    }
    
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

const handleSelectAnswer = (questionId, answer) => {
  answers.value[questionId] = answer
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
