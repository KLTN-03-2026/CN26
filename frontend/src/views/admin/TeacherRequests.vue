<template>
  <div class="dashboard-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <img src="/logo/logo.png" alt="Logo" class="sidebar-logo" />
        <h2>Hệ Thống Thi</h2>
        <p>Quản trị viên</p>
      </div>
      <nav class="sidebar-menu">
        <a href="#" @click.prevent="$router.push('/admin')" class="menu-item">
          Trang chủ
        </a>
        <a href="#" @click.prevent="$router.push('/admin/users')" class="menu-item">
          Người dùng
        </a>
        <a href="#" @click.prevent="$router.push('/admin/teacher-requests')" class="menu-item active">
          Yêu cầu GV
        </a>
      </nav>
      <div class="sidebar-footer">
        <button class="btn-logout" @click="handleLogout">Đăng xuất</button>
      </div>
    </aside>
    <div class="main-content">
      <header class="top-header">
        <div class="header-left">
          <h1>Duyệt Yêu Cầu Giáo Viên</h1>
        </div>
      </header>

      <div class="filter-section">
        <select v-model="filterStatus" class="filter-select">
          <option value="">Tất cả trạng thái</option>
          <option value="pending">Chờ duyệt</option>
          <option value="approved">Đã duyệt</option>
          <option value="rejected">Từ chối</option>
        </select>
      </div>

      <div class="requests-container">
        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <p>Đang tải...</p>
        </div>
        <div v-else-if="filteredRequests.length === 0" class="empty-state">
          <p>Không có yêu cầu nào</p>
        </div>
        <div v-else class="requests-grid">
          <div v-for="request in filteredRequests" :key="request.id" class="request-card">
            <div class="request-header">
              <div class="user-info">
                <h3>{{ request.userName }}</h3>
                <p class="user-email">{{ request.userEmail }}</p>
              </div>
              <span :class="['status-badge', getStatusClass(request.status)]">
                {{ getStatusText(request.status) }}
              </span>
            </div>
            <div class="request-body">
              <div class="reason-section">
                <span class="label">Lý do:</span>
                <p class="reason-text">{{ request.reason }}</p>
              </div>
              <div class="meta-info">
                <span class="date">{{ formatDate(request.createdAt) }}</span>
              </div>
              <div v-if="request.reviewNote" class="review-note">
                <span class="label">Ghi chú:</span>
                <p>{{ request.reviewNote }}</p>
              </div>
            </div>
            <div v-if="request.status === 'pending'" class="request-actions">
              <button class="btn-approve" @click="showReviewModal(request, 'approved')">
                Duyệt
              </button>
              <button class="btn-reject" @click="showReviewModal(request, 'rejected')">
                Từ chối
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Review Modal -->
    <div v-if="reviewingRequest" class="modal" @click.self="closeReviewModal">
      <div class="modal-content">
        <h2>{{ reviewAction === 'approved' ? 'Duyệt Yêu Cầu' : 'Từ Chối Yêu Cầu' }}</h2>
        <p class="modal-description">
          Bạn có chắc muốn {{ reviewAction === 'approved' ? 'duyệt' : 'từ chối' }} yêu cầu của 
          <strong>{{ reviewingRequest.userName }}</strong>?
        </p>
        <div class="form-group">
          <label>Ghi chú (tùy chọn)</label>
          <textarea v-model="reviewNote" rows="3" placeholder="Nhập ghi chú..."></textarea>
        </div>
        <div class="modal-actions">
          <button class="btn-cancel" @click="closeReviewModal">Hủy</button>
          <button :class="reviewAction === 'approved' ? 'btn-confirm-approve' : 'btn-confirm-reject'" 
                  @click="submitReview">
            Xác nhận
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
import teacherRequestService from '../../services/teacherRequestService'

const router = useRouter()
const authStore = useAuthStore()

const requests = ref([])
const loading = ref(false)
const filterStatus = ref('')
const reviewingRequest = ref(null)
const reviewAction = ref('')
const reviewNote = ref('')

const filteredRequests = computed(() => {
  if (!filterStatus.value) return requests.value
  return requests.value.filter(r => r.status === filterStatus.value)
})

onMounted(() => {
  loadRequests()
})

const loadRequests = async () => {
  try {
    loading.value = true
    const response = await teacherRequestService.getAllRequests()
    if (response.success) {
      requests.value = response.data
    }
  } catch (error) {
    console.error('Error loading requests:', error)
  } finally {
    loading.value = false
  }
}

