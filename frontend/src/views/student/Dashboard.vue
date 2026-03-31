<template>
  <div class="dashboard-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <img src="/logo/logo.png" alt="Logo" class="sidebar-logo" />
        <h2>Hệ Thống Thi</h2>
        <p>Học sinh</p>
      </div>
      <nav class="sidebar-menu">
        <a href="#" @click.prevent="$router.push('/student')" class="menu-item active">
          Trang chủ
        </a>
        <a href="#" @click.prevent="$router.push('/student/exams')" class="menu-item">
          Đề thi
        </a>
        <a href="#" @click.prevent="$router.push('/student/results')" class="menu-item">
          Kết quả
        </a>
        <a href="#" @click.prevent="$router.push('/student/profile')" class="menu-item">
          Cài đặt
        </a>
      </nav>
      <div class="sidebar-footer">
        <button class="btn-help" @click="requestTeacher">Trở thành giáo viên</button>
      </div>
    </aside>
    <div class="main-content">
      <header class="top-header">
        <div class="header-left">
          <h1>Trang chủ</h1>
        </div>
        <div class="header-right">
          <input type="text" placeholder="Tìm kiếm đề thi..." class="search-input">
          <div class="user-menu">
            <span class="user-name">{{ authStore.user?.fullName }}</span>
            <button class="btn-logout-small" @click="handleLogout">Đăng xuất</button>
          </div>
        </div>
      </header>
      <div class="welcome-section">
        <h2>Chào mừng đến với Hệ Thống Thi Trực Tuyến!</h2>
        <p>Nền tảng thi trắc nghiệm hiện đại, giúp bạn học tập và kiểm tra kiến thức một cách hiệu quả</p>
      </div>

      <div class="intro-section">
        <div class="intro-card">
          <div class="intro-number">01</div>
          <h3>Thi Trực Tuyến</h3>
          <p>Làm bài thi mọi lúc mọi nơi với giao diện thân thiện và dễ sử dụng. Hệ thống tự động chấm điểm và thống kê kết quả chi tiết.</p>
        </div>
        <div class="intro-card">
          <div class="intro-number">02</div>
          <h3>Ngân Hàng Đề Thi</h3>
          <p>Kho đề thi phong phú với nhiều môn học và độ khó khác nhau. Giáo viên liên tục cập nhật đề thi mới để bạn luyện tập.</p>
        </div>
        <div class="intro-card">
          <div class="intro-number">03</div>
          <h3>Theo Dõi Tiến Độ</h3>
          <p>Xem lại kết quả các bài thi đã làm, phân tích điểm mạnh điểm yếu để cải thiện kết quả học tập của bạn.</p>
        </div>
      </div>
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-header">
            <span class="stat-badge">Tổng số</span>
          </div>
          <div class="stat-value">{{ stats.availableExams }}</div>
          <div class="stat-label">Đề thi khả dụng</div>
        </div>
        <div class="stat-card">
          <div class="stat-header">
            <span class="stat-badge success">Hoàn thành</span>
          </div>
          <div class="stat-value">{{ stats.completedExams }}</div>
          <div class="stat-label">Đã hoàn thành</div>
        </div>
        <div class="stat-card">
          <div class="stat-header">
            <span class="stat-badge warning">Điểm số</span>
          </div>
          <div class="stat-value">{{ stats.averageScore }}%</div>
          <div class="stat-label">Điểm trung bình</div>
        </div>
      </div>
      <div class="content-grid">
        <div class="content-main">
          <div class="section-title">
            <h3>Hướng Dẫn Sử Dụng</h3>
          </div>
          <div class="guide-list">
            <div class="guide-item">
              <div class="guide-step">Bước 1</div>
              <div class="guide-content">
                <h4>Chọn Đề Thi</h4>
                <p>Vào mục "Đề thi" để xem danh sách các đề thi khả dụng. Chọn đề thi phù hợp với môn học và độ khó bạn muốn luyện tập.</p>
              </div>
            </div>
            <div class="guide-item">
              <div class="guide-step">Bước 2</div>
              <div class="guide-content">
                <h4>Làm Bài Thi</h4>
                <p>Đọc kỹ câu hỏi và chọn đáp án đúng. Hệ thống sẽ tự động lưu câu trả lời của bạn. Lưu ý thời gian làm bài.</p>
              </div>
            </div>
            <div class="guide-item">
              <div class="guide-step">Bước 3</div>
              <div class="guide-content">
                <h4>Xem Kết Quả</h4>
                <p>Sau khi nộp bài, vào mục "Kết quả" để xem điểm số và đáp án chi tiết. Phân tích để cải thiện kết quả lần sau.</p>
              </div>
            </div>
          </div>

          <div class="section-title" style="margin-top: 40px;">
            <h3>Đề Thi Gần Đây</h3>
            <a href="#" @click.prevent="$router.push('/student/exams')" class="view-all">Xem tất cả</a>
          </div>
          <div v-if="loading" class="loading-state">
            <div class="spinner"></div>
            <p>Đang tải...</p>
          </div>
          <div v-else-if="exams.length === 0" class="empty-state">
            <p>Chưa có đề thi nào khả dụng</p>
          </div>
          <div v-else class="exam-list">
            <div v-for="exam in exams.slice(0, 3)" :key="exam.id" class="exam-item">
              <div class="exam-icon">
                <div class="icon-placeholder">{{ exam.subject.charAt(0) }}</div>
              </div>
              <div class="exam-info">
                <h4>{{ exam.name }}</h4>
                <p class="exam-meta">{{ exam.subject }} • {{ exam.totalQuestions }} câu • {{ exam.duration }} phút</p>
              </div>
              <div class="exam-actions">
                <button class="btn-details" @click="startExam(exam.id)">Bắt đầu</button>
              </div>
            </div>
          </div>
        </div>
        <div class="content-sidebar">
          <div class="user-card">
            <div class="user-avatar">{{ authStore.user?.fullName?.charAt(0) }}</div>
            <h4>{{ authStore.user?.fullName }}</h4>
            <p class="user-email">{{ authStore.user?.email }}</p>
            <div class="user-stats-mini">
              <div class="stat-mini">
                <div class="stat-mini-value">{{ stats.completedExams }}</div>
                <div class="stat-mini-label">Bài đã thi</div>
              </div>
              <div class="stat-mini">
                <div class="stat-mini-value">{{ stats.averageScore }}%</div>
                <div class="stat-mini-label">Điểm TB</div>
              </div>
            </div>
          </div>
          <div class="tip-card">
            <h4>Mẹo Học Tập</h4>
            <p>Học sinh ôn tập trước khi thi 48 giờ có điểm số cao hơn 15% so với trung bình. Hãy thử ngay!</p>
            <button class="btn-tip" @click="$router.push('/student/exams')">Bắt đầu luyện tập</button>
          </div>
          <div class="activity-card">
            <h4>Hoạt Động Gần Đây</h4>
            <div v-if="loadingResults" class="loading-state-small">
              <div class="spinner-small"></div>
            </div>
            <div v-else-if="results.length === 0" class="empty-state-small">
              <p>Chưa có hoạt động nào</p>
            </div>
            <div v-else class="activity-list">
              <div v-for="result in results.slice(0, 3)" :key="result.id" class="activity-item">
                <div class="activity-icon">
                  <span :class="getActivityClass(result.score)">•</span>
                </div>
                <div class="activity-info">
                  <p class="activity-title">{{ result.examName }}</p>
                  <p class="activity-time">{{ formatTimeAgo(result.submitTime) }}</p>
                </div>
                <div class="activity-score">
                  <span :class="getScoreClass(result.score)">{{ result.score }}%</span>
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
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore'
import examService from '../../services/examService'
import resultService from '../../services/resultService'

