<template>
  <div class="dashboard-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <img src="/logo/logo.png" alt="Logo" class="sidebar-logo" />
        <h2>Hệ Thống Thi</h2>
        <p>Giáo viên</p>
      </div>
      <nav class="sidebar-menu">
        <a href="#" @click.prevent="$router.push('/teacher')" class="menu-item active">
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
        <button class="btn-logout" @click="handleLogout">Đăng xuất</button>
      </div>
    </aside>
    <div class="main-content">
      <header class="top-header">
        <div class="header-left">
          <h1>Trang chủ</h1>
        </div>
        <div class="header-right">
          <div class="user-menu">
            <span class="user-name">{{ authStore.user?.fullName }}</span>
          </div>
        </div>
      </header>

      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-info">
            <h3>{{ stats.totalQuestions }}</h3>
            <p>Câu hỏi</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-info">
            <h3>{{ stats.totalExams }}</h3>
            <p>Đề thi</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-info">
            <h3>{{ stats.totalStudents }}</h3>
            <p>Học sinh đã thi</p>
          </div>
        </div>
      </div>

      <div class="quick-actions">
        <div class="action-card">
          <h3>Quản Lý Câu Hỏi</h3>
          <p>Tạo và quản lý ngân hàng câu hỏi của bạn</p>
          <button class="btn-action" @click="$router.push('/teacher/questions')">
            Quản lý câu hỏi →
          </button>
        </div>

        <div class="action-card">
          <h3>Quản Lý Đề Thi</h3>
          <p>Tạo đề thi từ ngân hàng câu hỏi</p>
          <button class="btn-action" @click="$router.push('/teacher/exams')">
            Quản lý đề thi →
          </button>
        </div>

        <div class="action-card">
          <h3>Xem Thống Kê</h3>
          <p>Phân tích kết quả thi của học sinh</p>
          <button class="btn-action" @click="$router.push('/teacher/statistics')">
            Xem thống kê →
          </button>
        </div>
      </div>

      <div class="recent-exams">
        <div class="section-header">
          <h2>Đề Thi Gần Đây</h2>
          <button class="btn-view-all" @click="$router.push('/teacher/exams')">
            Xem tất cả →
          </button>
        </div>
        
        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <p>Đang tải...</p>
        </div>
        
        <div v-else-if="exams.length === 0" class="empty-state">
          <p>Chưa có đề thi nào</p>
          <button class="btn-create" @click="$router.push('/teacher/exams')">
            Tạo đề thi đầu tiên
          </button>
        </div>
        
        <div v-else class="exams-table">
          <table>
            <thead>
              <tr>
                <th>Tên đề thi</th>
                <th>Số câu</th>
                <th>Thời gian</th>
                <th>Trạng thái</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="exam in exams" :key="exam.id">
                <td class="exam-name">{{ exam.name }}</td>
                <td>{{ exam.totalQuestions }} câu</td>
                <td>{{ exam.duration }} phút</td>
                <td>
                  <span :class="['status-badge', exam.isActive ? 'active' : 'inactive']">
                    {{ exam.isActive ? 'Đang mở' : 'Đã đóng' }}
                  </span>
                </td>
                <td>
                  <button class="btn-stats" @click="viewStatistics(exam.id)">
                    Thống kê
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
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
import questionService from '../../services/questionService'

const router = useRouter()
const authStore = useAuthStore()

const exams = ref([])
const loading = ref(false)

const stats = ref({
  totalQuestions: 0,
  totalExams: 0,
  totalStudents: 0
})

onMounted(() => {
  loadDashboardData()
})

const loadDashboardData = async () => {
  try {
    loading.value = true
    
    const [examsResponse, questionsResponse] = await Promise.all([
      examService.getMyExams(),
      questionService.getMyQuestions()
    ])

    if (examsResponse.success) {
      exams.value = examsResponse.data.slice(0, 5)
      stats.value.totalExams = examsResponse.data.length
    }

    if (questionsResponse.success) {
      stats.value.totalQuestions = questionsResponse.data.length
    }
  } catch (error) {
    console.error('Error loading dashboard:', error)
  } finally {
    loading.value = false
  }
}

const viewStatistics = (examId) => {
  router.push(`/teacher/statistics/${examId}`)
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

.top-header {
  background: white;
  padding: 20px 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-bottom: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left h1 {
  font-size: 28px;
  color: #1f2937;
  margin: 0;
  font-weight: 800;
}

.user-name {
  font-size: 15px;
  color: #374151;
  font-weight: 600;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
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

.quick-actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.action-card {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  text-align: center;
  transition: all 0.3s;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.12);
}

.action-card h3 {
  font-size: 18px;
  color: #1f2937;
  margin: 0 0 10px 0;
  font-weight: 700;
}

.action-card p {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 20px 0;
  line-height: 1.6;
}

.btn-action {
  padding: 10px 20px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-action:hover {
  background: #1e3a8a;
}

.recent-exams {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 20px;
  color: #1f2937;
  margin: 0;
  font-weight: 700;
}

.btn-view-all {
  padding: 8px 16px;
  background: #f3f4f6;
  color: #374151;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-view-all:hover {
  background: #e5e7eb;
}

.loading-state, .empty-state {
  text-align: center;
  padding: 40px 20px;
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
  margin-bottom: 15px;
}

.btn-create {
  padding: 10px 20px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}

.exams-table table {
  width: 100%;
  border-collapse: collapse;
}

.exams-table th {
  text-align: left;
  padding: 12px;
  background: #f9fafb;
  color: #374151;
  font-weight: 600;
  font-size: 14px;
  border-bottom: 2px solid #e5e7eb;
}

.exams-table td {
  padding: 15px 12px;
  border-bottom: 1px solid #f3f4f6;
  color: #6b7280;
  font-size: 14px;
}

.exam-name {
  color: #1f2937;
  font-weight: 600;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.active {
  background: #d1fae5;
  color: #065f46;
}

.status-badge.inactive {
  background: #fee2e2;
  color: #991b1b;
}

.btn-stats {
  padding: 6px 16px;
  background: #eff6ff;
  color: #1e40af;
  border: 1px solid #bfdbfe;
  border-radius: 6px;
  font-weight: 600;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-stats:hover {
  background: #dbeafe;
}

@media (max-width: 1200px) {
  .stats-grid, .quick-actions {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-grid, .quick-actions {
    grid-template-columns: 1fr;
  }
}
</style>
