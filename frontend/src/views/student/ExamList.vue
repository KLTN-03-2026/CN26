<template>
  <StudentLayout activeMenu="exams">
    <header class="top-header">
      <div class="header-left">
        <h1>Danh Sách Đề Thi</h1>
      </div>
      <div class="header-right">
        <div class="user-menu">
          <span class="user-name">{{ authStore.user?.fullName }}</span>
        </div>
      </div>
    </header>

    <div class="filter-section">
      <div class="search-box">
        <input v-model="searchQuery" type="text" placeholder="Tìm kiếm đề thi theo tên..." class="search-input">
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

    <LoadingSpinner v-if="loading" />

    <EmptyState 
      v-else-if="filteredExams.length === 0"
      message="Không tìm thấy đề thi. Thử thay đổi bộ lọc hoặc từ khóa tìm kiếm"
    />

    <div v-else class="exam-grid">
      <div v-for="exam in filteredExams" :key="exam.id" class="exam-card">
        <div class="exam-card-header">
          <div class="exam-subject-badge">{{ exam.subject }}</div>
          <div class="exam-status-badge" :class="getExamStatus(exam).class">
            {{ getExamStatus(exam).text }}
          </div>
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
              <span class="meta-label">Bắt đầu:</span>
              <span class="meta-value">{{ exam.startTime ? formatDateTime(exam.startTime) : 'Không giới hạn' }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">Kết thúc:</span>
              <span class="meta-value">{{ exam.endTime ? formatDateTime(exam.endTime) : 'Không giới hạn' }}</span>
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
  </StudentLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore'
import { useFormatters } from '../../composables/useFormatters'
import examService from '../../services/examService'
import StudentLayout from '../../components/layouts/StudentLayout.vue'
import LoadingSpinner from '../../components/common/LoadingSpinner.vue'
import EmptyState from '../../components/common/EmptyState.vue'

const router = useRouter()
const authStore = useAuthStore()
const { formatDateTime } = useFormatters()

const exams = ref([])
const loading = ref(false)
const searchQuery = ref('')

const filteredExams = computed(() => {
  return exams.value.filter(exam => {
    const matchSearch = exam.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    return matchSearch
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

const getExamStatus = (exam) => {
  const now = new Date()
  const startTime = exam.startTime ? new Date(exam.startTime) : null
  const endTime = exam.endTime ? new Date(exam.endTime) : null
  
  // Nếu có thời gian kết thúc và đã qua thời gian kết thúc
  if (endTime && now > endTime) {
    return { text: 'Đã đóng', class: 'status-closed' }
  }
  
  // Nếu có thời gian bắt đầu và chưa đến thời gian bắt đầu
  if (startTime && now < startTime) {
    return { text: 'Chưa mở', class: 'status-upcoming' }
  }
  
  // Còn lại là đang mở
  return { text: 'Đang mở', class: 'status-open' }
}
</script>

<style scoped>
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
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-bottom: 20px;
  display: flex;
  gap: 20px;
  align-items: center;
}

.search-box {
  flex: 1;
}

.filter-box {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-box label {
  font-size: 14px;
  color: #374151;
  font-weight: 600;
}

.exam-stats-bar {
  display: flex;
  gap: 20px;
  margin-bottom: 25px;
}

.stat-item {
  background: white;
  padding: 20px 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
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
}

.exam-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.exam-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  overflow: hidden;
  transition: all 0.3s;
}

.exam-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.12);
}

.exam-card-header {
  padding: 15px 20px;
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.exam-subject-badge {
  padding: 6px 14px;
  background: rgba(255,255,255,0.2);
  color: white;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.exam-status-badge {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.exam-status-badge.status-open {
  background: #d1fae5;
  color: #065f46;
}

.exam-status-badge.status-closed {
  background: #fee2e2;
  color: #991b1b;
}

.exam-status-badge.status-upcoming {
  background: #fef3c7;
  color: #92400e;
}

.exam-duration {
  color: white;
  font-size: 14px;
  font-weight: 600;
}

.exam-card-body {
  padding: 20px;
}

.exam-card-body h3 {
  font-size: 18px;
  color: #1f2937;
  margin: 0 0 10px 0;
  font-weight: 700;
}

.exam-description {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 15px 0;
  line-height: 1.6;
}

.exam-meta-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
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
  padding: 15px 20px;
  background: #f9fafb;
  border-top: 1px solid #e5e7eb;
}

.btn-start-exam {
  width: 100%;
  padding: 12px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-start-exam:hover {
  background: #1e3a8a;
  transform: translateY(-2px);
}
</style>
