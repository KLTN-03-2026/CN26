<template>
  <StudentLayout activeMenu="home">
    <header class="top-header">
      <div class="header-left">
        <h1>Yêu Cầu Trở Thành Giáo Viên</h1>
      </div>
    </header>

    <div class="request-container">
      <div class="request-main">
        <div class="info-card">
          <h2>Quyền Lợi Của Giáo Viên</h2>
          <ul class="benefits-list">
            <li>Tạo và quản lý câu hỏi riêng</li>
            <li>Tạo đề thi từ ngân hàng câu hỏi</li>
            <li>Xem thống kê kết quả học sinh</li>
            <li>Nhập câu hỏi từ file Word</li>
            <li>Tạo câu hỏi bằng AI</li>
          </ul>
        </div>

        <div v-if="existingRequest" class="status-card">
          <h2>Trạng Thái Yêu Cầu</h2>
          <div class="status-content">
            <span :class="['status-badge-large', getStatusClass(existingRequest.status)]">
              {{ getStatusText(existingRequest.status) }}
            </span>
            <p class="status-date">Ngày gửi: {{ existingRequest.createdAt ? formatDate(existingRequest.createdAt) : 'Chưa có thông tin' }}</p>
            <div v-if="existingRequest.reviewNote" class="review-note">
              <strong>Ghi chú:</strong> {{ existingRequest.reviewNote }}
            </div>
          </div>
        </div>

        <div v-else class="request-form-card">
          <h2>Gửi Yêu Cầu</h2>
          <form @submit.prevent="submitRequest">
            <div class="form-group">
              <label>Lý do muốn trở thành giáo viên *</label>
              <textarea v-model="requestForm.reason" required rows="4" 
                        placeholder="Chia sẻ lý do bạn muốn trở thành giáo viên..."></textarea>
            </div>
            <div class="form-group">
              <label>Bằng cấp, chứng chỉ</label>
              <textarea v-model="requestForm.qualifications" rows="3"
                        placeholder="Liệt kê các bằng cấp, chứng chỉ liên quan (nếu có)"></textarea>
            </div>
            <button type="submit" class="btn-submit" :disabled="submitting">
              {{ submitting ? 'Đang gửi...' : 'Gửi yêu cầu' }}
            </button>
          </form>
        </div>
      </div>

      <div class="request-sidebar">
        <div class="guide-card">
          <h3>Hướng Dẫn</h3>
          <ol>
            <li>Điền đầy đủ thông tin vào form</li>
            <li>Gửi yêu cầu và chờ admin duyệt</li>
            <li>Kiểm tra trạng thái yêu cầu tại trang này</li>
            <li>Sau khi được duyệt, bạn sẽ có quyền giáo viên</li>
          </ol>
        </div>

        <div class="note-card">
          <h3>Lưu Ý</h3>
          <p>Yêu cầu sẽ được admin xem xét và phê duyệt trong vòng 1-3 ngày làm việc.</p>
        </div>
      </div>
    </div>
  </StudentLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useFormatters } from '../../composables/useFormatters'
import teacherRequestService from '../../services/teacherRequestService'
import StudentLayout from '../../components/layouts/StudentLayout.vue'

const router = useRouter()
const { formatDate, getStatusText } = useFormatters()

const existingRequest = ref(null)
const submitting = ref(false)

const requestForm = ref({
  reason: '',
  qualifications: ''
})

onMounted(() => {
  checkExistingRequest()
})

const checkExistingRequest = async () => {
  try {
    const response = await teacherRequestService.getMyRequest()
    if (response.success && response.data && response.data.length > 0) {
      // Sắp xếp theo createdAt giảm dần để lấy request mới nhất
      const sortedRequests = response.data.sort((a, b) => {
        return new Date(b.createdAt) - new Date(a.createdAt)
      })
      const latestRequest = sortedRequests[0]
      
      // Chỉ hiển thị status nếu là pending hoặc approved
      // Nếu rejected thì cho phép gửi lại
      if (latestRequest.status === 'pending' || latestRequest.status === 'approved') {
        existingRequest.value = latestRequest
      }
    }
  } catch (error) {
    console.error('Error checking request:', error)
  }
}

const submitRequest = async () => {
  try {
    submitting.value = true
    const response = await teacherRequestService.createRequest(requestForm.value)
    if (response.success) {
      alert('Gửi yêu cầu thành công! Vui lòng chờ admin phê duyệt.')
      await checkExistingRequest()
    }
  } catch (error) {
    console.error('Error submitting request:', error)
    alert(error.response?.data?.message || 'Có lỗi khi gửi yêu cầu')
  } finally {
    submitting.value = false
  }
}

const getStatusClass = (status) => {
  return status
}
</script>

<style scoped>
.request-container {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 25px;
}

.request-main {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.info-card,
.status-card,
.request-form-card {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.info-card h2,
.status-card h2,
.request-form-card h2 {
  font-size: 20px;
  color: #1f2937;
  margin: 0 0 20px 0;
  font-weight: 700;
}

.benefits-list {
  margin: 0;
  padding-left: 20px;
  color: #374151;
}

.benefits-list li {
  margin-bottom: 12px;
  font-size: 15px;
  line-height: 1.6;
}

.status-content {
  text-align: center;
}

.status-badge-large {
  display: inline-block;
  padding: 12px 24px;
  border-radius: 20px;
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 15px;
}

.status-badge-large.pending {
  background: #fef3c7;
  color: #92400e;
}

.status-badge-large.approved {
  background: #d1fae5;
  color: #065f46;
}

.status-badge-large.rejected {
  background: #fee2e2;
  color: #991b1b;
}

.status-date {
  color: #6b7280;
  font-size: 14px;
  margin: 10px 0;
}

.review-note {
  margin-top: 20px;
  padding: 15px;
  background: #f9fafb;
  border-left: 3px solid #1e40af;
  border-radius: 6px;
  text-align: left;
  font-size: 14px;
  color: #374151;
}

.review-note strong {
  color: #1f2937;
}

.btn-submit {
  width: 100%;
  padding: 14px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-submit:hover:not(:disabled) {
  background: #1e3a8a;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(30,64,175,0.3);
}

.btn-submit:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.request-sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.guide-card,
.note-card {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.guide-card h3,
.note-card h3 {
  font-size: 16px;
  color: #1f2937;
  margin: 0 0 15px 0;
  font-weight: 700;
}

.guide-card ol {
  margin: 0;
  padding-left: 20px;
  color: #374151;
}

.guide-card li {
  margin-bottom: 10px;
  font-size: 14px;
  line-height: 1.6;
}

.note-card p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
  line-height: 1.6;
}

@media (max-width: 1024px) {
  .request-container {
    grid-template-columns: 1fr;
  }
  .request-sidebar {
    order: -1;
  }
}
</style>
