<template>
  <TeacherLayout activeMenu="questions">
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
      <LoadingSpinner v-if="loading" />

      <EmptyState 
        v-else-if="filteredQuestions.length === 0"
        message="Chưa có câu hỏi nào"
        actionText="Tạo câu hỏi đầu tiên"
        @action="showCreateModal = true"
      />

      <div v-else class="questions-grid">
        <QuestionCard
          v-for="question in filteredQuestions"
          :key="question.id"
          :question="question"
          @edit="editQuestion"
          @delete="deleteQuestion"
        />
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <AppModal v-model="showCreateModal" :title="editingQuestion ? 'Sửa Câu Hỏi' : 'Tạo Câu Hỏi Mới'">
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
        <div class="modal-actions">
          <button type="button" class="btn-cancel" @click="closeCreateModal">Hủy</button>
          <button type="submit" class="btn-save">Lưu</button>
        </div>
      </form>
    </AppModal>

    <!-- AI Modal -->
    <AppModal v-model="showAIModal" title="Tạo Câu Hỏi Bằng AI" size="large">
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
          <div class="modal-actions">
            <button type="button" class="btn-cancel" @click="closeAIModal">Hủy</button>
            <button type="submit" class="btn-save" :disabled="generatingAI">
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
              <p class="preview-question-text">
                <MathText :text="question.content" :auto-wrap="true" />
              </p>
              <div class="preview-options">
                <div class="preview-option">
                  <span class="option-label">A</span>
                  <MathText :text="question.optionA" :auto-wrap="true" />
                </div>
                <div class="preview-option">
                  <span class="option-label">B</span>
                  <MathText :text="question.optionB" :auto-wrap="true" />
                </div>
                <div class="preview-option">
                  <span class="option-label">C</span>
                  <MathText :text="question.optionC" :auto-wrap="true" />
                </div>
                <div class="preview-option">
                  <span class="option-label">D</span>
                  <MathText :text="question.optionD" :auto-wrap="true" />
                </div>
              </div>
              <div class="preview-answer">
                Đáp án đúng: <strong>{{ question.correctAnswer }}</strong>
              </div>
            </div>
          </label>
        </div>

        <div class="modal-actions">
          <button class="btn-cancel" @click="closeAIModal">Hủy</button>
          <button class="btn-save" @click="saveSelectedAIQuestions" :disabled="selectedAIQuestions.length === 0">
            Lưu {{ selectedAIQuestions.length }} câu đã chọn
          </button>
        </div>
      </div>
    </AppModal>

    <!-- Import Modal -->
    <AppModal v-model="showImportModal" title="Nhập Câu Hỏi Từ Word" size="large">
      <div v-if="!importedQuestions.length">
        <div class="import-guide">
          <h3>Hướng dẫn định dạng file Word</h3>
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
        
        <div class="modal-actions">
          <button class="btn-cancel" @click="closeImportModal">Hủy</button>
          <button class="btn-save" @click="importFromWord" :disabled="!selectedFile || importing">
            {{ importing ? 'Đang nhập...' : 'Nhập câu hỏi' }}
          </button>
        </div>
      </div>

      <div v-else>
        <div class="preview-header">
          <p>Đã nhập {{ importedQuestions.length }} câu hỏi. Chọn câu hỏi muốn lưu:</p>
          <div style="display: flex; gap: 10px;">
            <button class="btn-secondary" @click="selectAllImport">Chọn tất cả</button>
            <button class="btn-secondary" @click="deselectAllImport">Bỏ chọn tất cả</button>
          </div>
        </div>

        <div class="ai-questions-preview">
          <label v-for="(question, index) in importedQuestions" :key="index" 
                 class="preview-question-item"
                 :class="{ 'selected': selectedImportQuestions.includes(index) }">
            <input type="checkbox" :value="index" v-model="selectedImportQuestions">
            <div class="preview-question-content">
              <p class="preview-question-text">
                <MathText :text="question.content" :auto-wrap="true" />
              </p>
              <div class="preview-options">
                <div class="preview-option">
                  <span class="option-label">A</span>
                  <MathText :text="question.optionA" :auto-wrap="true" />
                </div>
                <div class="preview-option">
                  <span class="option-label">B</span>
                  <MathText :text="question.optionB" :auto-wrap="true" />
                </div>
                <div class="preview-option">
                  <span class="option-label">C</span>
                  <MathText :text="question.optionC" :auto-wrap="true" />
                </div>
                <div class="preview-option">
                  <span class="option-label">D</span>
                  <MathText :text="question.optionD" :auto-wrap="true" />
                </div>
              </div>
              <div class="preview-answer">
                Đáp án đúng: <strong>{{ question.correctAnswer }}</strong>
              </div>
            </div>
          </label>
        </div>

        <div class="modal-actions">
          <button class="btn-cancel" @click="closeImportModal">Hủy</button>
          <button class="btn-save" @click="saveSelectedImportQuestions" :disabled="selectedImportQuestions.length === 0">
            Lưu {{ selectedImportQuestions.length }} câu đã chọn
          </button>
        </div>
      </div>
    </AppModal>
  </TeacherLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useFormatters } from '../../composables/useFormatters'