const router = useRouter()
const authStore = useAuthStore()
const exams = ref([])
const results = ref([])
const loading = ref(false)
const loadingResults = ref(false)
const stats = ref({
  availableExams: 0,
  completedExams: 0,
  averageScore: 0
})

onMounted(() => {
  loadExams()
  loadResults()
})

const loadExams = async () => {
  try {
    loading.value = true
    const response = await examService.getActiveExams()
    if (response.success) {
      exams.value = response.data
      stats.value.availableExams = exams.value.length
    }
  } catch (error) {
    console.error('Error loading exams:', error)
  } finally {
    loading.value = false
  }
}

const loadResults = async () => {
  try {
    loadingResults.value = true
    const response = await resultService.getMyResults()
    if (response.success) {
      results.value = response.data.filter(r => r.status === 'graded').slice(0, 5)
      stats.value.completedExams = response.data.filter(r => r.status === 'graded').length
      if (results.value.length > 0) {
        const total = results.value.reduce((sum, r) => sum + parseFloat(r.score), 0)
        stats.value.averageScore = Math.round(total / results.value.length)
      }
    }
  } catch (error) {
    console.error('Error loading results:', error)
  } finally {
    loadingResults.value = false
  }
}

const startExam = (examId) => {
  router.push(`/student/exam/${examId}`)
}