const showReviewModal = (request, action) => {
  reviewingRequest.value = request
  reviewAction.value = action
  reviewNote.value = ''
}

const closeReviewModal = () => {
  reviewingRequest.value = null
  reviewAction.value = ''
  reviewNote.value = ''
}

const submitReview = async () => {
  try {
    const response = await teacherRequestService.reviewRequest(reviewingRequest.value.id, {
      status: reviewAction.value,
      reviewNote: reviewNote.value
    })
    
    if (response.success) {
      alert('Xử lý yêu cầu thành công!')
      closeReviewModal()
      loadRequests()
    }
  } catch (error) {
    console.error('Error reviewing request:', error)
    alert('Có lỗi khi xử lý yêu cầu')
  }
}

const getStatusText = (status) => {
  const map = {
    pending: 'Chờ duyệt',
    approved: 'Đã duyệt',
    rejected: 'Từ chối'
  }
  return map[status] || status
}

const getStatusClass = (status) => {
  const map = {
    pending: 'pending',
    approved: 'approved',
    rejected: 'rejected'
  }
  return map[status] || 'pending'
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN')
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
}

.header-left h1 {
  font-size: 28px;
  color: #1f2937;
  margin: 0;
  font-weight: 800;
}

.filter-section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-bottom: 25px;
}

.filter-select {
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  min-width: 200px;
  transition: all 0.3s;
}

.filter-select:focus {
  outline: none;
  border-color: #1e40af;
}

.requests-container {
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
  font-size: 16px;
}

.requests-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.request-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  overflow: hidden;
  transition: all 0.3s;
}

.request-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.12);
}

.request-header {
  padding: 20px;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: start;
}

.user-info h3 {
  font-size: 18px;
  color: #1f2937;
  margin: 0 0 5px 0;
  font-weight: 700;
}

.user-email {
  font-size: 13px;
  color: #6b7280;
  margin: 0;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.pending {
  background: #fef3c7;
  color: #92400e;
}

.status-badge.approved {
  background: #d1fae5;
  color: #065f46;
}

.status-badge.rejected {
  background: #fee2e2;
  color: #991b1b;
}

.request-body {
  padding: 20px;
}

.reason-section {
  margin-bottom: 15px;
}

.label {
  font-size: 13px;
  color: #6b7280;
  font-weight: 600;
  display: block;
  margin-bottom: 5px;
}

.reason-text {
  font-size: 14px;
  color: #1f2937;
  line-height: 1.6;
  margin: 0;
}

.meta-info {
  margin-bottom: 15px;
}

.date {
  font-size: 13px;
  color: #6b7280;
}

.review-note {
  padding: 12px;
  background: #fffbeb;
  border-left: 3px solid #f59e0b;
  border-radius: 6px;
}

.review-note p {
  font-size: 13px;
  color: #78350f;
  margin: 5px 0 0 0;
  line-height: 1.5;
}

.request-actions {
  padding: 15px 20px;
  background: #f9fafb;
  border-top: 1px solid #e5e7eb;
  display: flex;
  gap: 10px;
}

.btn-approve, .btn-reject {
  flex: 1;
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.btn-approve {
  background: #10b981;
  color: white;
}

.btn-approve:hover {
  background: #059669;
}

.btn-reject {
  background: #ef4444;
  color: white;
}

.btn-reject:hover {
  background: #dc2626;
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
  max-width: 500px;
  width: 90%;
}

.modal-content h2 {
  font-size: 22px;
  color: #1f2937;
  margin: 0 0 15px 0;
  font-weight: 700;
}

.modal-description {
  font-size: 15px;
  color: #6b7280;
  margin: 0 0 20px 0;
  line-height: 1.6;
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

.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
  font-family: inherit;
}

.form-group textarea:focus {
  outline: none;
  border-color: #1e40af;
  box-shadow: 0 0 0 3px rgba(30,64,175,0.1);
}

.modal-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.btn-cancel {
  padding: 10px 20px;
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

.btn-confirm-approve {
  padding: 10px 20px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-confirm-approve:hover {
  background: #059669;
}

.btn-confirm-reject {
  padding: 10px 20px;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-confirm-reject:hover {
  background: #dc2626;
}

@media (max-width: 768px) {
  .requests-grid {
    grid-template-columns: 1fr;
  }
}
</style>
