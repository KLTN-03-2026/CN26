<template>
  <div class="dashboard-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <img src="/logo/logo.png" alt="Logo" class="sidebar-logo" />
        <h2>Hệ Thống Thi</h2>
        <p>Học sinh</p>
      </div>
      <nav class="sidebar-menu">
        <a href="#" @click.prevent="$router.push('/student')" class="menu-item">
          Trang chủ
        </a>
        <a href="#" @click.prevent="$router.push('/student/exams')" class="menu-item">
          Đề thi
        </a>
        <a href="#" @click.prevent="$router.push('/student/results')" class="menu-item active">
          Kết quả
        </a>
        <a href="#" @click.prevent="$router.push('/student/profile')" class="menu-item">
          Cài đặt
        </a>
      </nav>
      <div class="sidebar-footer">
        <button class="btn-help" @click="$router.push('/student/teacher-request')">Trở thành giáo viên</button>
      </div>
    </aside>
    <div class="main-content">
      <header class="top-header">
        <div class="header-left">
          <button class="btn-back" @click="$router.push('/student/results')">
            ← Quay lại
          </button>
          <h1>Chi Tiết Kết Quả</h1>
        </div>
      </header>

      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Đang tải kết quả...</p>
      </div>

      <div v-else-if="!result" class="empty-state">
        <h3>Không tìm thấy kết quả</h3>
        <p>Kết quả thi không tồn tại hoặc đã bị xóa</p>
        <button class="btn-start" @click="$router.push('/student/results')">Quay lại danh sách</button>
      </div>

      <div v-else class="result-detail">
        <div class="result-summary">
          <div class="summary-header">
            <h2>{{ result.examName }}</h2>
            <div class="exam-date">{{ formatDateFull(result.submitTime) }}</div>
          </div>
          <div class="summary-stats">
            <div class="stat-box primary">
              <div class="stat-label">Điểm số</div>
              <div class="stat-value" :class="getScoreClass(result.score)">{{ result.score }}%</div>
              <div class="stat-sublabel">{{ (result.score / 10).toFixed(1) }}/10</div>
            </div>
            <div class="stat-box">
              <div class="stat-label">Số câu đúng</div>
              <div class="stat-value">{{ result.totalCorrect || result.correctAnswers || 0 }}</div>
              <div class="stat-sublabel">/ {{ result.totalQuestions }} câu</div>
            </div>
            <div class="stat-box">
              <div class="stat-label">Thời gian làm bài</div>
              <div class="stat-value">{{ Math.floor(result.timeSpent / 60) }}:{{ String(result.timeSpent % 60).padStart(2, '0') }}</div>
              <div class="stat-sublabel">phút:giây</div>
            </div>
            <div class="stat-box">
              <div class="stat-label">Tỷ lệ đúng</div>
              <div class="stat-value">{{ Math.round(((result.totalCorrect || result.correctAnswers || 0) / result.totalQuestions) * 100) }}%</div>
              <div class="stat-sublabel">câu trả lời đúng</div>
            </div>
          </div>
        </div>

        <div class="questions-section">
          <div class="section-header">
            <h3>Chi Tiết Từng Câu Hỏi</h3>
            <div class="legend">
              <span class="legend-item correct">Đúng: {{ result.totalCorrect || result.correctAnswers || 0 }}</span>
              <span class="legend-item incorrect">Sai: {{ result.totalQuestions - (result.totalCorrect || result.correctAnswers || 0) }}</span>
            </div>
          </div>

          <div class="questions-list">
            <div v-for="(answer, index) in result.answers" :key="answer.id" 
                 class="question-card"
                 :class="{ 'correct': answer.isCorrect, 'incorrect': !answer.isCorrect }">
              <div class="question-header">
                <div class="question-number">Câu {{ index + 1 }}</div>
                <div class="question-status" :class="{ 'status-correct': answer.isCorrect, 'status-incorrect': !answer.isCorrect }">
                  {{ answer.isCorrect ? 'Đúng' : 'Sai' }}
                </div>
              </div>
              <div class="question-content">
                <p class="question-text">{{ answer.questionContent }}</p>
              </div>
              <div class="question-options">
                <div v-for="option in ['A', 'B', 'C', 'D']" :key="option"
                     class="option-item"
                     :class="{
                       'selected': answer.selectedAnswer === option,
                       'correct-answer': answer.correctAnswer === option,
                       'wrong-selected': answer.selectedAnswer === option && !answer.isCorrect
                     }">
                  <div class="option-label">{{ option }}</div>
                  <div class="option-text">{{ answer['option' + option] }}</div>
                </div>
              </div>
              <div class="question-footer">
                <div class="answer-info">
                  <span class="info-label">Câu trả lời của bạn:</span>
                  <span class="info-value" :class="{ 'correct-text': answer.isCorrect, 'incorrect-text': !answer.isCorrect }">
                    {{ answer.selectedAnswer || 'Không trả lời' }}
                  </span>
                </div>
                <div v-if="!answer.isCorrect" class="answer-info">
                  <span class="info-label">Đáp án đúng:</span>
                  <span class="info-value correct-text">{{ answer.correctAnswer }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import resultService from '../../services/resultService'

const route = useRoute()
const router = useRouter()

const result = ref(null)
const loading = ref(false)

onMounted(() => {
  loadResult()
})

const loadResult = async () => {
  try {
    loading.value = true
    
    const resultResponse = await resultService.getResultDetail(route.params.id)
    if (resultResponse.success) {
      result.value = resultResponse.data
      
      const answersResponse = await resultService.getResultAnswers(route.params.id)
      if (answersResponse.success) {
        result.value.answers = answersResponse.data.map(answer => ({
          id: answer.id,
          questionContent: answer.question?.content || '',
          optionA: answer.question?.optionA || '',
          optionB: answer.question?.optionB || '',
          optionC: answer.question?.optionC || '',
          optionD: answer.question?.optionD || '',
          selectedAnswer: answer.selectedAnswer,
          correctAnswer: answer.question?.correctAnswer || '',
          isCorrect: answer.isCorrect
        }))
      }
    }
  } catch (error) {
    console.error('Error loading result:', error)
  } finally {
    loading.value = false
  }
}

const getScoreClass = (score) => {
  if (score >= 80) return 'excellent'
  if (score >= 65) return 'good'
  if (score >= 50) return 'average'
  return 'poor'
}

const formatDateFull = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.dashboard-layout {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.sidebar {
  width: 280px;
  background: linear-gradient(180deg, #1e40af 0%, #1e3a8a 100%);
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  left: 0;
  top: 0;
  box-shadow: 2px 0 10px rgba(0,0,0,0.1);
}

.sidebar-header {
  padding: 35px 25px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  text-align: center;
}

.sidebar-logo {
  height: 120px;
  width: auto;
  object-fit: contain;
  margin-bottom: 20px;
  background: white;
  padding: 18px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.sidebar-header h2 {
  font-size: 22px;
  color: #ffffff;
  margin: 0 0 6px 0;
  font-weight: 700;
}

.sidebar-header p {
  font-size: 13px;
  color: rgba(255,255,255,0.7);
  margin: 0;
}

.sidebar-menu {
  flex: 1;
  padding: 25px 15px;
  overflow-y: auto;
}

.menu-item {
  display: block;
  width: 100%;
  padding: 14px 18px;
  color: #ffffff;
  text-decoration: none;
  transition: all 0.3s ease;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 6px;
}

.menu-item:hover {
  background: rgba(255,255,255,0.15);
}

.menu-item.active {
  background: #ffffff;
  color: #1e40af;
  font-weight: 600;
}

.sidebar-footer {
  padding: 20px 15px 25px;
  border-top: 1px solid rgba(255,255,255,0.1);
}

.btn-new-exam {
  width: 100%;
  background: white;
  color: #1e40af;
  border: none;
  padding: 14px;
  border-radius: 10px;
  font-weight: 700;
  font-size: 15px;
  cursor: pointer;
  margin-bottom: 12px;
  transition: all 0.3s ease;
}

.btn-new-exam:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.btn-help {
  width: 100%;
  background: white;
  color: #1e40af;
  border: none;
  padding: 14px;
  border-radius: 10px;
  font-weight: 700;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.btn-help:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.main-content {
  margin-left: 280px;
  flex: 1;
  padding: 35px 40px;
}

.top-header {
  margin-bottom: 35px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.btn-back {
  padding: 10px 20px;
  background: white;
  color: #1e40af;
  border: 2px solid #1e40af;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-back:hover {
  background: #1e40af;
  color: white;
}

.header-left h1 {
  font-size: 32px;
  color: #1f2937;
  font-weight: 800;
  margin: 0;
}

.result-summary {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  padding: 35px;
  margin-bottom: 30px;
}

.summary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.summary-header h2 {
  font-size: 28px;
  color: #1f2937;
  margin: 0;
  font-weight: 700;
}

.exam-date {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.summary-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 25px;
}

.stat-box {
  background: #f9fafb;
  padding: 25px;
  border-radius: 12px;
  text-align: center;
  transition: all 0.3s ease;
}

.stat-box:hover {
  background: #f3f4f6;
  transform: translateY(-2px);
}

.stat-box.primary {
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
  font-weight: 600;
  margin-bottom: 10px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-value {
  font-size: 36px;
  font-weight: 800;
  color: #1f2937;
  margin-bottom: 5px;
}

.stat-value.excellent {
  color: #10b981;
}

.stat-value.good {
  color: #3b82f6;
}

.stat-value.average {
  color: #f59e0b;
}

.stat-value.poor {
  color: #ef4444;
}

.stat-sublabel {
  font-size: 13px;
  color: #9ca3af;
  font-weight: 500;
}

.questions-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  padding: 35px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.section-header h3 {
  font-size: 24px;
  color: #1f2937;
  margin: 0;
  font-weight: 700;
}

.legend {
  display: flex;
  gap: 20px;
}

.legend-item {
  font-size: 14px;
  font-weight: 600;
  padding: 6px 14px;
  border-radius: 20px;
}

.legend-item.correct {
  background: #d1fae5;
  color: #065f46;
}

.legend-item.incorrect {
  background: #fee2e2;
  color: #991b1b;
}

.questions-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.question-card {
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  padding: 25px;
  transition: all 0.3s ease;
}

.question-card.correct {
  border-color: #10b981;
  background: linear-gradient(to right, #f0fdf4 0%, #ffffff 100%);
}

.question-card.incorrect {
  border-color: #ef4444;
  background: linear-gradient(to right, #fef2f2 0%, #ffffff 100%);
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.question-number {
  font-size: 16px;
  font-weight: 700;
  color: #1f2937;
  background: #f3f4f6;
  padding: 8px 16px;
  border-radius: 8px;
}

.question-status {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 700;
  text-transform: uppercase;
}

.status-correct {
  background: #d1fae5;
  color: #065f46;
}

.status-incorrect {
  background: #fee2e2;
  color: #991b1b;
}

.question-content {
  margin-bottom: 20px;
}

.question-text {
  font-size: 16px;
  color: #1f2937;
  line-height: 1.7;
  font-weight: 500;
  margin: 0;
}

.question-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 14px 18px;
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.option-item.correct-answer {
  border-color: #10b981;
  background: #f0fdf4;
}

.option-item.wrong-selected {
  border-color: #ef4444;
  background: #fef2f2;
}

.option-item.selected:not(.wrong-selected) {
  border-color: #10b981;
  background: #f0fdf4;
}

.option-label {
  width: 32px;
  height: 32px;
  background: #f3f4f6;
  color: #1f2937;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
  flex-shrink: 0;
}

.option-item.correct-answer .option-label {
  background: #10b981;
  color: white;
}

.option-item.wrong-selected .option-label {
  background: #ef4444;
  color: white;
}

.option-text {
  flex: 1;
  font-size: 15px;
  color: #374151;
  line-height: 1.6;
}

.question-footer {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.answer-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.info-label {
  font-size: 14px;
  color: #6b7280;
  font-weight: 600;
}

.info-value {
  font-size: 15px;
  font-weight: 700;
  padding: 4px 12px;
  border-radius: 6px;
}

.correct-text {
  color: #065f46;
  background: #d1fae5;
}

.incorrect-text {
  color: #991b1b;
  background: #fee2e2;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
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
  to {
    transform: rotate(360deg);
  }
}

.loading-state p {
  color: #6b7280;
  font-size: 16px;
  margin: 0;
}

.empty-state h3 {
  font-size: 24px;
  color: #1f2937;
  margin: 0 0 10px 0;
  font-weight: 700;
}

.empty-state p {
  font-size: 16px;
  color: #6b7280;
  margin: 0 0 25px 0;
}

.btn-start {
  padding: 14px 32px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 700;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-start:hover {
  background: #1e3a8a;
  transform: translateY(-2px);
}

@media (max-width: 1200px) {
  .summary-stats {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
  }
  .main-content {
    margin-left: 0;
    padding: 20px;
  }
  .summary-stats {
    grid-template-columns: 1fr;
  }
  .header-left {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
