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
        <a href="#" @click.prevent="$router.push('/teacher/exams')" class="menu-item active">
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
          <h1>Quản Lý Đề Thi</h1>
        </div>
        <div class="header-right">
          <button class="btn-create" @click="openCreateModal">Tạo đề thi mới</button>
        </div>
      </header>

      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Đang tải...</p>
      </div>
      
      <div v-else-if="exams.length === 0" class="empty-state">
        <h3>Chưa có đề thi nào</h3>
        <p>Tạo đề thi đầu tiên từ ngân hàng câu hỏi của bạn</p>
        <button class="btn-create-large" @click="openCreateModal">
          Tạo đề thi đầu tiên
        </button>
      </div>
      
      <div v-else class="exams-grid">
        <div v-for="exam in exams" :key="exam.id" class="exam-card">
          <div class="exam-header">
            <h3>{{ exam.name }}</h3>
            <span :class="['status-badge', exam.isActive ? 'active' : 'inactive']">
              {{ exam.isActive ? 'Đang mở' : 'Đã đóng' }}
            </span>
          </div>
          <p class="exam-description">{{ exam.description }}</p>
          <div class="exam-meta">
            <span class="meta-item">{{ exam.totalQuestions }} câu</span>
            <span class="meta-item">{{ exam.duration }} phút</span>
            <span class="meta-item">{{ exam.subject }}</span>
          </div>
          <div v-if="exam.startTime || exam.endTime" class="exam-dates">
            <div v-if="exam.startTime" class="date-item">
              <span class="date-label">Bắt đầu:</span>
              <span class="date-value">{{ formatDateTime(exam.startTime) }}</span>
            </div>
            <div v-if="exam.endTime" class="date-item">
              <span class="date-label">Kết thúc:</span>
              <span class="date-value">{{ formatDateTime(exam.endTime) }}</span>
            </div>
          </div>
          <div class="exam-actions">
            <button class="btn-action btn-stats" @click="viewStatistics(exam.id)">
              Thống kê
            </button>
            <button class="btn-action btn-toggle" @click="toggleStatus(exam)">
              {{ exam.isActive ? 'Đóng' : 'Mở' }}
            </button>
            <button class="btn-action btn-edit" @click="editExam(exam)">
              Sửa
            </button>
            <button class="btn-action btn-delete" @click="deleteExam(exam.id)">
              Xóa
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showCreateModal" class="modal" @click.self="closeCreateModal">
      <div class="modal-content modal-large">
        <h2>{{ editingExam ? 'Sửa Đề Thi' : 'Tạo Đề Thi Mới' }}</h2>
        <form @submit.prevent="saveExam">
          <div class="form-group">
            <label>Tên đề thi</label>
            <input v-model="examForm.name" required placeholder="VD: Kiểm tra giữa kỳ Toán 12">
          </div>
          <div class="form-group">
            <label>Mô tả</label>
            <textarea v-model="examForm.description" rows="3" placeholder="Mô tả ngắn về đề thi"></textarea>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Thời gian (phút)</label>
              <input v-model.number="examForm.duration" type="number" min="1" required>
            </div>
            <div class="form-group">
              <label>Môn học</label>
              <input v-model="examForm.subject" required placeholder="VD: Toán học">
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Ngày bắt đầu</label>
              <input v-model="examForm.startTime" type="datetime-local">
            </div>
            <div class="form-group">
              <label>Ngày kết thúc</label>
              <input v-model="examForm.endTime" type="datetime-local">
            </div>
          </div>

          <div class="form-group">
            <label>Chọn câu hỏi ({{ selectedQuestions.length }} câu đã chọn)</label>
            <div class="questions-list">
              <div v-if="loadingQuestions" class="loading-small">
                <div class="spinner-small"></div>
                <span>Đang tải câu hỏi...</span>
              </div>
              <div v-else-if="availableQuestions.length === 0" class="empty-small">
                Chưa có câu hỏi nào. Vui lòng tạo câu hỏi trước.
              </div>
              <label v-else v-for="question in availableQuestions" :key="question.id" 
                     class="question-item"
                     :class="{ 'selected': selectedQuestions.includes(question.id) }">
                <input type="checkbox" 
                       :value="question.id" 
                       v-model="selectedQuestions">
                <span class="question-content">{{ question.content }}</span>
                <span :class="['level-badge', question.level]">
                  {{ getLevelText(question.level) }}
                </span>
              </label>
            </div>
          </div>

          <div class="modal-actions">
            <button type="button" class="btn-cancel" @click="closeCreateModal">Hủy</button>
            <button type="submit" class="btn-save" :disabled="selectedQuestions.length === 0">
              {{ editingExam ? 'Cập nhật' : 'Tạo đề thi' }}
            </button>
          </div>
        </form>
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
const availableQuestions = ref([])
const loading = ref(false)
const loadingQuestions = ref(false)
const showCreateModal = ref(false)
const editingExam = ref(null)
const selectedQuestions = ref([])

