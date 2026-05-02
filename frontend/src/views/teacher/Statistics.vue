<template>
  <TeacherLayout activeMenu="exams">
    <LoadingSpinner v-if="loading" />

    <EmptyState 
      v-else-if="!statistics"
      message="Không tìm thấy thống kê"
      actionText="Quay lại danh sách đề thi"
      @action="$router.push('/teacher/exams')"
    />

    <div v-else>
      <header class="top-header">
        <div class="header-left">
          <h1>{{ statistics.examName }}</h1>
          <p class="exam-description">{{ statistics.examDescription }}</p>
        </div>
        <button class="btn-back" @click="$router.push('/teacher/exams')">Quay lại</button>
      </header>

      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-info">
            <h3>{{ statistics.totalStudents }}</h3>
            <p>Học sinh đã thi</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-info">
            <h3>{{ convertToScore10(statistics.averageScore) }}</h3>
            <p>Điểm trung bình</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-info">
            <h3>{{ convertToScore10(statistics.highestScore) }}</h3>
            <p>Điểm cao nhất</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-info">
            <h3>{{ convertToScore10(statistics.lowestScore) }}</h3>
            <p>Điểm thấp nhất</p>
          </div>
        </div>
      </div>

      <div class="section-card">
        <h2>Phân Bố Điểm</h2>
        <div class="score-distribution">
          <div v-for="range in scoreRanges" :key="range.label" class="distribution-item">
            <div class="distribution-count">{{ range.count }}</div>
            <div class="distribution-label">{{ range.label }}</div>
            <div class="distribution-bar">
              <div class="distribution-fill" :style="{ width: getDistributionWidth(range.count) + '%' }"></div>
            </div>
          </div>
        </div>
      </div>

      <div class="section-card">
        <h2>Danh Sách Kết Quả</h2>
        <div v-if="!statistics.results || statistics.results.length === 0" class="empty-message">
          Chưa có học sinh nào thi
        </div>
        <div v-else class="results-table">
          <table>
            <thead>
              <tr>
                <th>Học sinh</th>
                <th>Email</th>
                <th>Điểm</th>
                <th>Số câu đúng</th>
                <th>Thời gian làm</th>
                <th>Ngày thi</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="result in statistics.results" :key="result.id">
                <td class="student-name">
                  <a href="#" @click.prevent="viewResultDetail(result.id)" class="student-link">
                    {{ result.studentName }}
                  </a>
                </td>
                <td class="student-email">{{ result.studentEmail }}</td>
                <td>
                  <span class="score-badge" :style="{ background: getScoreBadgeColor(result.score) }">
                    {{ convertToScore10(result.score) }}
                  </span>
                </td>
                <td>{{ result.correctAnswers }}/{{ result.totalQuestions }}</td>
                <td>{{ formatTimeSpent(result.timeSpent) }}</td>
                <td>{{ formatDateTime(result.submitTime) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="section-card">
        <h2>Phân Tích Câu Hỏi</h2>
        <div v-if="!statistics.questionAnalysis || statistics.questionAnalysis.length === 0" class="empty-message">
          Chưa có dữ liệu phân tích
        </div>
        <div v-else class="questions-analysis">
          <div v-for="question in statistics.questionAnalysis" :key="question.questionId" class="analysis-item">
            <div class="analysis-header">
              <p class="question-content">
                <MathText :text="question.content" :auto-wrap="true" />
              </p>
              <div class="analysis-percentage">{{ question.correctPercentage }}%</div>
            </div>
            <div class="analysis-details">
              <span class="detail-badge answer-badge">Đáp án: {{ question.correctAnswer }}</span>
              <span class="detail-badge count-badge">{{ question.correctCount }}/{{ question.totalAnswers }} đúng</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: question.correctPercentage + '%', background: getProgressColor(question.correctPercentage) }"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </TeacherLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useFormatters } from '../../composables/useFormatters'
import resultService from '../../services/resultService'
import TeacherLayout from '../../components/layouts/TeacherLayout.vue'
import LoadingSpinner from '../../components/common/LoadingSpinner.vue'
import EmptyState from '../../components/common/EmptyState.vue'
import MathText from '../../components/common/MathText.vue'

const route = useRoute()
const router = useRouter()
const { formatDateTime, formatTimeSpent } = useFormatters()

const statistics = ref(null)
const loading = ref(false)

const scoreRanges = computed(() => {
  if (!statistics.value || !statistics.value.results) return []
  
  const ranges = [
    { label: '9-10', min: 90, max: 100, count: 0 },
    { label: '7-8', min: 70, max: 89, count: 0 },
    { label: '5-6', min: 50, max: 69, count: 0 },
    { label: '0-4', min: 0, max: 49, count: 0 }
  ]
  
  statistics.value.results.forEach(result => {
    const score = parseFloat(result.score)
    ranges.forEach(range => {
      if (score >= range.min && score <= range.max) {
        range.count++
      }
    })
  })
  
  return ranges
})

onMounted(() => {
  loadStatistics()
})

const loadStatistics = async () => {
  try {
    loading.value = true
    const examId = route.params.id
    const response = await resultService.getExamStatistics(examId)
    if (response.success) {
      statistics.value = response.data
    }
  } catch (error) {
    console.error('Error loading statistics:', error)
  } finally {
    loading.value = false
  }
}

const getDistributionWidth = (count) => {
  if (!statistics.value || !statistics.value.totalStudents) return 0
  return (count / statistics.value.totalStudents) * 100
}

const getScoreBadgeColor = (score) => {
  const s = parseFloat(score)
  if (s >= 80) return '#10b981'
  if (s >= 65) return '#3b82f6'
  if (s >= 50) return '#f59e0b'
  return '#ef4444'
}

const convertToScore10 = (score) => {
  return (parseFloat(score) / 10).toFixed(1)
}

const getProgressColor = (percentage) => {
  if (percentage >= 70) return '#10b981'
  if (percentage >= 50) return '#f59e0b'
  return '#ef4444'
}

const viewResultDetail = (resultId) => {
  router.push(`/teacher/result/${resultId}`)
}
</script>

<style scoped>
.exam-description {
  color: #6b7280;
  font-size: 14px;
  margin: 8px 0 0 0;
}

.btn-back {
  padding: 10px 20px;
  background: #f3f4f6;
  color: #374151;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  text-align: center;
}

.stat-info h3 {
  font-size: 48px;
  color: #1e40af;
  margin: 0 0 10px 0;
  font-weight: 800;
}

.stat-info p {
  font-size: 16px;
  color: #6b7280;
  margin: 0;
  font-weight: 500;
}

.section-card {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-bottom: 25px;
}

.section-card h2 {
  font-size: 20px;
  color: #1f2937;
  margin: 0 0 20px 0;
  font-weight: 700;
}

.empty-message {
  text-align: center;
  padding: 40px;
  color: #6b7280;
}

.score-distribution {
  display: grid;
  gap: 15px;
}

.distribution-item {
  display: grid;
  grid-template-columns: 60px 80px 1fr;
  align-items: center;
  gap: 15px;
}

.distribution-count {
  font-size: 24px;
  font-weight: 800;
  color: #1e40af;
  text-align: center;
}

.distribution-label {
  font-size: 14px;
  color: #374151;
  font-weight: 600;
}

.distribution-bar {
  height: 30px;
  background: #f3f4f6;
  border-radius: 8px;
  overflow: hidden;
}

.distribution-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6 0%, #1e40af 100%);
  transition: width 0.5s ease;
}

