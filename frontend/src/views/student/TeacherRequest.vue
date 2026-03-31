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
        <button class="btn-help active">Trở thành giáo viên</button>
      </div>
    </aside>
    <div class="main-content">
      <header class="top-header">
        <div class="header-left">
          <h1>Yêu Cầu Trở Thành Giáo Viên</h1>
        </div>
        <div class="header-right">
          <div class="user-menu">
            <span class="user-name">{{ authStore.user?.fullName }}</span>
            <button class="btn-logout-small" @click="handleLogout">Đăng xuất</button>
          </div>
        </div>
      </header>

      <div class="request-container">
        <div v-if="existingRequest" class="status-card">
          <div v-if="existingRequest.status === 'pending'" class="status-pending">
            <div class="status-icon pending-icon">⏳</div>
            <h2>Yêu cầu đang chờ duyệt</h2>
            <p class="status-description">Yêu cầu của bạn đang được xem xét bởi quản trị viên. Chúng tôi sẽ thông báo kết quả sớm nhất có thể.</p>
            <div class="request-details">
              <div class="detail-row">
                <span class="detail-label">Lý do yêu cầu:</span>
                <span class="detail-value">{{ existingRequest.reason }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">Thời gian gửi:</span>
                <span class="detail-value">{{ formatDate(existingRequest.createdAt) }}</span>
              </div>
            </div>
          </div>

          <div v-else-if="existingRequest.status === 'approved'" class="status-approved">
            <div class="status-icon approved-icon">✓</div>
            <h2>Yêu cầu đã được chấp nhận</h2>
            <p class="status-description">Chúc mừng! Bạn đã được nâng cấp lên giáo viên. Vui lòng đăng xuất và đăng nhập lại để cập nhật quyền hạn mới.</p>
            <button class="btn-action" @click="handleLogout">Đăng xuất ngay</button>
          </div>

          <div v-else-if="existingRequest.status === 'rejected'" class="status-rejected">
            <div class="status-icon rejected-icon">✗</div>
            <h2>Yêu cầu đã bị từ chối</h2>
            <p class="status-description">Rất tiếc, yêu cầu của bạn đã bị từ chối.</p>
            <div v-if="existingRequest.reviewNote" class="reject-reason">
              <div class="reason-label">Lý do từ chối:</div>
              <div class="reason-text">{{ existingRequest.reviewNote }}</div>
            </div>
            <button class="btn-action" @click="existingRequest = null">Gửi yêu cầu mới</button>
          </div>
        </div>

        <div v-else class="request-form-card">
          <div class="form-header">
            <h2>Gửi Yêu Cầu Trở Thành Giáo Viên</h2>
            <p>Vui lòng cung cấp thông tin chi tiết về lý do bạn muốn trở thành giáo viên trên hệ thống</p>
          </div>

          <div v-if="error" class="alert alert-error">
            {{ error }}
          </div>
          <div v-if="success" class="alert alert-success">
            {{ success }}
          </div>

          <form @submit.prevent="submitRequest" class="request-form">
            <div class="form-group">
              <label>Lý do yêu cầu <span class="required">*</span></label>
              <textarea 
                v-model="form.reason" 
                rows="8"
                placeholder="Ví dụ: Tôi là giáo viên môn Toán tại trường THPT ABC, có 5 năm kinh nghiệm giảng dạy. Tôi muốn tạo đề thi trắc nghiệm để hỗ trợ học sinh ôn tập và kiểm tra kiến thức..."
                required
              ></textarea>
              <div class="form-hint">Vui lòng mô tả chi tiết về kinh nghiệm, môn học giảng dạy và mục đích sử dụng hệ thống</div>
            </div>

            <div class="info-box">
              <div class="info-title">Lưu ý quan trọng</div>
              <ul class="info-list">
                <li>Yêu cầu sẽ được quản trị viên xem xét trong vòng 24-48 giờ</li>
                <li>Bạn cần cung cấp thông tin chính xác và đầy đủ</li>
                <li>Sau khi được duyệt, bạn sẽ có quyền tạo và quản lý đề thi</li>
              </ul>
            </div>

            <button type="submit" class="btn-submit" :disabled="loading">
              {{ loading ? 'Đang gửi yêu cầu...' : 'Gửi yêu cầu' }}
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore'
import api from '../../services/api'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({
  reason: ''
})

const existingRequest = ref(null)
const loading = ref(false)
const error = ref('')
const success = ref('')

onMounted(() => {
  checkExistingRequest()
})

const checkExistingRequest = async () => {
  try {
    const response = await api.get('/teacher-requests/my')
    if (response.success && response.data && response.data.length > 0) {
      existingRequest.value = response.data[0]
    }
  } catch (err) {
    console.error('Error checking request:', err)
  }
}

const submitRequest = async () => {
  try {
    loading.value = true
    error.value = ''
    success.value = ''

    const response = await api.post('/teacher-requests', form.value)
    
    if (response.success) {
      success.value = 'Gửi yêu cầu thành công! Vui lòng chờ quản trị viên duyệt.'
      setTimeout(() => {
        checkExistingRequest()
      }, 1500)
    }
  } catch (err) {
    error.value = err.response?.data?.message || 'Có lỗi xảy ra khi gửi yêu cầu'
  } finally {
    loading.value = false
  }
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
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

.sidebar-footer {
  padding: 20px 15px 25px;
  border-top: 1px solid rgba(255,255,255,0.1);
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

.btn-help.active {
  background: rgba(255,255,255,0.95);
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

.request-container {
  max-width: 800px;
  margin: 0 auto;
}

.status-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  padding: 50px;
  text-align: center;
}

.status-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  margin: 0 auto 25px;
}

.pending-icon {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border: 3px solid #f59e0b;
}

.approved-icon {
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
  border: 3px solid #10b981;
}

.rejected-icon {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  border: 3px solid #ef4444;
}

.status-card h2 {
  font-size: 28px;
  color: #1f2937;
  margin: 0 0 15px 0;
  font-weight: 700;
}

.status-description {
  font-size: 16px;
  color: #6b7280;
  line-height: 1.7;
  margin: 0 0 30px 0;
}

.request-details {
  background: #f9fafb;
  border-radius: 12px;
  padding: 25px;
  text-align: left;
  margin-bottom: 30px;
}

.detail-row {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 20px;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-label {
  font-size: 13px;
  color: #6b7280;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.detail-value {
  font-size: 15px;
  color: #1f2937;
  line-height: 1.6;
}

.reject-reason {
  background: #fef2f2;
  border: 2px solid #fecaca;
  border-radius: 12px;
  padding: 20px;
  text-align: left;
  margin-bottom: 30px;
}

.reason-label {
  font-size: 13px;
  color: #991b1b;
  font-weight: 700;
  margin-bottom: 10px;
  text-transform: uppercase;
}

.reason-text {
  font-size: 15px;
  color: #7f1d1d;
  line-height: 1.6;
}

.btn-action {
  padding: 14px 32px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 700;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-action:hover {
  background: #1e3a8a;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(30,64,175,0.3);
}

.request-form-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  padding: 40px;
}

.form-header {
  margin-bottom: 30px;
  padding-bottom: 25px;
  border-bottom: 2px solid #f0f0f0;
}

.form-header h2 {
  font-size: 24px;
  color: #1f2937;
  margin: 0 0 10px 0;
  font-weight: 700;
}

.form-header p {
  font-size: 15px;
  color: #6b7280;
  line-height: 1.6;
  margin: 0;
}

.alert {
  padding: 14px 16px;
  border-radius: 10px;
  margin-bottom: 20px;
  font-size: 14px;
  font-weight: 500;
}

.alert-error {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

.alert-success {
  background: #d1fae5;
  color: #065f46;
  border: 1px solid #a7f3d0;
}

.request-form {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  color: #374151;
  font-weight: 600;
}

.required {
  color: #ef4444;
}

.form-group textarea {
  padding: 14px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  font-size: 15px;
  font-family: inherit;
  resize: vertical;
  transition: all 0.3s ease;
  line-height: 1.6;
}

.form-group textarea:focus {
  outline: none;
  border-color: #1e40af;
  box-shadow: 0 0 0 3px rgba(30,64,175,0.1);
}

.form-hint {
  font-size: 13px;
  color: #9ca3af;
  font-style: italic;
}

.info-box {
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  border: 2px solid #bfdbfe;
  border-radius: 12px;
  padding: 20px;
}

.info-title {
  font-size: 15px;
  color: #1e40af;
  font-weight: 700;
  margin-bottom: 12px;
}

.info-list {
  margin: 0;
  padding-left: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-list li {
  font-size: 14px;
  color: #1e3a8a;
  line-height: 1.6;
}

.btn-submit {
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

.btn-submit:hover {
  background: #1e3a8a;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(30,64,175,0.3);
}

.btn-submit:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
}

@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
  }
  .main-content {
    margin-left: 0;
    padding: 20px;
  }
  .status-card,
  .request-form-card {
    padding: 30px 20px;
  }
}
</style>