const examForm = ref({
  name: '',
  description: '',
  duration: 60,
  subject: '',
  startTime: '',
  endTime: ''
})

onMounted(() => {
  loadExams()
})

const loadExams = async () => {
  try {
    loading.value = true
    const response = await examService.getMyExams()
    if (response.success) {
      exams.value = response.data
    }
  } catch (error) {
    console.error('Error loading exams:', error)
  } finally {
    loading.value = false
  }
}

const loadQuestions = async () => {
  try {
    loadingQuestions.value = true
    const response = await questionService.getMyQuestions()
    if (response.success) {
      availableQuestions.value = response.data
    }
  } catch (error) {
    console.error('Error loading questions:', error)
  } finally {
    loadingQuestions.value = false
  }
}

const saveExam = async () => {
  try {
    const data = {
      ...examForm.value,
      questionIds: selectedQuestions.value
    }

    let response
    if (editingExam.value) {
      response = await examService.updateExam(editingExam.value.id, data)
    } else {
      response = await examService.createExam(data)
    }

    if (response.success) {
      alert('Lưu đề thi thành công!')
      closeCreateModal()
      loadExams()
    }
  } catch (error) {
    console.error('Error saving exam:', error)
    alert('Có lỗi khi lưu đề thi')
  }
}

const editExam = async (exam) => {
  editingExam.value = exam
  examForm.value = {
    name: exam.name,
    description: exam.description,
    duration: exam.duration,
    subject: exam.subject,
    startTime: exam.startTime ? formatDateTimeForInput(exam.startTime) : '',
    endTime: exam.endTime ? formatDateTimeForInput(exam.endTime) : ''
  }
  
  await loadQuestions()
  
  try {
    const response = await examService.getExamQuestions(exam.id)
    if (response.success) {
      selectedQuestions.value = response.data.map(q => q.id)
    }
  } catch (error) {
    console.error('Error loading exam questions:', error)
  }
  
  showCreateModal.value = true
}

const deleteExam = async (id) => {
  if (!confirm('Bạn có chắc muốn xóa đề thi này?')) return
  
  try {
    const response = await examService.deleteExam(id)
    if (response.success) {
      alert('Xóa thành công!')
      loadExams()
    }
  } catch (error) {
    console.error('Error deleting exam:', error)
    alert('Có lỗi khi xóa đề thi')
  }
}

const toggleStatus = async (exam) => {
  try {
    const newStatus = !exam.isActive
    const response = await examService.updateExam(exam.id, { isActive: newStatus })
    if (response.success) {
      alert('Cập nhật trạng thái thành công!')
      loadExams()
    }
  } catch (error) {
    console.error('Error updating status:', error)
    alert('Có lỗi khi cập nhật trạng thái')
  }
}

const viewStatistics = (examId) => {
  router.push(`/teacher/statistics/${examId}`)
}

const closeCreateModal = () => {
  showCreateModal.value = false
  editingExam.value = null
  selectedQuestions.value = []
  examForm.value = {
    name: '',
    description: '',
    duration: 60,
    subject: '',
    startTime: '',
    endTime: ''
  }
}

