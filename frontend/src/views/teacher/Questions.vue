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
        <a href="#" @click.prevent="$router.push('/teacher/questions')" class="menu-item active">
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
          <h1>Quản Lý Câu Hỏi</h1>
        </div>
        <div class="header-actions">
          <button class="btn-action" @click="showImportModal = true">Nhập từ Word</button>
          <button class="btn-action" @click="showAIModal = true">Tạo bằng AI</button>
          <button class="btn-primary" @click="showCreateModal = true">Tạo câu hỏi</button>
        </div>
      </header>

      <div class="filter-section">
        <input v-model="searchQuery" type="text" placeholder="Tìm kiếm câu hỏi..." class="search-input">
        <select v-model="filterLevel" class="filter-select">
          <option value="">Tất cả độ khó</option>
          <option value="easy">Dễ</option>
          <option value="medium">Trung bình</option>
          <option value="hard">Khó</option>
        </select>
      </div>

      <div class="questions-container">
        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <p>Đang tải...</p>
        </div>
        <div v-else-if="filteredQuestions.length === 0" class="empty-state">
          <p>Chưa có câu hỏi nào</p>
          <button class="btn-create" @click="showCreateModal = true">Tạo câu hỏi đầu tiên</button>
        </div>
        <div v-else class="questions-grid">
          <div v-for="question in filteredQuestions" :key="question.id" class="question-card">
            <div class="question-header">
              <span :class="['level-badge', `level-${question.level}`]">{{ getLevelText(question.level) }}</span>
              <div class="question-actions">
                <button class="btn-edit" @click="editQuestion(question)">Sửa</button>
                <button class="btn-delete" @click="deleteQuestion(question.id)">Xóa</button>
              </div>
            </div>
            <div class="question-content">
              <p class="question-text">{{ question.content }}</p>
              <div class="options-list">
                <div class="option-item">
                  <span class="option-label">A</span>
                  <span>{{ question.optionA }}</span>
                </div>
                <div class="option-item">
                  <span class="option-label">B</span>
                  <span>{{ question.optionB }}</span>
                </div>
                <div class="option-item">
                  <span class="option-label">C</span>
                  <span>{{ question.optionC }}</span>
                </div>
                <div class="option-item">
                  <span class="option-label">D</span>
                  <span>{{ question.optionD }}</span>
                </div>
              </div>
              <div class="correct-answer">
                Đáp án đúng: <strong>{{ question.correctAnswer }}</strong>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showCreateModal" class="modal" @click.self="closeCreateModal">
      <div class="modal-content">
        <h2>{{ editingQuestion ? 'Sửa Câu Hỏi' : 'Tạo Câu Hỏi Mới' }}</h2>
        <form @submit.prevent="saveQuestion">
          <div class="form-group">
            <label>Nội dung câu hỏi</label>
            <textarea v-model="questionForm.content" required rows="3"></textarea>
          </div>
          <div class="form-group">
            <label>Đáp án A</label>
            <input v-model="questionForm.optionA" required>
          </div>
          <div class="form-group">
            <label>Đáp án B</label>
            <input v-model="questionForm.optionB" required>
          </div>
          <div class="form-group">
            <label>Đáp án C</label>
            <input v-model="questionForm.optionC" required>
          </div>
          <div class="form-group">
            <label>Đáp án D</label>
            <input v-model="questionForm.optionD" required>
          </div>
          <div class="form-group">
            <label>Đáp án đúng</label>
            <select v-model="questionForm.correctAnswer" required>
              <option value="A">A</option>
              <option value="B">B</option>
              <option value="C">C</option>
              <option value="D">D</option>
            </select>
          </div>
          <div class="form-group">
            <label>Độ khó</label>
            <select v-model="questionForm.level" required>
              <option value="easy">Dễ</option>
              <option value="medium">Trung bình</option>
              <option value="hard">Khó</option>
            </select>
          </div>
          <div style="display: flex; gap: 10px; justify-content: flex-end;">
            <button type="button" class="btn-secondary" @click="closeCreateModal">Hủy</button>
            <button type="submit" class="btn-primary">Lưu</button>
          </div>
        </form>
      </div>
    </div>

    <!-- AI Modal -->
    <div v-if="showAIModal" class="modal" @click.self="closeAIModal">
      <div class="modal-content modal-large">
        <h2>Tạo Câu Hỏi Bằng AI</h2>
        
        <div v-if="!aiGeneratedQuestions.length">
          <form @submit.prevent="generateAIQuestions">
            <div class="form-group">
              <label>Chủ đề</label>
              <input v-model="aiForm.topic" required placeholder="VD: Phương trình bậc 2">
            </div>
            <div class="form-group">
              <label>Số lượng câu hỏi</label>
              <input v-model.number="aiForm.count" type="number" min="1" max="10" required>
            </div>
            <div class="form-group">
              <label>Độ khó</label>
              <select v-model="aiForm.level" required>
                <option value="easy">Dễ</option>
                <option value="medium">Trung bình</option>
                <option value="hard">Khó</option>
              </select>
            </div>
            <div style="display: flex; gap: 10px; justify-content: flex-end;">
              <button type="button" class="btn-secondary" @click="closeAIModal">Hủy</button>
              <button type="submit" class="btn-primary" :disabled="generatingAI">
                {{ generatingAI ? 'Đang tạo...' : 'Tạo câu hỏi' }}
              </button>
            </div>
          </form>
        </div>

        <div v-else>
          <div class="preview-header">
            <p>Đã tạo {{ aiGeneratedQuestions.length }} câu hỏi. Chọn câu hỏi muốn lưu:</p>
            <div style="display: flex; gap: 10px;">
              <button class="btn-secondary" @click="selectAllAI">Chọn tất cả</button>
              <button class="btn-secondary" @click="deselectAllAI">Bỏ chọn tất cả</button>
            </div>
          </div>

          <div class="ai-questions-preview">
            <label v-for="(question, index) in aiGeneratedQuestions" :key="index" 
                   class="preview-question-item"
                   :class="{ 'selected': selectedAIQuestions.includes(index) }">
              <input type="checkbox" :value="index" v-model="selectedAIQuestions">
              <div class="preview-question-content">
                <p class="preview-question-text">{{ question.content }}</p>
                <div class="preview-options">
                  <div class="preview-option">
                    <span class="option-label">A</span>
                    <span>{{ question.optionA }}</span>
                  </div>
                  <div class="preview-option">
                    <span class="option-label">B</span>
                    <span>{{ question.optionB }}</span>
                  </div>
                  <div class="preview-option">
                    <span class="option-label">C</span>
                    <span>{{ question.optionC }}</span>
                  </div>
                  <div class="preview-option">
                    <span class="option-label">D</span>
                    <span>{{ question.optionD }}</span>
                  </div>
                </div>
                <div class="preview-answer">
                  Đáp án đúng: <strong>{{ question.correctAnswer }}</strong>
                </div>
              </div>
            </label>
          </div>

          <div style="display: flex; gap: 10px; justify-content: flex-end; margin-top: 20px;">
            <button class="btn-secondary" @click="closeAIModal">Hủy</button>
            <button class="btn-primary" @click="saveSelectedAIQuestions" :disabled="selectedAIQuestions.length === 0">
              Lưu {{ selectedAIQuestions.length }} câu đã chọn
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Import Modal -->
    <div v-if="showImportModal" class="modal" @click.self="showImportModal = false">
      <div class="modal-content modal-large">
        <h2>Nhập Câu Hỏi Từ Word</h2>
        
        <div class="import-guide">
          <h2>Hướng dẫn định dạng file Word</h2>
          <div class="guide-content">
            <p>File Word (.docx) cần tuân theo định dạng sau:</p>
            <div class="template-example">
              <pre>Câu 1: Nội dung câu hỏi?
