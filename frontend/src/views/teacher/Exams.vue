<template>
  <TeacherLayout activeMenu="exams">
    <header class="top-header">
      <div class="header-left">
        <h1>Quản Lý Đề Thi</h1>
      </div>
      <div class="header-right">
        <button class="btn-create" @click="openCreateModal">Tạo đề thi mới</button>
      </div>
    </header>

    <LoadingSpinner v-if="loading" />
    
    <EmptyState 
      v-else-if="exams.length === 0"
      message="Chưa có đề thi nào. Tạo đề thi đầu tiên từ ngân hàng câu hỏi của bạn"
      actionText="Tạo đề thi đầu tiên"
      @action="openCreateModal"
    />
    
    <div v-else class="exams-grid">
      <ExamCard
        v-for="exam in exams"
        :key="exam.id"
        :exam="exam"
        @view-stats="viewStatistics"
        @toggle-status="toggleStatus"
        @edit="editExam"
        @delete="deleteExam"
      />
    </div>

    <!-- Create/Edit Modal -->
    <AppModal v-model="showCreateModal" :title="editingExam ? 'Sửa Đề Thi' : 'Tạo Đề Thi Mới'" size="large">
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
          <label>Số lần được làm lại</label>
          <input v-model.number="examForm.maxAttempts" type="number" min="0" placeholder="0 = Không giới hạn">
          <small class="form-hint">Nhập 0 để cho phép làm không giới hạn, nhập 1 để chỉ cho làm 1 lần</small>
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
              <span :class="['level-badge', 'level-' + question.level]">
                {{ getLevelText(question.level) }}
              </span>
            </label>
          </div>
        </div>

        <div class="modal-actions">
          <button type="button" class="btn-cancel" @click="showCreateModal = false">Hủy</button>
          <button type="submit" class="btn-save" :disabled="selectedQuestions.length === 0">
            {{ editingExam ? 'Cập nhật' : 'Tạo đề thi' }}
          </button>
        </div>
      </form>
    </AppModal>
  </TeacherLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useFormatters } from '../../composables/useFormatters'
import examService from '../../services/examService'
import questionService from '../../services/questionService'
import TeacherLayout from '../../components/layouts/TeacherLayout.vue'
import LoadingSpinner from '../../components/common/LoadingSpinner.vue'
import EmptyState from '../../components/common/EmptyState.vue'
import AppModal from '../../components/common/AppModal.vue'
import ExamCard from '../../components/teacher/ExamCard.vue'

const router = useRouter()
const { formatDateTime, getLevelText } = useFormatters()

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
  endTime: '',
  maxAttempts: 0
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
    alert(error.error || error.message || 'Có lỗi khi lưu đề thi')
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
    endTime: exam.endTime ? formatDateTimeForInput(exam.endTime) : '',
    maxAttempts: exam.maxAttempts || 0
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
    const response = await examService.toggleExamStatus(exam.id)
    if (response.success) {
      alert('Cập nhật trạng thái thành công!')
      loadExams()
    }
  } catch (error) {
    console.error('Error updating status:', error)
    alert(error.error || 'Có lỗi khi cập nhật trạng thái')
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
    endTime: '',
    maxAttempts: 0
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

const openCreateModal = async () => {
  await loadQuestions()
  showCreateModal.value = true
}
</script>

<style scoped>
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

.exams-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.form-hint {
  display: block;
  margin-top: 5px;
  font-size: 13px;
  color: #6b7280;
  font-style: italic;
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

@keyframes spin {
  to { transform: rotate(360deg); }
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
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.level-badge.level-easy {
  background: #d1fae5;
  color: #065f46;
}

.level-badge.level-medium {
  background: #fef3c7;
  color: #92400e;
}

.level-badge.level-hard {
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
