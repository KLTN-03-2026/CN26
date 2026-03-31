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
        <a href="#" @click.prevent="$router.push('/student/exams')" class="menu-item active">
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
        <button class="btn-help" @click="$router.push('/student/teacher-request')">Trở thành giáo viên</button>
      </div>
    </aside>
    <div class="main-content">
      <header class="top-header">
        <div class="header-left">
          <h1>Danh Sách Đề Thi</h1>
        </div>
        <div class="header-right">
          <div class="user-menu">
            <span class="user-name">{{ authStore.user?.fullName }}</span>
            <button class="btn-logout-small" @click="handleLogout">Đăng xuất</button>
          </div>
        </div>
      </header>

      <div class="filter-section">
        <div class="search-box">
          <input v-model="searchQuery" type="text" placeholder="Tìm kiếm đề thi theo tên..." class="search-input-large">
        </div>
        <div class="filter-box">
          <label>Môn học:</label>
          <select v-model="filterSubject" class="filter-select">
            <option value="">Tất cả môn học</option>
            <option value="Toán">Toán</option>
            <option value="Lý">Lý</option>
            <option value="Hóa">Hóa</option>
            <option value="Anh">Anh</option>
            <option value="Văn">Văn</option>
            <option value="Sinh">Sinh</option>
          </select>
        </div>
      </div>

      <div class="exam-stats-bar">
        <div class="stat-item">
          <span class="stat-number">{{ filteredExams.length }}</span>
          <span class="stat-text">Đề thi khả dụng</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">{{ uniqueSubjects }}</span>
          <span class="stat-text">Môn học</span>
        </div>
      </div>

      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Đang tải danh sách đề thi...</p>
      </div>

      <div v-else-if="filteredExams.length === 0" class="empty-state">
        <div class="empty-icon">📝</div>
        <h3>Không tìm thấy đề thi</h3>
        <p>Thử thay đổi bộ lọc hoặc từ khóa tìm kiếm</p>
      </div>

      <div v-else class="exam-grid">
        <div v-for="exam in filteredExams" :key="exam.id" class="exam-card">
          <div class="exam-card-header">
            <div class="exam-subject-badge">{{ exam.subject }}</div>
            <div class="exam-duration">{{ exam.duration }} phút</div>
          </div>
          <div class="exam-card-body">
            <h3>{{ exam.name }}</h3>
            <p class="exam-description">{{ exam.description || 'Đề thi trắc nghiệm' }}</p>
            <div class="exam-meta-info">
              <div class="meta-item">
                <span class="meta-label">Số câu hỏi:</span>
                <span class="meta-value">{{ exam.totalQuestions }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">Điểm đạt:</span>
                <span class="meta-value">{{ exam.passingScore }}%</span>
              </div>
            </div>
          </div>
          <div class="exam-card-footer">
            <button class="btn-start-exam" @click="startExam(exam.id)">
              Bắt đầu làm bài
            </button>
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
import examService from '../../services/examService'

const router = useRouter()
const authStore = useAuthStore()

const exams = ref([])
const loading = ref(false)
const searchQuery = ref('')
const filterSubject = ref('')

const filteredExams = computed(() => {
  return exams.value.filter(exam => {
    const matchSearch = exam.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchSubject = !filterSubject.value || exam.subject === filterSubject.value
    return matchSearch && matchSubject
  })
})

const uniqueSubjects = computed(() => {
  const subjects = new Set(exams.value.map(e => e.subject))
  return subjects.size
})

onMounted(() => {
  loadExams()
})

const loadExams = async () => {
  try {
    loading.value = true
    const response = await examService.getActiveExams()
    if (response.success) {
      exams.value = response.data
    }
  } catch (error) {
    console.error('Error loading exams:', error)
  } finally {
    loading.value = false
  }
}

const startExam = (examId) => {
  router.push(`/student/exam/${examId}`)
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

.filter-section {
  background: white;
  padding: 25px 30px;
  border-radius: 16px;
  margin-bottom: 25px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  display: flex;
  gap: 20px;
  align-items: center;
}

.search-box {
  flex: 1;
}

.search-input-large {
  width: 100%;
  padding: 14px 18px;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  font-size: 15px;
  transition: all 0.3s ease;
}

.search-input-large:focus {
  outline: none;
  border-color: #1e40af;
  box-shadow: 0 0 0 3px rgba(30,64,175,0.1);
}

.filter-box {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-box label {
  font-size: 14px;
  color: #6b7280;
  font-weight: 600;
}

.filter-select {
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  font-size: 14px;
  min-width: 180px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-select:focus {
  outline: none;
  border-color: #1e40af;
}

.exam-stats-bar {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}

.stat-item {
  background: white;
  padding: 20px 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-number {
  font-size: 32px;
  font-weight: 800;
  color: #1e40af;
}

.stat-text {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.exam-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 25px;
}

.exam-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  transition: all 0.3s ease;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.exam-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.exam-card-header {
  padding: 20px 25px;
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.exam-subject-badge {
  background: #1e40af;
  color: white;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

.exam-duration {
  font-size: 14px;
  color: #1e40af;
  font-weight: 700;
}

.exam-card-body {
  padding: 25px;
  flex: 1;
}

.exam-card-body h3 {
  font-size: 20px;
  color: #1f2937;
  margin: 0 0 12px 0;
  font-weight: 700;
}

.exam-description {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
  margin: 0 0 20px 0;
}

.exam-meta-info {
  display: flex;
  gap: 20px;
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.meta-label {
  font-size: 12px;
  color: #9ca3af;
  font-weight: 500;
}

.meta-value {
  font-size: 16px;
  color: #1f2937;
  font-weight: 700;
}

.exam-card-footer {
  padding: 20px 25px;
  border-top: 1px solid #f0f0f0;
}

.btn-start-exam {
  width: 100%;
  padding: 14px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 700;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-start-exam:hover {
  background: #1e3a8a;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(30,64,175,0.3);
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
  margin: 0;
}

@media (max-width: 1200px) {
  .exam-grid {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
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
  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }
  .exam-grid {
    grid-template-columns: 1fr;
  }
}
</style>