import questionService from '../../services/questionService'
import TeacherLayout from '../../components/layouts/TeacherLayout.vue'
import LoadingSpinner from '../../components/common/LoadingSpinner.vue'
import EmptyState from '../../components/common/EmptyState.vue'
import AppModal from '../../components/common/AppModal.vue'
import QuestionCard from '../../components/teacher/QuestionCard.vue'
import MathText from '../../components/common/MathText.vue'

const { getLevelText } = useFormatters()

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
const importedQuestions = ref([])
const selectedImportQuestions = ref([])

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
    const response = await questionService.parseWordFile(selectedFile.value)
    if (response.success) {
      importedQuestions.value = response.data
      selectedImportQuestions.value = response.data.map((_, index) => index)
      selectedFile.value = null
    }
  } catch (error) {
    console.error('Error importing questions:', error)
    alert('Có lỗi khi nhập câu hỏi từ Word')
  } finally {
    importing.value = false
  }
}

const selectAllImport = () => {
  selectedImportQuestions.value = importedQuestions.value.map((_, index) => index)
}

const deselectAllImport = () => {
  selectedImportQuestions.value = []
}

const saveSelectedImportQuestions = async () => {
  try {
    const questionsToSave = selectedImportQuestions.value.map(index => importedQuestions.value[index])
    
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
    closeImportModal()
    await loadQuestions()
  } catch (error) {
    console.error('Error saving imported questions:', error)
    alert('Có lỗi khi lưu câu hỏi')
  }
}

