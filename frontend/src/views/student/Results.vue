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
          <h1>Kết Quả Thi</h1>
        </div>
        <div class="header-right">
          <div class="user-menu">
            <span class="user-name">{{ authStore.user?.fullName }}</span>
            <button class="btn-logout-small" @click="handleLogout">Đăng xuất</button>
          </div>
        </div>
      </header>

      <div class="stats-overview">
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ results.length }}</div>
            <div class="stat-label">Tổng số bài thi</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ passedCount }}</div>
            <div class="stat-label">Bài đạt yêu cầu</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ averageScore }}%</div>
            <div class="stat-label">Điểm trung bình</div>
          </div>
        </div>
      </div>

      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Đang tải kết quả...</p>
      </div>

      <div v-else-if="results.length === 0" class="empty-state">
        <h3>Chưa có kết quả thi</h3>
        <p>Bạn chưa hoàn thành bài thi nào. Hãy bắt đầu làm bài thi đầu tiên!</p>
        <button class="btn-start" @click="$router.push('/student/exams')">Xem đề thi</button>
      </div>

      <div v-else class="results-container">
        <div class="results-table">
          <div class="table-header">
            <div class="col-exam">TÊN ĐỀ THI</div>
            <div class="col-date">NGÀY THI</div>
            <div class="col-score">ĐIỂM SỐ</div>
            <div class="col-status">TRẠNG THÁI</div>
            <div class="col-action">HÀNH ĐỘNG</div>
          </div>
          <div class="table-body">
            <div v-for="result in results" :key="result.id" class="table-row">
              <div class="col-exam">
                <div class="exam-info">
                  <div class="exam-title">{{ result.examName }}</div>
                  <div class="exam-meta">{{ result.totalQuestions }} câu • {{ Math.floor(result.timeSpent / 60) }} phút</div>
                </div>
              </div>
              <div class="col-date">
                <div class="date-text">{{ formatDateShort(result.submitTime) }}</div>
              </div>
              <div class="col-score">
                <div class="score-display">
                  <div class="score-bar" :class="getScoreClass(result.score)">
                    <div class="score-fill" :style="{ width: result.score + '%' }"></div>
                  </div>
                  <div class="score-text" :class="getScoreClass(result.score)">{{ result.score }}/100</div>
                </div>
              </div>
              <div class="col-status">
                <span class="status-badge" :class="result.status === 'graded' ? 'status-completed' : 'status-pending'">
                  {{ result.status === 'graded' ? 'Hoàn thành' : 'Đang chấm' }}
                </span>
              </div>
              <div class="col-action">
                <button v-if="result.status === 'graded'" 
                        class="btn-view" 
                        @click="viewResult(result.id)">
                  Xem kết quả
                </button>
                <button v-else class="btn-view disabled" disabled>
                  N/A
                </button>
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
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore'
import resultService from '../../services/resultService'

const router = useRouter()
const authStore = useAuthStore()

const results = ref([])
const loading = ref(false)

const passedCount = computed(() => {
  return results.value.filter(r => r.score >= 50).length
})

const averageScore = computed(() => {
  if (results.value.length === 0) return 0
  const total = results.value.reduce((sum, r) => sum + parseFloat(r.score), 0)
  return Math.round(total / results.value.length)
})

onMounted(() => {
  loadResults()
})

const loadResults = async () => {
  try {
    loading.value = true
    const response = await resultService.getMyResults()
    if (response.success) {
      results.value = response.data
    }
  } catch (error) {
    console.error('Error loading results:', error)
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

const formatDateShort = (dateString) => {
  const date = new Date(dateString)
  const month = date.toLocaleDateString('vi-VN', { month: 'short' })
  const day = date.getDate()
  const year = date.getFullYear()
  return `${month} ${day}, ${year}`
}

const viewResult = (resultId) => {
  router.push(`/student/result/${resultId}`)
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 35px;
}

.header-left h1 {
  font-size: 32px;
  color: #1f2937;
  font-weight: 800;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 16px;
  background: white;
  padding: 10px 18px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.user-name {
  font-size: 14px;
  color: #1f2937;
  font-weight: 600;
}

.btn-logout-small {
  padding: 8px 16px;
  background: #fee2e2;
  color: #dc2626;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
}

.btn-logout-small:hover {
  background: #fecaca;
}

.stats-overview {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 25px;
  margin-bottom: 35px;
}

.stat-card {
  background: white;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 800;
  color: #1e40af;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.results-container {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  overflow: hidden;
}

.results-table {
  width: 100%;
}

.table-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1.5fr 1fr 1fr;
  gap: 20px;
  padding: 20px 30px;
  background: #f9fafb;
  border-bottom: 2px solid #e5e7eb;
  font-size: 12px;
  font-weight: 700;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.table-body {
  display: flex;
  flex-direction: column;
}

.table-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1.5fr 1fr 1fr;
  gap: 20px;
  padding: 25px 30px;
  border-bottom: 1px solid #f0f0f0;
  align-items: center;
  transition: all 0.2s ease;
}

.table-row:hover {
  background: #f9fafb;
}

.table-row:last-child {
  border-bottom: none;
}

.col-exam {
  display: flex;
  align-items: center;
  gap: 15px;
}

.exam-info {
  flex: 1;
}

.exam-title {
  font-size: 16px;
  color: #1f2937;
  font-weight: 600;
  margin-bottom: 4px;
}

.exam-meta {
  font-size: 13px;
  color: #6b7280;
}

.col-date {
  display: flex;
  align-items: center;
}

.date-text {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.col-score {
  display: flex;
  align-items: center;
}

.score-display {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.score-bar {
  height: 8px;
  background: #e5e7eb;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
}

.score-fill {
  height: 100%;
  border-radius: 10px;
  transition: width 0.3s ease;
}

.score-bar.excellent .score-fill {
  background: #10b981;
}

.score-bar.good .score-fill {
  background: #3b82f6;
}

.score-bar.average .score-fill {
  background: #f59e0b;
}

.score-bar.poor .score-fill {
  background: #ef4444;
}

.score-text {
  font-size: 15px;
  font-weight: 700;
}

.score-text.excellent {
  color: #10b981;
}

.score-text.good {
  color: #3b82f6;
}

.score-text.average {
  color: #f59e0b;
}

.score-text.poor {
  color: #ef4444;
}

.col-status {
  display: flex;
  align-items: center;
}

.status-badge {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  display: inline-block;
}

.status-completed {
  background: #d1fae5;
  color: #065f46;
}

.status-pending {
  background: #dbeafe;
  color: #1e40af;
}

.col-action {
  display: flex;
  align-items: center;
}

.btn-view {
  padding: 10px 20px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.btn-view:hover {
  background: #1e3a8a;
  transform: translateY(-1px);
}

.btn-view.disabled {
  background: #e5e7eb;
  color: #9ca3af;
  cursor: not-allowed;
}

.btn-view.disabled:hover {
  transform: none;
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

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  display: none;
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

@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
  }
  .main-content {
    margin-left: 0;
    padding: 20px;
  }
  .stats-overview {
    grid-template-columns: 1fr;
  }
  .table-header {
    display: none;
  }
  .table-row {
    grid-template-columns: 1fr;
    gap: 15px;
    padding: 20px;
  }
  .col-exam,
  .col-date,
  .col-score,
  .col-status,
  .col-action {
    width: 100%;
  }
  .btn-view {
    width: 100%;
  }
}
</style>
