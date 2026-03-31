<template>
  <div class="dashboard-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <img src="/logo/logo.png" alt="Logo" class="sidebar-logo" />
        <h2>Hệ Thống Thi</h2>
        <p>Giáo viên</p>
      </div>
      <nav class="sidebar-menu">
        <a href="#" @click.prevent="$router.push('/teacher')" class="menu-item">
          Trang chủ
        </a>
        <a href="#" @click.prevent="$router.push('/teacher/exams')" class="menu-item">
          Đề thi
        </a>
        <a href="#" @click.prevent="$router.push('/teacher/questions')" class="menu-item">
          Câu hỏi
        </a>
        <a href="#" @click.prevent="$router.push('/teacher/profile')" class="menu-item">
          Cài đặt
        </a>
      </nav>
      <div class="sidebar-footer">
        <button class="btn-logout" @click="$router.push('/teacher/exams')">Quay lại</button>
      </div>
    </aside>
    <div class="main-content">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Đang tải thống kê...</p>
      </div>
      <div v-else-if="!statistics" class="empty-state">
        <p>Không tìm thấy thống kê</p>
        <button class="btn-back" @click="$router.push('/teacher/exams')">Quay lại danh sách đề thi</button>
      </div>
      <div v-else>
        <header class="top-header">
          <div class="header-left">
            <h1>{{ statistics.examName }}</h1>
            <p class="exam-description">{{ statistics.examDescription }}</p>
          </div>
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
              <h3>{{ statistics.averageScore }}%</h3>
              <p>Điểm trung bình</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-info">
              <h3>{{ statistics.highestScore }}%</h3>
              <p>Điểm cao nhất</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-info">
              <h3>{{ statistics.lowestScore }}%</h3>
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
                  <td class="student-name">{{ result.studentName }}</td>
                  <td class="student-email">{{ result.studentEmail }}</td>
                  <td>
                    <span class="score-badge" :style="{ background: getScoreBadgeColor(result.score) }">
                      {{ convertToScore10(result.score) }}
                    </span>
                  </td>
                  <td>{{ result.correctAnswers }}/{{ result.totalQuestions }}</td>
                  <td>{{ formatTimeSpent(result.timeSpent) }}</td>
                  <td>{{ formatDate(result.submitTime) }}</td>
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
                <p class="question-content">{{ question.content }}</p>
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
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import examService from '../../services/examService'

const route = useRoute()

const statistics = ref(null)
const loading = ref(false)

const scoreRanges = computed(() => {
  if (!statistics.value || !statistics.value.results) return []
  
  const ranges = [
    { label: '0-20%', min: 0, max: 20, count: 0 },
    { label: '21-40%', min: 21, max: 40, count: 0 },
    { label: '41-60%', min: 41, max: 60, count: 0 },
    { label: '61-80%', min: 61, max: 80, count: 0 },
    { label: '81-100%', min: 81, max: 100, count: 0 }
  ]

  statistics.value.results.forEach(result => {
    const score = parseFloat(result.score)
    const range = ranges.find(r => score >= r.min && score <= r.max)
    if (range) range.count++
  })

  return ranges
})

onMounted(() => {
  loadStatistics()
})

const loadStatistics = async () => {
  try {
    loading.value = true
    const response = await examService.getExamStatistics(route.params.id)
    if (response.success) {
      statistics.value = response.data
    }
  } catch (error) {
    console.error('Error loading statistics:', error)
  } finally {
    loading.value = false
  }
}

const getScoreColor = (score) => {
  if (score >= 80) return '#10b981'
  if (score >= 50) return '#f59e0b'
  return '#ef4444'
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN')
}

const getDistributionWidth = (count) => {
  if (!statistics.value || !statistics.value.totalStudents) return 0
  return (count / statistics.value.totalStudents) * 100
}

const getScoreBadgeColor = (score) => {
  if (score >= 80) return '#d1fae5'
  if (score >= 50) return '#fef3c7'
  return '#fee2e2'
}

const getProgressColor = (percentage) => {
  if (percentage >= 80) return '#10b981'
  if (percentage >= 50) return '#f59e0b'
  return '#ef4444'
}

const convertToScore10 = (percentage) => {
  if (!percentage) return '0.0'
  const score = (parseFloat(percentage) / 10).toFixed(1)
  return score
}

const formatTimeSpent = (seconds) => {
  if (!seconds || seconds === 0) return 'Chưa có dữ liệu'
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  if (minutes === 0) return `${secs} giây`
  if (secs === 0) return `${minutes} phút`
  return `${minutes} phút ${secs} giây`
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
  color: white;
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  overflow-y: auto;
}

.sidebar-header {
  padding: 35px 25px;
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
  padding: 14px 25px;
  color: rgba(255,255,255,0.8);
  text-decoration: none;
  transition: all 0.3s;
  font-weight: 500;
  border-radius: 10px;
  margin-bottom: 6px;
}

.menu-item:hover {
  background: rgba(255,255,255,0.1);
  color: white;
}

.menu-item.active {
  background: #ffffff;
  color: #1e40af;
  font-weight: 600;
}

.sidebar-footer {
  padding: 20px 25px;
  border-top: 1px solid rgba(255,255,255,0.1);
}

.btn-logout {
  width: 100%;
  padding: 12px;
  background: rgba(255,255,255,0.1);
  color: white;
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-logout:hover {
  background: rgba(255,255,255,0.2);
}

.main-content {
  margin-left: 280px;
  flex: 1;
  padding: 30px;
}

.loading-state, .empty-state {
  background: white;
  padding: 60px 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  text-align: center;
  margin-top: 50px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e5e7eb;
  border-top-color: #1e40af;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 15px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-state p {
  color: #6b7280;
  margin-bottom: 20px;
  font-size: 16px;
}

.btn-back {
  padding: 12px 24px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-back:hover {
  background: #1e3a8a;
}

.top-header {
  background: white;
  padding: 25px 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-bottom: 25px;
}

.header-left h1 {
  font-size: 28px;
  color: #1f2937;
  margin: 0 0 8px 0;
  font-weight: 800;
}

.exam-description {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
  line-height: 1.5;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 25px;
}

.stat-card {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  text-align: center;
}

.stat-info h3 {
  font-size: 42px;
  color: #1e40af;
  margin: 0 0 10px 0;
  font-weight: 800;
}

.stat-info p {
  font-size: 15px;
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
  margin: 0 0 25px 0;
  font-weight: 700;
}

.empty-message {
  text-align: center;
  padding: 40px 20px;
  color: #6b7280;
  font-size: 15px;
}

.score-distribution {
  display: grid;
  gap: 20px;
}

.distribution-item {
  display: grid;
  grid-template-columns: 80px 120px 1fr;
  align-items: center;
  gap: 15px;
}

.distribution-count {
  font-size: 32px;
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
  border-radius: 15px;
  overflow: hidden;
}

.distribution-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6 0%, #1e40af 100%);
  transition: width 0.5s ease;
}

.results-table {
  overflow-x: auto;
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

.student-email {
  color: #6b7280;
  font-size: 13px;
}

.score-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 700;
  color: #1f2937;
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
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 15px;
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
  font-size: 28px;
  font-weight: 800;
  color: #1e40af;
  flex-shrink: 0;
}

.analysis-details {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}

.detail-badge {
  padding: 4px 12px;
  border-radius: 20px;
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
  border-radius: 4px;
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
  
  .distribution-item {
    grid-template-columns: 60px 100px 1fr;
  }
}
</style>