const closeImportModal = () => {
  if (importedQuestions.value.length > 0) {
    if (!confirm('Bạn có chắc muốn hủy? Các câu hỏi chưa lưu sẽ bị mất.')) {
      return
    }
  }
  
  showImportModal.value = false
  importedQuestions.value = []
  selectedImportQuestions.value = []
  selectedFile.value = null
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
</script>

<style scoped>
.header-actions { display: flex; gap: 12px; }
.btn-action { padding: 10px 20px; background: #f3f4f6; color: #374151; border: 1px solid #e5e7eb; border-radius: 8px; font-weight: 600; cursor: pointer; transition: all 0.3s; font-size: 14px; }
.btn-action:hover { background: #e5e7eb; }
.btn-primary { padding: 10px 20px; background: #1e40af; color: white; border: none; border-radius: 8px; font-weight: 600; cursor: pointer; transition: all 0.3s; font-size: 14px; }
.btn-primary:hover { background: #1e3a8a; }
.filter-section { background: white; padding: 20px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); margin-bottom: 25px; display: flex; gap: 15px; }
.questions-container { min-height: 400px; }
.questions-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(450px, 1fr)); gap: 20px; }
.modal-actions { display: flex; gap: 12px; justify-content: flex-end; margin-top: 25px; }
.btn-cancel { padding: 12px 24px; background: #f3f4f6; color: #374151; border: none; border-radius: 8px; font-weight: 600; cursor: pointer; transition: all 0.3s; }
.btn-cancel:hover { background: #e5e7eb; }
.btn-save { padding: 12px 24px; background: #1e40af; color: white; border: none; border-radius: 8px; font-weight: 600; cursor: pointer; transition: all 0.3s; }
.btn-save:hover:not(:disabled) { background: #1e3a8a; }
.btn-save:disabled { background: #9ca3af; cursor: not-allowed; }
.btn-secondary { padding: 8px 16px; background: #f3f4f6; color: #374151; border: none; border-radius: 6px; font-weight: 600; cursor: pointer; font-size: 13px; }
.import-guide { background: #f0f9ff; border: 2px solid #3b82f6; border-radius: 12px; padding: 20px; margin-bottom: 25px; }
.import-guide h3 { color: #1e40af; margin: 0 0 15px 0; font-size: 18px; font-weight: 700; }
.guide-content p { margin: 0 0 15px 0; color: #374151; font-size: 14px; }
.template-example { background: white; border: 1px solid #e5e7eb; border-radius: 8px; padding: 15px; margin: 15px 0; }
.template-example pre { margin: 0; font-family: 'Courier New', monospace; font-size: 13px; line-height: 1.6; color: #1f2937; white-space: pre-wrap; }
.guide-notes { background: #fffbeb; border-left: 4px solid #f59e0b; padding: 15px; border-radius: 6px; margin-top: 15px; }
.guide-notes p { margin: 0 0 10px 0; color: #92400e; font-weight: 600; }
.guide-notes ul { margin: 0; padding-left: 20px; color: #78350f; }
.guide-notes li { margin-bottom: 6px; font-size: 13px; line-height: 1.5; }
.file-upload-area { margin-top: 10px; }
.file-input { display: none; }
.file-label { display: block; padding: 40px 20px; border: 2px dashed #cbd5e1; border-radius: 12px; text-align: center; cursor: pointer; transition: all 0.3s; background: #f8fafc; }
.file-label:hover { border-color: #3b82f6; background: #eff6ff; }
.file-label span { font-size: 15px; color: #64748b; font-weight: 500; }
.file-selected { color: #10b981 !important; font-weight: 600 !important; }
.preview-header { background: #eff6ff; padding: 15px 20px; border-radius: 8px; margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center; }
.preview-header p { margin: 0; font-size: 15px; color: #1e40af; font-weight: 600; }
.ai-questions-preview { max-height: 500px; overflow-y: auto; margin-bottom: 20px; }
.preview-question-item { display: flex; gap: 15px; padding: 20px; margin-bottom: 15px; background: #f9fafb; border: 2px solid #e5e7eb; border-radius: 10px; cursor: pointer; transition: all 0.3s; }
.preview-question-item:hover { background: #f3f4f6; border-color: #cbd5e1; }
.preview-question-item.selected { background: #eff6ff; border-color: #3b82f6; }
.preview-question-item input[type="checkbox"] { margin-top: 5px; width: 18px; height: 18px; cursor: pointer; }
.preview-question-content { flex: 1; }
.preview-question-text { font-size: 15px; color: #1f2937; font-weight: 600; margin: 0 0 12px 0; line-height: 1.6; }
.preview-options { margin-bottom: 12px; }
.preview-option { display: flex; align-items: flex-start; gap: 8px; padding: 8px; background: white; border-radius: 6px; margin-bottom: 6px; font-size: 14px; color: #374151; }
.preview-answer { padding: 8px 12px; background: #d1fae5; border-left: 3px solid #10b981; border-radius: 6px; color: #065f46; font-size: 13px; font-weight: 500; }
.preview-answer strong { font-weight: 700; }
</style>