A. Đáp án A
B. Đáp án B
C. Đáp án C
D. Đáp án D
Đáp án: A

Câu 2: Nội dung câu hỏi tiếp theo?
A. Đáp án A
B. Đáp án B
C. Đáp án C
D. Đáp án D
Đáp án: B</pre>
            </div>
            <div class="guide-notes">
              <p><strong>Lưu ý:</strong></p>
              <ul>
                <li>Mỗi câu hỏi bắt đầu bằng "Câu X:" (X là số thứ tự)</li>
                <li>4 đáp án A, B, C, D phải có đầy đủ</li>
                <li>Dòng "Đáp án:" chỉ định đáp án đúng (A, B, C hoặc D)</li>
                <li>Có thể thêm "Giải thích:" và "Độ khó:" (tùy chọn)</li>
                <li>Để trống 1 dòng giữa các câu hỏi</li>
              </ul>
            </div>
          </div>
        </div>

        <div class="form-group">
          <label>Chọn file Word (.docx)</label>
          <div class="file-upload-area">
            <input type="file" accept=".docx" @change="handleFileSelect" id="wordFile" class="file-input">
            <label for="wordFile" class="file-label">
              <span v-if="!selectedFile">Chọn file hoặc kéo thả vào đây</span>
              <span v-else class="file-selected">{{ selectedFile.name }}</span>
            </label>
          </div>
        </div>
        
        <div style="display: flex; gap: 10px; justify-content: flex-end; margin-top: 20px;">
          <button class="btn-secondary" @click="showImportModal = false">Hủy</button>
          <button class="btn-primary" @click="importFromWord" :disabled="!selectedFile || importing">
            {{ importing ? 'Đang nhập...' : 'Nhập câu hỏi' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore'
import questionService from '../../services/questionService'

const router = useRouter()
const authStore = useAuthStore()

const questions = ref([])
const loading = ref(false)
const searchQuery = ref('')
const filterLevel = ref('')

const showCreateModal = ref(false)
const showAIModal = ref(false)
const showImportModal = ref(false)
const editingQuestion = ref(null)
const generatingAI = ref(false)
const importing = ref(false)
const selectedFile = ref(null)
const aiGeneratedQuestions = ref([])
const selectedAIQuestions = ref([])

const questionForm = ref({
  content: '',
  optionA: '',
  optionB: '',
  optionC: '',
  optionD: '',
  correctAnswer: 'A',
  level: 'medium'
})

const aiForm = ref({
  topic: '',
  count: 5,
  level: 'medium'
})

const filteredQuestions = computed(() => {
  return questions.value.filter(q => {
    const matchSearch = q.content.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchLevel = !filterLevel.value || q.level === filterLevel.value
    return matchSearch && matchLevel
  })
})

onMounted(() => {
  loadQuestions()
})

const loadQuestions = async () => {
  try {
    loading.value = true
    const response = await questionService.getMyQuestions()
    if (response.success) {
      questions.value = response.data
    }
  } catch (error) {
    console.error('Error loading questions:', error)
  } finally {
    loading.value = false
  }
}

const saveQuestion = async () => {
  try {
    let response
    if (editingQuestion.value) {
      response = await questionService.updateQuestion(editingQuestion.value.id, questionForm.value)
    } else {
      response = await questionService.createQuestion(questionForm.value)
    }
    
    if (response.success) {
      alert('Lưu câu hỏi thành công!')
      closeCreateModal()
      loadQuestions()
    }
  } catch (error) {
    console.error('Error saving question:', error)
    alert('Có lỗi khi lưu câu hỏi')
  }
}

const editQuestion = (question) => {
  editingQuestion.value = question
  questionForm.value = { ...question }
  showCreateModal.value = true
}

const deleteQuestion = async (id) => {
  if (!confirm('Bạn có chắc muốn xóa câu hỏi này?')) return
  
  try {
    const response = await questionService.deleteQuestion(id)
    if (response.success) {
      alert('Xóa thành công!')
      loadQuestions()
    }
  } catch (error) {
    console.error('Error deleting question:', error)
    alert('Có lỗi khi xóa câu hỏi')
  }
}

const generateAIQuestions = async () => {
  try {
    generatingAI.value = true
    const response = await questionService.generateAIQuestions({
      topic: aiForm.value.topic,
      quantity: aiForm.value.count,
      level: aiForm.value.level,
      subject: aiForm.value.topic
    })
    if (response.success && response.data) {
      aiGeneratedQuestions.value = response.data
      selectedAIQuestions.value = response.data.map((_, index) => index)
    }
  } catch (error) {
    console.error('Error generating AI questions:', error)
    alert('Có lỗi khi tạo câu hỏi bằng AI')
  } finally {
    generatingAI.value = false
  }
}

const selectAllAI = () => {
  selectedAIQuestions.value = aiGeneratedQuestions.value.map((_, index) => index)
}

const deselectAllAI = () => {
  selectedAIQuestions.value = []
}

const saveSelectedAIQuestions = async () => {
  try {
    const questionsToSave = selectedAIQuestions.value.map(index => aiGeneratedQuestions.value[index])
    
    let savedCount = 0
    for (const question of questionsToSave) {
      try {
        await questionService.createQuestion(question)
        savedCount++
      } catch (error) {
        console.error('Error saving question:', error)
      }
    }
    
    alert(`Đã lưu ${savedCount}/${questionsToSave.length} câu hỏi!`)
    closeAIModal()
    await loadQuestions()
  } catch (error) {
    console.error('Error saving AI questions:', error)
    alert('Có lỗi khi lưu câu hỏi')
  }
}

const closeAIModal = () => {
  // Nếu có câu hỏi đã tạo nhưng chưa lưu, xác nhận trước khi đóng
  if (aiGeneratedQuestions.value.length > 0) {
    if (!confirm('Bạn có chắc muốn hủy? Các câu hỏi chưa lưu sẽ bị mất.')) {
      return
    }
  }
  
  showAIModal.value = false
  aiGeneratedQuestions.value = []
  selectedAIQuestions.value = []
  aiForm.value = {
    topic: '',
    count: 5,
    level: 'medium'
  }
}

const handleFileSelect = (event) => {
  selectedFile.value = event.target.files[0]
}

const importFromWord = async () => {
  if (!selectedFile.value) return
  
  try {
    importing.value = true
    const response = await questionService.importFromWord(selectedFile.value)
    if (response.success) {
      alert(`Đã nhập ${response.data.length} câu hỏi!`)
      showImportModal.value = false
      selectedFile.value = null
      loadQuestions()
    }
  } catch (error) {
    console.error('Error importing questions:', error)
    alert('Có lỗi khi nhập câu hỏi từ Word')
  } finally {
    importing.value = false
  }
}

const closeCreateModal = () => {
  showCreateModal.value = false
  editingQuestion.value = null
  questionForm.value = {
    content: '',
    optionA: '',
    optionB: '',
    optionC: '',
    optionD: '',
    correctAnswer: 'A',
    level: 'medium'
  }
}

const getLevelText = (level) => {
  const map = { easy: 'Dễ', medium: 'Trung bình', hard: 'Khó' }
  return map[level] || level
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
  margin-bottom: 25px;
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

.header-actions {
  display: flex;
  gap: 12px;
}

.btn-action {
  padding: 10px 20px;
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.btn-action:hover {
  background: #e5e7eb;
}

.btn-primary {
  padding: 10px 20px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.btn-primary:hover {
  background: #1e3a8a;
}

.filter-section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-bottom: 25px;
  display: flex;
  gap: 15px;
}

.search-input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
}

.search-input:focus {
  outline: none;
  border-color: #1e40af;
  box-shadow: 0 0 0 3px rgba(30,64,175,0.1);
}

.filter-select {
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  min-width: 180px;
  transition: all 0.3s;
}

.filter-select:focus {
  outline: none;
  border-color: #1e40af;
}

.questions-container {
  min-height: 400px;
}

.loading-state, .empty-state {
  background: white;
  padding: 60px 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  text-align: center;
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
}

.questions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(450px, 1fr));
  gap: 20px;
}

.question-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  overflow: hidden;
  transition: all 0.3s;
}

