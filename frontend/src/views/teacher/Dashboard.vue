<template>
  <TeacherLayout activeMenu="home">
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
        <button class="btn-action" @click="$router.push('/teacher/exams')">
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
      
      <LoadingSpinner v-if="loading" />
      
      <EmptyState 
        v-else-if="exams.length === 0"
        message="Chưa có đề thi nào"
        actionText="Tạo đề thi đầu tiên"
        @action="$router.push('/teacher/exams')"
      />
      
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
  </TeacherLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore'
import examService from '../../services/examService'
import questionService from '../../services/questionService'
import TeacherLayout from '../../components/layouts/TeacherLayout.vue'
import LoadingSpinner from '../../components/common/LoadingSpinner.vue'
import EmptyState from '../../components/common/EmptyState.vue'

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
</script>

<style scoped>
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