const requestTeacher = () => {
  router.push('/student/teacher-request')
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

const getScoreClass = (score) => {
  if (score >= 80) return 'excellent'
  if (score >= 65) return 'good'
  if (score >= 50) return 'average'
  return 'poor'
}

const getActivityClass = (score) => {
  if (score >= 80) return 'activity-success'
  if (score >= 50) return 'activity-warning'
  return 'activity-danger'
}

const formatTimeAgo = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = Math.floor((now - date) / 1000)
  if (diff < 60) return 'Vừa xong'
  if (diff < 3600) return `${Math.floor(diff / 60)} phút trước`
  if (diff < 86400) return `${Math.floor(diff / 3600)} giờ trước`
  return `${Math.floor(diff / 86400)} ngày trước`
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

.search-input {
  padding: 12px 18px;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  width: 320px;
  font-size: 14px;
}

.search-input:focus {
  outline: none;
  border-color: #1e40af;
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

.welcome-section {
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%);
  padding: 50px 40px;
  border-radius: 16px;
  margin-bottom: 30px;
  color: white;
  text-align: center;
}

.welcome-section h2 {
  font-size: 32px;
  margin: 0 0 12px 0;
  font-weight: 700;
}

.welcome-section p {
  font-size: 18px;
  margin: 0;
  opacity: 0.95;
}

.intro-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 25px;
  margin-bottom: 35px;
}

.intro-card {
  background: white;
  padding: 35px 30px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.intro-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.intro-number {
  font-size: 48px;
  font-weight: 800;
  color: #eff6ff;
  position: absolute;
  top: 20px;
  right: 20px;
  line-height: 1;
}

.intro-card h3 {
  font-size: 20px;
  color: #1f2937;
  margin: 0 0 12px 0;
  font-weight: 700;
}

.intro-card p {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.7;
  margin: 0;
}

.guide-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 30px;
}

.guide-item {
  display: flex;
  gap: 20px;
  padding: 25px;
  background: #f9fafb;
  border-radius: 12px;
  border: 2px solid #f0f0f0;
  transition: all 0.3s ease;
}

.guide-item:hover {
  background: white;
  border-color: #1e40af;
}

.guide-step {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%);
  color: white;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  flex-shrink: 0;
}

.guide-content {
  flex: 1;
}

.guide-content h4 {
  font-size: 18px;
  color: #1f2937;
  margin: 0 0 8px 0;
  font-weight: 700;
}

.guide-content p {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.7;
  margin: 0;
}

.user-card {
  background: white;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  text-align: center;
  margin-bottom: 25px;
}

.user-avatar {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 700;
  margin: 0 auto 15px;
}

.user-card h4 {
  font-size: 18px;
  color: #1f2937;
  margin: 0 0 5px 0;
  font-weight: 700;
}

.user-email {
  font-size: 13px;
  color: #6b7280;
  margin: 0 0 20px 0;
}