const formatDateTimeForInput = (dateTimeString) => {
  if (!dateTimeString) return ''
  const date = new Date(dateTimeString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day}T${hours}:${minutes}`
}

const formatDateTime = (dateTimeString) => {
  if (!dateTimeString) return ''
  const date = new Date(dateTimeString)
  return date.toLocaleString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getLevelText = (level) => {
  const map = { easy: 'Dễ', medium: 'TB', hard: 'Khó' }
  return map[level] || level
}

const openCreateModal = async () => {
  await loadQuestions()
  showCreateModal.value = true
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

.btn-create {
  padding: 12px 24px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-create:hover {
  background: #1e3a8a;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(30,64,175,0.3);
}

.loading-state, .empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
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
  to { transform: rotate(360deg); }
}

.empty-state h3 {
  font-size: 20px;
  color: #1f2937;
  margin: 0 0 10px 0;
}

.empty-state p {
  color: #6b7280;
  margin: 0 0 20px 0;
}

.btn-create-large {
  padding: 14px 28px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
}

.exams-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.exam-card {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  transition: all 0.3s;
}

.exam-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.12);
}

.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 15px;
}

.exam-header h3 {
  font-size: 18px;
  color: #1f2937;
  margin: 0;
  font-weight: 700;
  flex: 1;
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

.exam-description {
  color: #6b7280;
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 15px 0;
}

.exam-meta {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.meta-item {
  padding: 6px 12px;
  background: #f3f4f6;
  border-radius: 6px;
  font-size: 13px;
  color: #374151;
  font-weight: 500;
}

.exam-dates {
  margin-bottom: 20px;
  padding: 12px;
  background: #eff6ff;
  border-radius: 8px;
  border-left: 3px solid #3b82f6;
}

.date-item {
  display: flex;
  gap: 8px;
  margin-bottom: 6px;
  font-size: 13px;
}

.date-item:last-child {
  margin-bottom: 0;
}

.date-label {
  color: #1e40af;
  font-weight: 600;
}

.date-value {
  color: #374151;
  font-weight: 500;
}

.exam-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.btn-action {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-stats {
  background: #eff6ff;
  color: #1e40af;
}

.btn-stats:hover {
  background: #dbeafe;
}

.btn-toggle {
  background: #fef3c7;
  color: #92400e;
}

.btn-toggle:hover {
  background: #fde68a;
}

.btn-edit {
  background: #f3f4f6;
  color: #374151;
}

.btn-edit:hover {
  background: #e5e7eb;
}

.btn-delete {
  background: #fee2e2;
  color: #991b1b;
}

.btn-delete:hover {
  background: #fecaca;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 12px;
  max-width: 600px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-large {
  max-width: 800px;
}

.modal-content h2 {
  font-size: 24px;
  color: #1f2937;
  margin: 0 0 25px 0;
  font-weight: 800;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 14px;
  color: #374151;
  font-weight: 600;
  margin-bottom: 8px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #1e40af;
  box-shadow: 0 0 0 3px rgba(30,64,175,0.1);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.questions-list {
  max-height: 300px;
  overflow-y: auto;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  padding: 10px;
}

.loading-small, .empty-small {
  text-align: center;
  padding: 20px;
  color: #6b7280;
  font-size: 14px;
}

.spinner-small {
  width: 30px;
  height: 30px;
  border: 3px solid #e5e7eb;
  border-top-color: #1e40af;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 10px;
}

.question-item {
  display: flex;
  align-items: center;
  padding: 12px;
  margin-bottom: 8px;
  background: #f9fafb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.question-item:hover {
  background: #f3f4f6;
}

.question-item.selected {
  background: #eff6ff;
  border-color: #1e40af;
}

.question-item input[type="checkbox"] {
  margin-right: 12px;
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.question-content {
  flex: 1;
  font-size: 14px;
  color: #374151;
}

.level-badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
}

.level-badge.easy {
  background: #d1fae5;
  color: #065f46;
}

.level-badge.medium {
  background: #fef3c7;
  color: #92400e;
}

.level-badge.hard {
  background: #fee2e2;
  color: #991b1b;
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 25px;
}

.btn-cancel {
  padding: 12px 24px;
  background: #f3f4f6;
  color: #374151;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel:hover {
  background: #e5e7eb;
}

.btn-save {
  padding: 12px 24px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-save:hover:not(:disabled) {
  background: #1e3a8a;
}

.btn-save:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}
</style>