.question-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.12);
}

.question-header {
  padding: 15px 20px;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.question-actions {
  display: flex;
  gap: 8px;
}

.btn-edit, .btn-delete {
  padding: 6px 14px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-edit {
  background: #dbeafe;
  color: #1e40af;
}

.btn-edit:hover {
  background: #bfdbfe;
}

.btn-delete {
  background: #fee2e2;
  color: #991b1b;
}

.btn-delete:hover {
  background: #fecaca;
}

.question-content {
  padding: 20px;
}

.question-text {
  font-size: 15px;
  color: #1f2937;
  font-weight: 600;
  margin: 0 0 15px 0;
  line-height: 1.6;
}

.options-list {
  margin-bottom: 15px;
}

.option-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px;
  background: #f9fafb;
  border-radius: 6px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #374151;
}

.option-label {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background: #1e40af;
  color: white;
  border-radius: 4px;
  font-weight: 700;
  font-size: 12px;
  flex-shrink: 0;
}

.correct-answer {
  padding: 10px 15px;
  background: #d1fae5;
  border-left: 3px solid #10b981;
  border-radius: 6px;
  color: #065f46;
  font-size: 14px;
  font-weight: 500;
}

.correct-answer strong {
  font-weight: 700;
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

.modal-content h2 {
  font-size: 22px;
  color: #1f2937;
  margin: 0 0 25px 0;
  font-weight: 700;
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
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 12px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #1e40af;
  box-shadow: 0 0 0 3px rgba(30,64,175,0.1);
}

.modal-large {
  max-width: 800px;
}

.import-guide {
  background: #f0f9ff;
  border: 2px solid #3b82f6;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 25px;
}

.import-guide h3 {
  color: #1e40af;
  margin: 0 0 15px 0;
  font-size: 18px;
  font-weight: 700;
}

.guide-content p {
  margin: 0 0 15px 0;
  color: #374151;
  font-size: 14px;
}

.template-example {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 15px;
  margin: 15px 0;
}

.template-example pre {
  margin: 0;
  font-family: 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
  color: #1f2937;
  white-space: pre-wrap;
}

.guide-notes {
  background: #fffbeb;
  border-left: 4px solid #f59e0b;
  padding: 15px;
  border-radius: 6px;
  margin-top: 15px;
}

.guide-notes p {
  margin: 0 0 10px 0;
  color: #92400e;
  font-weight: 600;
}

.guide-notes ul {
  margin: 0;
  padding-left: 20px;
  color: #78350f;
}

.guide-notes li {
  margin-bottom: 6px;
  font-size: 13px;
  line-height: 1.5;
}

.file-upload-area {
  margin-top: 10px;
}

.file-input {
  display: none;
}

.file-label {
  display: block;
  padding: 40px 20px;
  border: 2px dashed #cbd5e1;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  background: #f8fafc;
}

.file-label:hover {
  border-color: #3b82f6;
  background: #eff6ff;
}

.file-label span {
  font-size: 15px;
  color: #64748b;
  font-weight: 500;
}

.file-selected {
  color: #10b981 !important;
  font-weight: 600 !important;
}

.preview-header {
  background: #eff6ff;
  padding: 15px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.preview-header p {
  margin: 0;
  font-size: 15px;
  color: #1e40af;
  font-weight: 600;
}

.ai-questions-preview {
  max-height: 500px;
  overflow-y: auto;
  margin-bottom: 20px;
}

.preview-question-item {
  display: flex;
  gap: 15px;
  padding: 20px;
  margin-bottom: 15px;
  background: #f9fafb;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.preview-question-item:hover {
  background: #f3f4f6;
  border-color: #cbd5e1;
}

.preview-question-item.selected {
  background: #eff6ff;
  border-color: #3b82f6;
}

.preview-question-item input[type="checkbox"] {
  margin-top: 5px;
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.preview-question-content {
  flex: 1;
}

.preview-question-text {
  font-size: 15px;
  color: #1f2937;
  font-weight: 600;
  margin: 0 0 12px 0;
  line-height: 1.6;
}

.preview-options {
  margin-bottom: 12px;
}

.preview-option {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 8px;
  background: white;
  border-radius: 6px;
  margin-bottom: 6px;
  font-size: 14px;
  color: #374151;
}

.preview-answer {
  padding: 8px 12px;
  background: #d1fae5;
  border-left: 3px solid #10b981;
  border-radius: 6px;
  color: #065f46;
  font-size: 13px;
  font-weight: 500;
}

.preview-answer strong {
  font-weight: 700;
}
</style>