.user-stats-mini {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.stat-mini {
  text-align: center;
}

.stat-mini-value {
  font-size: 24px;
  font-weight: 800;
  color: #1e40af;
  margin-bottom: 5px;
}

.stat-mini-label {
  font-size: 12px;
  color: #6b7280;
}

.stats-grid {
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
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.stat-header {
  margin-bottom: 18px;
}

.stat-badge {
  display: inline-block;
  padding: 6px 14px;
  background: #eff6ff;
  color: #1e40af;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
}

.stat-badge.success {
  background: #d1fae5;
  color: #065f46;
}

.stat-badge.warning {
  background: #fef3c7;
  color: #92400e;
}

.stat-value {
  font-size: 42px;
  font-weight: 800;
  color: #1f2937;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 30px;
}

.content-main {
  background: white;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.section-title h3 {
  font-size: 20px;
  color: #1f2937;
  font-weight: 700;
  margin: 0;
}

.view-all {
  color: #1e40af;
  font-size: 14px;
  text-decoration: none;
  font-weight: 600;
}

.view-all:hover {
  color: #1e3a8a;
}

.exam-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.exam-item {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 22px;
  background: #f9fafb;
  border-radius: 12px;
  border: 2px solid #f0f0f0;
  transition: all 0.3s ease;
}

.exam-item:hover {
  background: white;
  border-color: #1e40af;
  transform: translateY(-2px);
}

.exam-icon {
  width: 56px;
  height: 56px;
}

.icon-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  color: #1e40af;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 800;
}

.exam-info {
  flex: 1;
}

.exam-info h4 {
  font-size: 17px;
  color: #1f2937;
  margin: 0 0 6px 0;
  font-weight: 700;
}

.exam-meta {
  font-size: 13px;
  color: #6b7280;
  margin: 0;
}

.btn-details {
  padding: 12px 24px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-details:hover {
  background: #1e3a8a;
  transform: translateY(-2px);
}

.content-sidebar {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.tip-card,
.activity-card {
  background: white;
  padding: 28px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.tip-card {
  background: linear-gradient(135deg, #1e3a8a 0%, #1e40af 100%);
  color: white;
}

.tip-card h4 {
  font-size: 19px;
  margin: 0 0 14px 0;
  font-weight: 700;
}

.tip-card p {
  font-size: 14px;
  line-height: 1.7;
  margin: 0 0 18px 0;
}

.btn-tip {
  width: 100%;
  padding: 12px;
  background: white;
  color: #1e40af;
  border: none;
  border-radius: 10px;
  font-weight: 700;
  font-size: 14px;
  cursor: pointer;
}

.btn-tip:hover {
  transform: translateY(-2px);
}

.activity-card h4 {
  font-size: 18px;
  color: #1f2937;
  margin: 0 0 18px 0;
  font-weight: 700;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px;
  background: #f9fafb;
  border-radius: 10px;
}

.activity-item:hover {
  background: #f3f4f6;
}

.activity-icon span {
  font-size: 28px;
}

.activity-success {
  color: #10b981;
}

.activity-warning {
  color: #f59e0b;
}

.activity-danger {
  color: #ef4444;
}

.activity-info {
  flex: 1;
}

.activity-title {
  font-size: 14px;
  color: #1f2937;
  font-weight: 600;
  margin: 0 0 4px 0;
}

.activity-time {
  font-size: 12px;
  color: #6b7280;
  margin: 0;
}

.activity-score {
  font-weight: 800;
  font-size: 16px;
}

.activity-score .excellent {
  color: #10b981;
}

.activity-score .good {
  color: #3b82f6;
}

.activity-score .average {
  color: #f59e0b;
}

.activity-score .poor {
  color: #ef4444;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 50px 20px;
  color: #6b7280;
}

.spinner {
  width: 44px;
  height: 44px;
  border: 4px solid #e5e7eb;
  border-top-color: #1e40af;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 18px;
}

.loading-state-small,
.empty-state-small {
  text-align: center;
  padding: 25px;
  color: #6b7280;
  font-size: 14px;
}

.spinner-small {
  width: 32px;
  height: 32px;
  border: 3px solid #e5e7eb;
  border-top-color: #1e40af;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 12px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 1200px) {
  .content-grid {
    grid-template-columns: 1fr;
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
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