.results-table table {
  width: 100%;
  border-collapse: collapse;
}

.results-table th {
  text-align: left;
  padding: 12px;
  background: #f9fafb;
  color: #374151;
  font-weight: 600;
  font-size: 14px;
  border-bottom: 2px solid #e5e7eb;
}

.results-table td {
  padding: 15px 12px;
  border-bottom: 1px solid #f3f4f6;
  color: #6b7280;
  font-size: 14px;
}

.student-name {
  color: #1f2937;
  font-weight: 600;
}

.student-link {
  color: #1e40af;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s;
}

.student-link:hover {
  color: #3b82f6;
  text-decoration: underline;
}

.student-email {
  color: #6b7280;
  font-size: 13px;
}

.score-badge {
  display: inline-block;
  padding: 6px 14px;
  color: white;
  border-radius: 20px;
  font-weight: 700;
  font-size: 14px;
}

.questions-analysis {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.analysis-item {
  padding: 20px;
  background: #f9fafb;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
}

.analysis-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 12px;
}

.question-content {
  flex: 1;
  font-size: 15px;
  color: #1f2937;
  font-weight: 600;
  margin: 0;
  line-height: 1.6;
}

.analysis-percentage {
  font-size: 24px;
  font-weight: 800;
  color: #1e40af;
  margin-left: 20px;
}

.analysis-details {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}

.detail-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.answer-badge {
  background: #dbeafe;
  color: #1e40af;
}

.count-badge {
  background: #f3f4f6;
  color: #374151;
}

.progress-bar {
  height: 8px;
  background: #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  transition: width 0.5s ease;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
