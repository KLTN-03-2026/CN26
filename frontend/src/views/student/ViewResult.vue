<template>
  <StudentLayout activeMenu="results">
    <LoadingSpinner v-if="loading" />

    <EmptyState 
      v-else-if="!result"
      message="Không tìm thấy kết quả"
      actionText="Quay lại danh sách"
      @action="$router.push('/student/results')"
    />

    <div v-else>
      <header class="top-header">
        <div class="header-left">
          <h1>{{ result.examName }}</h1>
        </div>
        <button class="btn-back" @click="$router.push('/student/results')">Quay lại</button>
      </header>

      <div class="result-summary">
        <div class="summary-card">
          <div class="summary-score">
            <div :class="['score-circle', getScoreClass(result.score)]">
              <span class="score-value">{{ result.totalCorrect }}/{{ result.totalQuestions }}</span>
            </div>
          </div>
          <div class="summary-stats">
            <div class="stat-item">
              <span class="stat-label">Thời gian làm</span>
              <span class="stat-value">{{ formatTimeSpent(result.timeSpent) }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">Ngày thi</span>
              <span class="stat-value">{{ formatDateTime(result.submitTime) }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="answers-section" v-if="result.answers && result.answers.length > 0">
        <h2>Chi Tiết Câu Trả Lời</h2>
        <div v-for="(answer, index) in result.answers" :key="answer.id" class="answer-card">
          <div class="answer-header">
            <span class="question-number">Câu {{ index + 1 }}</span>
            <span :class="['answer-status', answer.isCorrect ? 'correct' : 'incorrect']">
              {{ answer.isCorrect ? '✓ Đúng' : '✗ Sai' }}
            </span>
          </div>
          <div class="answer-body">
            <p class="question-text">{{ answer.questionContent }}</p>
            <div class="answer-options">
              <div v-for="opt in ['A', 'B', 'C', 'D']" :key="opt" 
                   :class="['option', {
                     'selected': answer.selectedAnswer === opt,
                     'correct': answer.correctAnswer === opt,
                     'wrong': answer.selectedAnswer === opt && !answer.isCorrect
                   }]">
                <span class="option-letter">{{ opt }}</span>
                <span class="option-text">{{ answer['option' + opt] }}</span>
              </div>
            </div>
            <div v-if="!answer.isCorrect" class="correct-answer-note">
              Đáp án đúng: <strong>{{ answer.correctAnswer }}</strong>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="no-details-message">
        <div class="info-box">
          <h3>Thông báo</h3>
          <p>Chi tiết câu trả lời sẽ được công bố sau khi đề thi kết thúc.</p>
          <p>Hiện tại bạn chỉ có thể xem điểm số.</p>
        </div>
      </div>
    </div>
  </StudentLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useFormatters } from '../../composables/useFormatters'
import resultService from '../../services/resultService'
import StudentLayout from '../../components/layouts/StudentLayout.vue'
import LoadingSpinner from '../../components/common/LoadingSpinner.vue'
import EmptyState from '../../components/common/EmptyState.vue'

const route = useRoute()
const router = useRouter()
const { getScoreClass, formatDateTime, formatTimeSpent } = useFormatters()

const result = ref(null)
const loading = ref(false)

onMounted(() => {
  loadResult()
})

const loadResult = async () => {
  try {
    loading.value = true
    const response = await resultService.getResultDetail(route.params.id)
    if (response.success) {
      result.value = response.data
    }
  } catch (error) {
    console.error('Error loading result:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.btn-back {
  padding: 10px 20px;
  background: #f3f4f6;
  color: #374151;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}

.result-summary {
  margin-bottom: 30px;
}

.summary-card {
  background: white;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  display: flex;
  gap: 40px;
  align-items: center;
}

.summary-score {
  flex-shrink: 0;
}

.score-circle {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 8px solid;
}

.score-circle.excellent {
  border-color: #10b981;
  background: #d1fae5;
}

.score-circle.good {
  border-color: #3b82f6;
  background: #dbeafe;
}

.score-circle.average {
  border-color: #f59e0b;
  background: #fef3c7;
}

.score-circle.poor {
  border-color: #ef4444;
  background: #fee2e2;
}

.score-value {
  font-size: 36px;
  font-weight: 800;
  color: #1f2937;
}

.summary-stats {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.stat-value {
  font-size: 24px;
  color: #1f2937;
  font-weight: 700;
}

.answers-section h2 {
  font-size: 22px;
  color: #1f2937;
  margin: 0 0 20px 0;
  font-weight: 700;
}

.answer-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-bottom: 20px;
  overflow: hidden;
}

.answer-header {
  padding: 15px 20px;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.question-number {
  font-size: 16px;
  color: #1f2937;
  font-weight: 700;
}

.answer-status {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.answer-status.correct {
  background: #d1fae5;
  color: #065f46;
}

.answer-status.incorrect {
  background: #fee2e2;
  color: #991b1b;
}

.answer-body {
  padding: 20px;
}

.question-text {
  font-size: 16px;
  color: #1f2937;
  font-weight: 600;
  margin: 0 0 20px 0;
  line-height: 1.6;
}

.answer-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 15px;
}

.option {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  background: #f9fafb;
}

.option.selected {
  border-color: #3b82f6;
  background: #eff6ff;
}

.option.correct {
  border-color: #10b981;
  background: #d1fae5;
}

.option.wrong {
  border-color: #ef4444;
  background: #fee2e2;
}

.option-letter {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: #1e40af;
  color: white;
  border-radius: 6px;
  font-weight: 700;
  font-size: 14px;
  margin-right: 12px;
  flex-shrink: 0;
}

.option.correct .option-letter {
  background: #10b981;
}

.option.wrong .option-letter {
  background: #ef4444;
}

.option-text {
  font-size: 14px;
  color: #374151;
}

.correct-answer-note {
  padding: 12px 15px;
  background: #d1fae5;
  border-left: 3px solid #10b981;
  border-radius: 6px;
  color: #065f46;
  font-size: 14px;
}

.correct-answer-note strong {
  font-weight: 700;
}

.no-details-message {
  margin-top: 30px;
}

.info-box {
  background: #eff6ff;
  border: 2px solid #3b82f6;
  border-radius: 12px;
  padding: 30px;
  text-align: center;
}

.info-box h3 {
  font-size: 20px;
  color: #1e40af;
  margin: 0 0 15px 0;
}

.info-box p {
  font-size: 16px;
  color: #1f2937;
  margin: 8px 0;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .summary-card {
    flex-direction: column;
  }
  .summary-stats {
    grid-template-columns: 1fr;
  }
}
</style>
